package jh.biz.trade.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hf.base.enums.OperateType;
import hf.base.enums.TradeType;
import hf.base.utils.Utils;
import jh.biz.PayBiz;
import jh.biz.adapter.Adapter;
import jh.biz.adapter.impl.WwPayRequestAdapter;
import jh.biz.adapter.impl.WwPayResponseAdapter;
import jh.dao.remote.PayClient;
import jh.dao.remote.WwClient;
import jh.model.po.PayMsgRecord;
import jh.model.po.PayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class WwTradeBiz extends AbstractTradeBiz {
    @Autowired
    private WwPayRequestAdapter wwPayRequestAdapter;
    @Autowired
    private WwClient wwClient;
    @Autowired
    private WwPayResponseAdapter wwPayResponseAdapter;

    @Override
    Adapter getRequestAdapter() {
        return wwPayRequestAdapter;
    }

    @Override
    Adapter getResponseAdapter() {
        return wwPayResponseAdapter;
    }

    @Override
    PayClient getClient() {
        return wwClient;
    }

    @Override
    String getSign(Map<String, Object> map, String cipherCode) {
        return null;
    }

    @Override
    PayBiz getPayBiz() {
        return null;
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        return null;
    }

    @Override
    public Map<String, Object> refund(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public void doRemotePay(PayRequest payRequest) {
        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(payRequest.getOutTradeNo(), OperateType.HF_CLIENT.getValue(), TradeType.PAY.getValue());
        PayMsgRecord hfResultMsg = payMsgRecordDao.selectByTradeNo(payMsgRecord.getOutTradeNo(),OperateType.HF_USER.getValue(),TradeType.PAY.getValue());
        if(!Objects.isNull(hfResultMsg)) {
            return;
        }
        Map<String,Object> map = new Gson().fromJson(payMsgRecord.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
        Map<String,Object> resultMap = getClient().unifiedorder(map);

        if(null != resultMap) {
            resultMap.put("inputMsg",payMsgRecord);
        }
        getResponseAdapter().adpat(resultMap);
    }
}
