package jh.api;

import com.google.gson.Gson;
import hf.base.contants.CodeManager;
import hf.base.enums.ChannelProvider;
import hf.base.enums.PayRequestStatus;
import hf.base.exceptions.BizFailException;
import hf.base.utils.Utils;
import jh.biz.PayBiz;
import jh.biz.service.CacheService;
import jh.biz.service.PayService;
import jh.biz.service.TradeBizFactory;
import jh.biz.trade.TradeBiz;
import jh.dao.local.*;
import jh.model.po.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/pay")
public class PayApi {
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    @Qualifier("ysPayBiz")
    private PayBiz payBiz;
    @Autowired
    @Qualifier("fxtPayBiz")
    private PayBiz fxtPayBiz;
    @Autowired
    @Qualifier("wwTradeBiz")
    private TradeBiz wwTradeBiz;
    @Autowired
    @Qualifier("ysTradeBiz")
    private TradeBiz ysTradeBiz;
    @Autowired
    @Qualifier("fxtTradeBiz")
    private TradeBiz fxtTradeBiz;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private TradeBizFactory tradeBizFactory;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private PayService payService;

    protected Logger logger = LoggerFactory.getLogger(PayApi.class);

    @RequestMapping(value = "/unifiedorder",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody String unifiedorder(@RequestBody Map<String,Object> params) {
        try {
            logger.info("new pay request :" +new Gson().toJson(params));
            String mchId = String.valueOf(params.get("merchant_no"));
            String service = String.valueOf(params.get("service"));
            TradeBiz tradeBiz = tradeBizFactory.getTradeBiz(mchId,service);
            Map<String,Object> resultMap = tradeBiz.pay(params);
            return new Gson().toJson(resultMap);
        } catch (BizFailException e) {
            Map<String,Object> result = hf.base.utils.MapUtils.buildMap("errcode",e.getCode(),"message",e.getMessage());
            return new Gson().toJson(result);
        } catch (Exception e) {
            Map<String,Object> result = hf.base.utils.MapUtils.buildMap("errcode",CodeManager.FAILED);
            return new Gson().toJson(result);
        }
    }
//
//    @RequestMapping(value = "/unifiedorder",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
//    public @ResponseBody String unifiedorder(@RequestBody Map<String,Object> params) {
//        if(MapUtils.isEmpty(params)) {
//            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode", CodeManager.PARAM_CHECK_FAILED,"message","service不能为空"));
//        }
//        PayBiz payBiz = payBizCollection.getPayBiz(String.valueOf(params.get("service")));
//        if(Objects.isNull(payBiz)) {
//            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode",CodeManager.PARAM_CHECK_FAILED,"message","service错误"));
//        }
//        try {
//            payBiz.checkParam(params);
//        } catch (BizFailException e) {
//            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode",e.getCode(),"message",e.getMessage()));
//        }
//
//        String out_trade_no = String.valueOf(params.get("out_trade_no"));
//        String mchId = String.valueOf(params.get("merchant_no"));
//        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(String.format("%s_%s",mchId,out_trade_no), OperateType.HF_USER.getValue(), TradeType.PAY.getValue());
//        if(!Objects.isNull(payMsgRecord)) {
//            return payMsgRecord.getMsgBody();
//        }
//
//        try {
//            payBiz.savePayRequest(params);
//        } catch (BizFailException e) {
//            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode",e.getCode(),"message",e.getMessage()));
//        }
//        String outTradeNo = String.format("%s_%s",mchId,out_trade_no);
//        try {
//            payFlow.invoke(outTradeNo,PayRequestStatus.PROCESSING);
//            payMsgRecord = payMsgRecordDao.selectByTradeNo(outTradeNo, OperateType.HF_USER.getValue(), TradeType.PAY.getValue());
//            return payMsgRecord.getMsgBody();
//
//        } catch (BizFailException e) {
//            payMsgRecord = payMsgRecordDao.selectByTradeNo(outTradeNo, OperateType.HF_USER.getValue(), TradeType.PAY.getValue());
//            if(!Objects.isNull(payMsgRecord)) {
//                return payMsgRecord.getMsgBody();
//            }
//            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode",e.getCode(),"message",e.getMessage()));
//        }
//    }

    /**
     * 友收宝支付回调
     * @param params
     * @return
     */
    @RequestMapping(value = "/ys/payCallBack",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody String payCallBack(@RequestBody Map<String,Object> params) {
        try {
            payBiz.checkCallBack(params);
        } catch (BizFailException e) {
            logger.warn(e.getMessage());
            return CodeManager.FAILED;
        }

        try {
            payBiz.finishPay(params);
        } catch (BizFailException e) {
            logger.warn(e.getMessage());
        }

        String mchId = String.valueOf(params.get("merchant_no"));
        String out_trade_no = String.valueOf(params.get("out_trade_no"));
        String outTradeNo = String.format("%s_%s",mchId,out_trade_no);
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        try {
            ysTradeBiz.notice(payRequest);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return CodeManager.SUCCESS;
    }

    @RequestMapping(value = "/fxt/payCallBack",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody String fxtCallBack(@RequestBody Map<String,Object> params) {
        try {
            fxtPayBiz.checkCallBack(params);
        } catch (BizFailException e) {
            logger.warn(e.getMessage());
            return CodeManager.FAILED;
        }

        try {
            fxtPayBiz.finishPay(params);
        } catch (BizFailException e) {
            logger.warn(e.getMessage());
            return CodeManager.FAILED;
        }

        String out_trade_no = String.valueOf(params.get("out_trade_no"));
        PayRequest payRequest = payRequestDao.selectByTradeNo(out_trade_no);
        try {
            fxtTradeBiz.notice(payRequest);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return CodeManager.SUCCESS;
    }

    @RequestMapping(value = "/ww/payCallBack",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody String wwCallBack(@RequestBody Map<String,String> params) {
        String result = wwTradeBiz.handleCallBack(params);
        String tradeNo = params.get("orderNum");
        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);
        wwTradeBiz.notice(payRequest);
        return result;
    }

    @RequestMapping(value = "/queryOrder",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody String queryOrder(@RequestBody Map<String,Object> params) {
        String version = String.valueOf(params.get("version"));
        String merchant_no = String.valueOf(params.get("merchant _no"));
        String out_trade_no = String.valueOf(params.get("out_trade_no"));
        String nonce_str = String.valueOf(params.get("nonce_str"));
        String sign_type = String.valueOf(params.get("sign_type"));
        String sign = String.valueOf(params.get("sign"));

        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isEmpty(merchant_no) || StringUtils.isEmpty(out_trade_no)) {
            map.put("errcode",2);
            map.put("message","merchant_no或out_trade_no不能为空");
            return new Gson().toJson(map);
        }

        UserGroup userGroup = userGroupDao.selectByGroupNo(merchant_no);
        PayRequest payRequest = payRequestDao.selectByTradeNo(String.format("%s_%s",merchant_no,out_trade_no));
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.YS.getCode());

        UserChannel userChannel = userChannelDao.selectByGroupChannelCode(userGroup.getId(),String.valueOf(map.get("service")), ChannelProvider.YS.getCode());
        map.put("errcode","0");
        map.put("service",payRequest.getService());
        map.put("no",payRequest.getId());
        map.put("out_trade_no",out_trade_no);
        map.put("name",payRequest.getBody());
        map.put("remark",payRequest.getRemark());
        map.put("total",payRequest.getTotalFee());
        int payStatus = payRequest.getStatus();
        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payStatus);
        switch (payRequestStatus) {
            case NEW:
                map.put("status","0");
                break;
            case OPR_GENERATED:
                map.put("status","0");
                break;
            case PROCESSING:
                map.put("status","2");
                break;
            case OPR_SUCCESS:
                map.put("status",1);
                break;
            case USER_NOTIFIED:
                map.put("status",1);
                break;
            case PAY_SUCCESS:
                map.put("status",1);
                break;
            case PAY_FAILED:
                map.put("status",4);
                break;
            case OPR_FINISHED:
                map.put("status",4);
                break;
        }
        map.put("createtime",payRequest.getCreateTime().getTime());
        map.put("paytime",payRequest.getUpdateTime().getTime());
        map.put("merchant_no",payRequest.getMchId());
        map.put("fee_rate",userChannel.getFeeRate());
        map.put("sign_type","MD5");
        String returnSign = Utils.encrypt(map,userGroup.getCipherCode());
        map.put("sign",returnSign);

        return new Gson().toJson(map);
    }
}
