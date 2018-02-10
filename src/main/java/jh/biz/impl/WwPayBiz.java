package jh.biz.impl;

import hf.base.enums.ChannelProvider;
import jh.dao.remote.PayClient;
import jh.model.po.PayMsgRecord;
import jh.model.po.PayRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WwPayBiz extends AbstractPayBiz {
    @Override
    PayClient getPayClient() {
        return null;
    }

    @Override
    void handlePayResult(PayRequest payRequest, PayMsgRecord payMsgRecord, Map<String, Object> payResult) {

    }

    @Override
    public void checkCallBack(Map<String, Object> map) {

    }

    @Override
    public void finishPay(Map<String, Object> map) {

    }

    @Override
    public ChannelProvider getProvider() {
        return null;
    }
}
