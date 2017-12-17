package jh.biz.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hf.base.enums.OperateType;
import hf.base.enums.PayRequestStatus;
import hf.base.enums.TradeType;
import hf.base.exceptions.BizFailException;
import jh.biz.PayBiz;
import jh.biz.service.PayService;
import jh.dao.local.PayMsgRecordDao;
import jh.dao.local.PayRequestDao;
import jh.dao.remote.PayClient;
import jh.model.po.PayMsgRecord;
import jh.model.po.PayRequest;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractPayBiz implements PayBiz {

    private Logger logger = LoggerFactory.getLogger(AbstractPayBiz.class);
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PayService payService;
    @Autowired
    private PayMsgRecordDao payMsgRecordDao;

    abstract PayClient getPayClient();

    @Override
    public void pay(PayRequest payRequest) {
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());

        switch (PayRequestStatus.parse(payRequest.getStatus())) {
            case NEW:
                doRemoteCall(payRequest);
                break;
            case OPR_GENERATED:
                doRemoteCall(payRequest);
                break;
            case REMOTE_CALL_FINISHED:
                break;
            case PAY_SUCCESS:
                break;
            case PAY_FAILED:
                break;
            case OPR_SUCCESS:
                break;
            case OPR_FINISHED:
                break;
        }
    }

    private void doRemoteCall(PayRequest payRequest) {
        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(payRequest.getOutTradeNo(), OperateType.HF_CLIENT.getValue(), TradeType.PAY.getValue());
        Map<String,Object> params = new Gson().fromJson(payMsgRecord.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
        Map<String,Object> result = getPayClient().refundorder(params);
        if(MapUtils.isEmpty(result)) {
            throw new BizFailException();
        }

        if(Objects.isNull(result.get("errcode"))) {
            throw new BizFailException();
        }

        PayMsgRecord remoteReturnMsgRecord = new PayMsgRecord(payMsgRecord.getOutTradeNo(),payMsgRecord.getMerchantNo(),payMsgRecord.getService(),OperateType.CLIENT_HF.getValue(), TradeType.PAY.getValue(),result);
        try {
            payMsgRecordDao.insertSelective(remoteReturnMsgRecord);
        } catch (DuplicateKeyException e) {
            logger.warn(String.format("pay msg already exist,%s,%s,%s",remoteReturnMsgRecord.getOutTradeNo(),remoteReturnMsgRecord.getTradeType(),remoteReturnMsgRecord.getOperateType()));
        }

        PayMsgRecord hfToUserMsgRecord = new PayMsgRecord(payMsgRecord.getOutTradeNo(),payMsgRecord.getMerchantNo(),payMsgRecord.getService(),OperateType.HF_USER.getValue(),TradeType.PAY.getValue(),result);
        try {
            payMsgRecordDao.insertSelective(hfToUserMsgRecord);
        } catch (DuplicateKeyException e ){
            logger.warn(String.format("pay msg already exists,%s,%s,%s",hfToUserMsgRecord.getOutTradeNo(),hfToUserMsgRecord.getTradeType(),hfToUserMsgRecord.getOperateType()));
        }

        String errcode = String.valueOf(result.get("errcode"));
        String message = String.valueOf(result.get("message"));
        if("0".equalsIgnoreCase(errcode) || "4".equalsIgnoreCase(errcode)) {
            payService.saveOprLog(payRequest);
        } else {
            payRequestDao.updateFailed(payRequest.getId(),PayRequestStatus.NEW.getValue(),message);
        }
    }

    @Override
    public void paySuccess(String outTradeNo) {
        payService.paySuccess(outTradeNo);
    }

    @Override
    public void payFailed(String outTradeNo) {

    }

    @Override
    public void promote(String outTradeNo) {

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
}
