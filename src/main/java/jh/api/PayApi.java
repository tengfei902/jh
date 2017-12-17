package jh.api;

import com.google.gson.Gson;
import hf.base.contants.CodeManager;
import hf.base.enums.OperateType;
import hf.base.enums.TradeType;
import hf.base.exceptions.BizFailException;
import hf.base.utils.Utils;
import jh.biz.PayBiz;
import jh.biz.service.CacheService;
import jh.biz.service.PayBizCollection;
import jh.dao.local.PayMsgRecordDao;
import jh.dao.local.PayRequestDao;
import jh.model.po.PayMsgRecord;
import jh.model.po.PayRequest;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
    private CacheService cacheService;

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
        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(out_trade_no, OperateType.HF_USER.getValue(), TradeType.PAY.getValue());
        if(!Objects.isNull(payMsgRecord)) {
            return payMsgRecord.getMsgBody();
        }

        try {
            payBiz.savePayRequest(params);
        } catch (BizFailException e) {
            return new Gson().toJson(hf.base.utils.MapUtils.buildMap("errcode",e.getCode(),"message",e.getMessage()));
        }

        try {
            PayRequest payRequest = payRequestDao.selectByTradeNo(out_trade_no);
            payBiz.pay(payRequest);
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
}
