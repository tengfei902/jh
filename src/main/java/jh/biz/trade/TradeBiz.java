package jh.biz.trade;

import jh.model.po.PayRequest;

import java.util.Map;

public interface TradeBiz {
    Map<String,Object> pay(Map<String,Object> requestMap);
    Map<String,Object> query(PayRequest payRequest);
    Map<String,Object> refund(Map<String,Object> requestMap);
    void doPayFlow(PayRequest payRequest);
    void handleProcessingRequest(PayRequest payRequest);
}
