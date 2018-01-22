package jh.biz.trade.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hf.base.enums.OperateType;
import hf.base.enums.PayRequestStatus;
import hf.base.enums.TradeType;
import hf.base.exceptions.BizFailException;
import hf.base.utils.Utils;
import jh.api.PayApi;
import jh.biz.PayBiz;
import jh.biz.adapter.Adapter;
import jh.biz.service.PayService;
import jh.biz.trade.TradeBiz;
import jh.dao.local.PayMsgRecordDao;
import jh.dao.local.PayRequestDao;
import jh.dao.remote.PayClient;
import jh.model.po.PayMsgRecord;
import jh.model.po.PayRequest;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractTradeBiz implements TradeBiz {
    protected Logger logger = LoggerFactory.getLogger(AbstractTradeBiz.class);

    @Autowired
    protected PayRequestDao payRequestDao;
    @Autowired
    protected PayService payService;
    @Autowired
    protected PayMsgRecordDao payMsgRecordDao;

    abstract Adapter getRequestAdapter();

    abstract Adapter getResponseAdapter();

    abstract PayClient getClient();

    abstract String getSign(Map<String,Object> map,String cipherCode);

    abstract PayBiz getPayBiz();

    protected void doPayFlow(String tradeNo,Map<String,Object> map) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);
        if(Objects.isNull(payRequest)) {
            getRequestAdapter().adpat(map);
        }

        payRequest = payRequestDao.selectByTradeNo(tradeNo);
        if(Objects.isNull(payRequest)) {
            throw new BizFailException("处理失败");
        }

        doPayFlow(payRequest);
    }

    public void doPayFlow(PayRequest payRequest) {
        switch (PayRequestStatus.parse(payRequest.getStatus())) {
            case NEW:
                payService.saveOprLog(payRequest);
                doRemotePay(payRequest);
                break;
            case OPR_GENERATED:
                doRemotePay(payRequest);
                break;
            case PROCESSING:
                retryRequest(payRequest);
                break;
        }
    }

    private void doRemotePay(PayRequest payRequest) {
        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(payRequest.getOutTradeNo(), OperateType.HF_CLIENT.getValue(), TradeType.PAY.getValue());
        Map<String,Object> map = new Gson().fromJson(payMsgRecord.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
        map.put("nonce_str",Utils.getRandomString(8));
        String sign = getSign(map,payMsgRecord.getCipherCode());
        map.put("sign",sign);

        PayMsgRecord hfResultMsg = payMsgRecordDao.selectByTradeNo(payMsgRecord.getOutTradeNo(),OperateType.HF_USER.getValue(),TradeType.PAY.getValue());
        if(!Objects.isNull(hfResultMsg)) {
            return;
        }
        PayMsgRecord resultMsg = payMsgRecordDao.selectByTradeNo(payMsgRecord.getOutTradeNo(),OperateType.CLIENT_HF.getValue(),TradeType.PAY.getValue());

        Map<String,Object> resultMap;
        if(Objects.isNull(resultMsg)) {
            resultMap = getClient().unifiedorder(map);
        } else {
            resultMap = new Gson().fromJson(resultMsg.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
        }

        if(null != resultMap) {
            resultMap.put("inputMsg",payMsgRecord);
        }
        getResponseAdapter().adpat(resultMap);
    }

    private void retryRequest(PayRequest payRequest) {
        doRemotePay(payRequest);
    }

    @Override
    public Map<String, Object> pay(Map<String, Object> requestMap) {
        String outTradeNo = String.valueOf(requestMap.get("out_trade_no"));
        String merchant_no = String.valueOf(requestMap.get("merchant_no"));
        String tradeNo = String.format("%s_%s",merchant_no,outTradeNo);

        doPayFlow(tradeNo,requestMap);

        PayMsgRecord result = payMsgRecordDao.selectByTradeNo(tradeNo, OperateType.HF_USER.getValue(), TradeType.PAY.getValue());
        return new Gson().fromJson(result.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
    }

    @Override
    public void handleProcessingRequest(PayRequest payRequest) {
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        if(payRequest.getStatus() != PayRequestStatus.PROCESSING.getValue()) {
            logger.warn(String.format("payRequest not processing,%s,%s",payRequest.getOutTradeNo(),payRequest.getStatus()));
            return;
        }

        Map<String,Object> payResult = query(payRequest);

        if(MapUtils.isEmpty(payResult)) {
            logger.warn(String.format("query failed,%s",payRequest.getOutTradeNo()));
            return;
        }

        if(Objects.isNull(payResult.get("errcode"))) {
            logger.warn(String.format("query failed,errcode is null,%s",payRequest.getOutTradeNo()));
        }
        int errcode = Integer.parseInt(String.valueOf(payResult.get("errcode")));
        if(errcode != 0) {
            return;
        }
        String message = String.valueOf(payResult.get("message"));
        String service = String.valueOf(payResult.get("service"));
        String no = String.valueOf(payResult.get("no"));
        String out_trade_no = String.valueOf(payResult.get("out_trade_no"));
        int status = Integer.parseInt(String.valueOf(payResult.get("status")));

        switch (status) {
            case 0:
                logger.info(String.format("not paid,%s",payRequest.getOutTradeNo()));
                return;
            case 1:
                logger.info(String.format("pay success,%s",payRequest.getOutTradeNo()));
                payService.paySuccess(payRequest.getOutTradeNo());
                getPayBiz().notice(payRequest);
                return;
            case 2:
                logger.info(String.format("waiting pay,%s",payRequest.getOutTradeNo()));
                return;
            case 3:
            case 4:
            case 5:
                payService.payFailed(payRequest.getOutTradeNo());
                getPayBiz().notice(payRequest);
        }
    }
}
