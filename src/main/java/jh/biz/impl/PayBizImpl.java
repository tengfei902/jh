package jh.biz.impl;

import jh.biz.PayBiz;
import jh.model.dto.*;
import org.springframework.stereotype.Service;

/**
 * Created by tengfei on 2017/10/28.
 */
@Service
public class PayBizImpl implements PayBiz {
//    @Autowired
//    private FxtClient fxtClient;
//    @Autowired
//    private PayService payService;
//    @Autowired
//    private PayRefundOrderDao payRefundOrderDao;
//    @Autowired
//    private PayReverseOrderDao payReverseOrderDao;
//
//    /**
//     * 1.落地
//     * 2.
//     * @param request
//     */
//    @Override
//    public PayResponse pay(PayRequestDto request) {
//        payService.savePayInfo(request);
//        PayResponse response = fxtClient.unifiedorder(request);
//        payService.pay(response);
//        return response;
//    }
//
//    @Override
//    public RefundResponse refund(RefundRequest refundRequest) {
//        payService.saveRefund(refundRequest);
//        RefundResponse refundResponse = fxtClient.refund(refundRequest);
//        payService.refund(refundResponse);
//        return refundResponse;
//    }
//
//    @Override
//    public ReverseResponse reverse(ReverseRequest reverseRequest) {
//        payService.saveReverse(reverseRequest);
//        ReverseResponse reverseResponse = fxtClient.reverse(reverseRequest);
//        payService.reverse(reverseResponse);
//        return reverseResponse;
//    }

//    @Override
//    public PayResponse pay(PayRequest request) {
//        return null;
//    }

    @Override
    public RefundResponse refund(RefundRequest refundRequest) {
        return null;
    }

    @Override
    public ReverseResponse reverse(ReverseRequest reverseRequest) {
        return null;
    }

    @Override
    public PayResponse pay(PayRequestDto request) {
        return null;
    }
}
