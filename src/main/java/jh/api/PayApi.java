package jh.api;

import com.google.gson.Gson;
import hf.base.contants.CodeManager;
import hf.base.enums.OperateType;
import hf.base.enums.PayRequestStatus;
import hf.base.enums.TradeType;
import hf.base.exceptions.BizFailException;
import hf.base.utils.Utils;
import jh.biz.PayBiz;
import jh.biz.PayFlow;
import jh.biz.impl.AbstractPayBiz;
import jh.biz.service.CacheService;
import jh.biz.service.PayBizCollection;
import jh.dao.local.PayMsgRecordDao;
import jh.dao.local.PayRequestDao;
import jh.model.po.PayMsgRecord;
import jh.model.po.PayRequest;
import org.apache.commons.collections.MapUtils;
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
    private PayMsgRecordDao payMsgRecordDao;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PayBizCollection payBizCollection;
    @Autowired
    @Qualifier("ysPayBiz")
    private PayBiz payBiz;
    @Autowired
    private PayFlow payFlow;

    protected Logger logger = LoggerFactory.getLogger(PayApi.class);

    @RequestMapping(value = "/unifiedorder",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody String unifiedorder(@RequestBody Map<String,Object> params) {
        if(MapUtils.isEmpty(params)) {
            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode", CodeManager.PARAM_CHECK_FAILED,"message","service不能为空"));
        }
        PayBiz payBiz = payBizCollection.getPayBiz(String.valueOf(params.get("service")));
        if(Objects.isNull(payBiz)) {
            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode",CodeManager.PARAM_CHECK_FAILED,"message","service错误"));
        }
        try {
            payBiz.checkParam(params);
        } catch (BizFailException e) {
            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode",e.getCode(),"message",e.getMessage()));
        }

        String out_trade_no = String.valueOf(params.get("out_trade_no"));
        String mchId = String.valueOf(params.get("merchant_no"));
        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(String.format("%s_%s",mchId,out_trade_no), OperateType.HF_USER.getValue(), TradeType.PAY.getValue());
        if(!Objects.isNull(payMsgRecord)) {
            return payMsgRecord.getMsgBody();
        }

        try {
            payBiz.savePayRequest(params);
        } catch (BizFailException e) {
            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode",e.getCode(),"message",e.getMessage()));
        }

        try {
            payFlow.invoke(out_trade_no,PayRequestStatus.PROCESSING);
            payMsgRecord = payMsgRecordDao.selectByTradeNo(out_trade_no, OperateType.HF_USER.getValue(), TradeType.PAY.getValue());
            return payMsgRecord.getMsgBody();

        } catch (BizFailException e) {
            payMsgRecord = payMsgRecordDao.selectByTradeNo(out_trade_no, OperateType.HF_USER.getValue(), TradeType.PAY.getValue());
            if(!Objects.isNull(payMsgRecord)) {
                return payMsgRecord.getMsgBody();
            }
            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode",e.getCode(),"message",e.getMessage()));
        }
    }

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

        String out_trade_no = String.valueOf(params.get("out_trade_no"));

        PayRequest payRequest = payRequestDao.selectByTradeNo(out_trade_no);

        Map<String,Object> resutMap = new HashMap<>();

        if(payRequest.getStatus() == PayRequestStatus.OPR_SUCCESS.getValue()) {

            //code 0成功 99失败
            resutMap.put("code","0");
            //msg
            resutMap.put("msg","支付成功");
            //out_trade_no
            resutMap.put("out_trade_no",payRequest.getOutTradeNo());
            //mch_id
            resutMap.put("mch_id",payRequest.getMchId());
            //total
            resutMap.put("total",payRequest.getTotalFee());
            //fee
            resutMap.put("fee",payRequest.getFee());
            //trade_type 1:收单 2:撤销 3:退款
            resutMap.put("trade_type","1");
            //sign_type
            resutMap.put("sign_type","MD5");
            //sign
//            String sign = Utils.encrypt(resutMap,);
        }

        if(payRequest.getStatus() == PayRequestStatus.PAY_FAILED.getValue() || payRequest.getStatus() == PayRequestStatus.OPR_FINISHED.getValue()) {

        }

        return CodeManager.SUCCESS;
    }
}
