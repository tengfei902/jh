package jh.biz.impl;

import hf.base.utils.MapUtils;
import jh.biz.PayBiz;
import jh.dao.local.PayRequestDao;
import jh.dao.remote.YsClient;
import jh.model.dto.*;
import jh.model.enums.PayRequestStatus;
import jh.model.po.PayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class YsPayBiz implements PayBiz {
    @Autowired
    private YsClient ysClient;
    @Autowired
    private PayRequestDao payRequestDao;

    /**
     * @param request
     * @return
     */
    @Override
    public PayResponse pay(PayRequestDto request) {
        PayRequest payRequest = new PayRequest(request);
        try {
            payRequestDao.insertSelective(payRequest);
        } catch (DuplicateKeyException e) {

        }

        payRequest = payRequestDao.selectByTradeNo(request.getOut_trade_no());

        switch (PayRequestStatus.parse(payRequest.getStatus())) {
            case NEW:
                break;
            case OPR_GENERATED:
                break;
            case PAY_SUCCESS:
                break;
            case PAY_FAILED:
                break;
            case OPR_SUCCESS:
                break;
            case OPR_FINISHED:
                break;
        }

        String result = ysClient.pay(MapUtils.beanToMap(request));
        return null;
    }

    @Override
    public RefundResponse refund(RefundRequest refundRequest) {
        return null;
    }

    @Override
    public ReverseResponse reverse(ReverseRequest reverseRequest) {
        return null;
    }
}
