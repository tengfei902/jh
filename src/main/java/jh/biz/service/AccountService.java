package jh.biz.service;

import jh.model.dto.RefundResponse;
import jh.model.dto.ReverseResponse;
import jh.model.po.AccountOprLog;

/**
 * Created by tengfei on 2017/10/28.
 */
public interface AccountService {
//    void pay(PayTrdOrder payTrdOrder);
    void refund(RefundResponse refundResponse);
    void reverse(ReverseResponse reverseResponse);
    void promote(AccountOprLog log);
    void settle(Long settleId);
}
