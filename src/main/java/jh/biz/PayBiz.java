package jh.biz;

import jh.model.dto.*;

/**
 * Created by tengfei on 2017/10/28.
 */
public interface PayBiz {

    PayResponse pay(PayRequestDto request);
    RefundResponse refund(RefundRequest refundRequest);
    ReverseResponse reverse(ReverseRequest reverseRequest);
}
