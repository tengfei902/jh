package jh.biz.impl;

import hf.base.enums.PayRequestStatus;
import hf.base.exceptions.BizFailException;
import hf.base.utils.MapUtils;
import jh.biz.PayBiz;
import jh.biz.service.PayService;
import jh.dao.local.PayRequestDao;
import jh.dao.remote.YsClient;
import jh.model.dto.*;
import jh.model.po.PayRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class YsPayBiz implements PayBiz {
    @Autowired
    private YsClient ysClient;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PayService payService;

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
                payService.saveOprLog(payRequest);
                doRemoteCall(request,payRequest);
                break;
            case OPR_GENERATED:
                doRemoteCall(request,payRequest);
                break;
            case REMOTE_CALL_FINISHED:
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
        return null;
    }

    private void doRemoteCall(PayRequestDto request,PayRequest payRequest) {
        try {
            ysClient.pay(MapUtils.beanToMap(request));
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
        payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_GENERATED.getValue(),PayRequestStatus.REMOTE_CALL_FINISHED.getValue());
    }

    @Override
    public void finishPay() {
        List<PayRequest> unfinishedRequests = payRequestDao.selectUnfinishedList(DateUtils.addMinutes(new Date(),-0));
        unfinishedRequests.parallelStream().forEach(payRequest -> {
            Map<String,String> result = ysClient.getPayResult(payRequest.getMchId(),payRequest.getOutTradeNo());
            if(org.apache.commons.collections.MapUtils.isEmpty(result)) {
                return;
            }
            if(!StringUtils.equals(result.get("out_trade_no"),payRequest.getOutTradeNo())) {
                return;
            }
            if(StringUtils.equalsIgnoreCase(result.get("trade_state"),"SUCCESS")) {
                payService.paySuccess(payRequest.getOutTradeNo());
            }
        });
    }

    @Override
    public void promote() {
        List<PayRequest> list = payRequestDao.selectWaitingPromote();
        list.parallelStream().forEach(payRequest ->
            payService.payPromote(payRequest.getOutTradeNo())
        );
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
