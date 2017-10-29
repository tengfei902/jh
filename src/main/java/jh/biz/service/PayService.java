package jh.biz.service;

import jh.model.dto.*;

/**
 * Created by tengfei on 2017/10/28.
 */
public interface PayService {
    String savePayInfo(PayRequest request);
    String saveRefund(RefundRequest refundRequest);
    void saveReverse(ReverseRequest reverseRequest);
    void pay(PayResponse response);
    void refund(RefundResponse refundResponse);
    void reverse(ReverseResponse reverseResponse);
}
