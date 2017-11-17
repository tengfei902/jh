package jh.biz.service;

import jh.model.po.PayTrdOrder;
import jh.model.dto.RefundResponse;
import jh.model.dto.ReverseResponse;

/**
 * Created by tengfei on 2017/10/28.
 */
public interface AccountService {
    void pay(PayTrdOrder payTrdOrder);
    void refund(RefundResponse refundResponse);
    void reverse(ReverseResponse reverseResponse);
}
