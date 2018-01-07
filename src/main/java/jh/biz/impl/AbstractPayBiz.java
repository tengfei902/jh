package jh.biz.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hf.base.contants.CodeManager;
import hf.base.enums.OperateType;
import hf.base.enums.PayRequestStatus;
import hf.base.enums.TradeType;
import hf.base.exceptions.BizFailException;
import hf.base.utils.Utils;
import jh.biz.PayBiz;
import jh.biz.service.CacheService;
import jh.biz.service.PayService;
import jh.dao.local.*;
import jh.dao.remote.CallBackClient;
import jh.dao.remote.PayClient;
import jh.model.po.PayMsgRecord;
import jh.model.po.PayRequest;
import jh.model.po.UserGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractPayBiz implements PayBiz {

    protected Logger logger = LoggerFactory.getLogger(AbstractPayBiz.class);
    @Autowired
    protected PayRequestDao payRequestDao;
    @Autowired
    private PayService payService;
    @Autowired
    protected PayMsgRecordDao payMsgRecordDao;
    @Autowired
    protected CacheService cacheService;
    @Autowired
    protected UserChannelDao userChannelDao;
    @Autowired
    protected ChannelDao channelDao;
    @Autowired
    private CallBackClient callBackClient;
    @Autowired
    private UserGroupDao userGroupDao;

    private static final String[] needFields = {"service","version","merchant_no","total","name","remark","out_trade_no",
            "create_ip","nonce_str","sign","sign_type"};

    abstract PayClient getPayClient();
    abstract void handlePayResult(PayRequest payRequest,PayMsgRecord payMsgRecord,Map<String, Object> payResult);

    @Override
    public void pay(PayRequest payRequest) {
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());

        switch (PayRequestStatus.parse(payRequest.getStatus())) {
            case NEW:
                payService.saveOprLog(payRequest);
                doRemoteCall(payRequest);
                break;
            case OPR_GENERATED:
                doRemoteCall(payRequest);
                break;
            case PROCESSING:
                break;
            case PAY_FAILED:
                //失败
                break;
            case OPR_SUCCESS:
                //收到回复消息
                notice(payRequest);
                break;
            case PAY_SUCCESS:
                notice(payRequest);
                break;
            case USER_NOTIFIED:

                break;
            case OPR_FINISHED:
                break;
        }
    }

    @Override
    public void doRemoteCall(PayRequest payRequest) {
        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(payRequest.getOutTradeNo(), OperateType.HF_CLIENT.getValue(), TradeType.PAY.getValue());
        Map<String,Object> params = new Gson().fromJson(payMsgRecord.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
        Map<String,Object> result = getPayClient().unifiedorder(params);
        handlePayResult(payRequest,payMsgRecord,result);
    }

    @Override
    public void paySuccess(String outTradeNo) {
        payService.paySuccess(outTradeNo);
    }

    @Override
    public void payFailed(String outTradeNo) {

    }

//    @Override
//    public void finishPay() {
//        List<PayRequest> unfinishedRequests = payRequestDao.selectUnfinishedList(DateUtils.addMinutes(new Date(),-0));
//        unfinishedRequests.parallelStream().forEach(payRequest -> {
//            Map<String,String> result = ysClient.getPayResult(payRequest.getMchId(),payRequest.getOutTradeNo());
//            if(org.apache.commons.collections.MapUtils.isEmpty(result)) {
//                return;
//            }
//            if(!StringUtils.equals(result.get("out_trade_no"),payRequest.getOutTradeNo())) {
//                return;
//            }
//            if(StringUtils.equalsIgnoreCase(result.get("trade_state"),"SUCCESS")) {
//                payService.paySuccess(payRequest.getOutTradeNo());
//            }
//        });
//    }

//    @Override
//    public void promote() {
//        List<PayRequest> list = payRequestDao.selectWaitingPromote();
//        list.parallelStream().forEach(payRequest ->
//                payService.payPromote(payRequest.getOutTradeNo())
//        );
//    }

    protected void checkField(Map<String,Object> map) {
        for(String field:needFields) {
            if(Objects.isNull(map.get(field)) || StringUtils.isBlank(map.get(field).toString()) || StringUtils.equalsIgnoreCase("null",map.get(field).toString())) {
                throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,String.format("参数错误:%s",field));
            }
        }
    }

    @Override
    public void notice(PayRequest payRequest) {
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        if(payRequest.getStatus() != PayRequestStatus.OPR_SUCCESS.getValue()) {
            return;
        }

        UserGroup userGroup = userGroupDao.selectByGroupNo(payRequest.getMchId());
        String url = userGroup.getCallbackUrl();
        if(StringUtils.isEmpty(url)) {
            logger.warn(String.format("callback url is null,%s",url));
            if(StringUtils.equalsIgnoreCase(payRequest.getPayResult(),"0")) {
                payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_SUCCESS.getValue(),PayRequestStatus.USER_NOTIFIED.getValue());
            } else {
                payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_SUCCESS.getValue(),PayRequestStatus.OPR_FINISHED.getValue());
            }
            return;
        }

        Map<String,Object> resutMap = new HashMap<>();

        if(StringUtils.equalsIgnoreCase(payRequest.getPayResult(),"0")) {
            //code 0成功 99失败
            resutMap.put("errcode","0");
            //msg
            resutMap.put("message","支付成功");

            resutMap.put("no",payRequest.getId());
            //out_trade_no
            resutMap.put("out_trade_no",payRequest.getOutTradeNo().split("_")[1]);
            //mch_id
            resutMap.put("merchant_no",payRequest.getMchId());
            //total
            resutMap.put("total",payRequest.getTotalFee());
            //fee
            resutMap.put("fee",payRequest.getFee());
            //trade_type 1:收单 2:撤销 3:退款
            resutMap.put("trade_type","1");
            //sign_type
            resutMap.put("sign_type","MD5");
            String sign = Utils.encrypt(resutMap,userGroup.getCipherCode());
            resutMap.put("sign",sign);

            boolean result = callBackClient.post(url,resutMap);
            if(result) {
                payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_SUCCESS.getValue(),PayRequestStatus.USER_NOTIFIED.getValue());
            }
        } else {
            resutMap.put("code","99");
            resutMap.put("msg","支付失败");
            resutMap.put("out_trade_no",payRequest.getOutTradeNo());
            resutMap.put("mch_id",payRequest.getMchId());

            boolean result = callBackClient.post(url,resutMap);
            if(result) {
                payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_SUCCESS.getValue(),PayRequestStatus.OPR_FINISHED.getValue());
            }
        }

    }
}
