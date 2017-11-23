package jh.biz.service;

import jh.model.po.PayRequest;

/**
 * Created by tengfei on 2017/10/28.
 */
public interface PayService {
    void saveOprLog(PayRequest payRequest);
    void paySuccess(String outTradeNo);
    void payPromote(String outTradeNo);
}
