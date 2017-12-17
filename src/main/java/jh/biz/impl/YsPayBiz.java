package jh.biz.impl;

import jh.biz.service.PayService;
import jh.dao.local.PayRequestDao;
import jh.dao.remote.PayClient;
import jh.dao.remote.YsClient;
import jh.model.dto.*;
import jh.model.remote.RefundRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class YsPayBiz extends AbstractPayBiz {
    @Autowired
    private YsClient ysClient;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PayService payService;

    @Override
    PayClient getPayClient() {
        return ysClient;
    }

    @Override
    public Long savePayRequest(Map<String, Object> map) {
        return null;
    }

    @Override
    public void checkParam(Map<String, Object> map) {

    }

    //    @Override
//    public PayResponse pay(Map<String,Object> map) {
//        PayRequestDto request = null;
//        PayRequest payRequest = new PayRequest(request);
//        try {
//            payRequestDao.insertSelective(payRequest);
//        } catch (DuplicateKeyException e) {
//
//        }
//
//        payRequest = payRequestDao.selectByTradeNo(request.getOut_trade_no());
//
//        switch (PayRequestStatus.parse(payRequest.getStatus())) {
//            case NEW:
//                payService.saveOprLog(payRequest);
//                doRemoteCall(request,payRequest);
//                break;
//            case OPR_GENERATED:
//                doRemoteCall(request,payRequest);
//                break;
//            case REMOTE_CALL_FINISHED:
//                break;
//            case PAY_SUCCESS:
//                break;
//            case PAY_FAILED:
//                break;
//            case OPR_SUCCESS:
//                break;
//            case OPR_FINISHED:
//                break;
//        }
//        return null;
//    }
//
//    private void doRemoteCall(PayRequestDto request,PayRequest payRequest) {
//        try {
//            ysClient.pay(MapUtils.beanToMap(request));
//        } catch (Exception e) {
//            throw new BizFailException(e.getMessage());
//        }
//        payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_GENERATED.getValue(),PayRequestStatus.REMOTE_CALL_FINISHED.getValue());
//    }
//
//    @Override
//    public void finishPay() {
//        List<PayRequest> unfinishedRequests = payRequestDao.selectUnfinishedList(DateUtils.addMinutes(new Date(),-0));
//        unfinishedRequests.parallelStream().forEach(payRequest -> {
//            Map<String,String> result = ysClient.getPayResult(payRequest.getMchId(),payRequest.getOutTradeNo());
//            if(org.apache.commons.collections.MapUtils.isEmpty(result)) {
//                return;
//            }
//            if(!StringUtils.equals(result.get("out_trade_no"),payRequest.getOutTradeNo())) {
//                return;
//            }
//            if(StringUtils.equalsIgnoreCase(result.get("trade_state"),"SUCCESS")) {
//                payService.paySuccess(payRequest.getOutTradeNo());
//            }
//        });
//    }
//
//    @Override
//    public void promote() {
//        List<PayRequest> list = payRequestDao.selectWaitingPromote();
//        list.parallelStream().forEach(payRequest ->
//            payService.payPromote(payRequest.getOutTradeNo())
//        );
//    }

    @Override
    public RefundResponse refund(RefundRequest refundRequest) {
        return null;
    }

    @Override
    public ReverseResponse reverse(ReverseRequest reverseRequest) {
        return null;
    }
}
