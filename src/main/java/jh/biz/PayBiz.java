package jh.biz;

import hf.base.enums.ChannelProvider;
import jh.model.dto.*;
import jh.model.po.PayRequest;
import jh.model.remote.RefundRequest;

import java.util.Map;

/**
 * Created by tengfei on 2017/10/28.
 */
public interface PayBiz {
    void checkParam(Map<String,Object> map);
    Long savePayRequest(Map<String,Object> map);
    void pay(PayRequest payRequest);
    void checkCallBack(Map<String,Object> map);
    void finishPay(Map<String,Object> map);

    void paySuccess(String outTradeNo);
    void payFailed(String outTradeNo);
    void promote(String outTradeNo);

    RefundResponse refund(RefundRequest refundRequest);
    ReverseResponse reverse(ReverseRequest reverseRequest);

    ChannelProvider getProvider();
}
