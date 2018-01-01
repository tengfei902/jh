package jh.biz;

import hf.base.enums.PayRequestStatus;
import jh.biz.impl.AbstractPayBiz;
import jh.biz.service.PayBizCollection;
import jh.biz.service.PayService;
import jh.dao.local.PayRequestDao;
import jh.model.po.PayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PayFlow {
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PayBizCollection payBizCollection;
    @Autowired
    private PayService payService;
    private Logger logger = LoggerFactory.getLogger(PayFlow.class);

    public void invoke(String outTradeNo,PayRequestStatus targetStatus) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        if(Objects.isNull(payRequest)) {
            logger.warn("payRequest is null");
            return;
        }
        if(PayRequestStatus.isFinalStatus(payRequest.getStatus())) {
            logger.warn(String.format("payRequest already finished,%s",payRequest.getOutTradeNo()));
            return;
        }

        int count = 0;
        while (payRequest.getStatus() != targetStatus.getValue()) {
            if(count>10) {
                logger.warn(String.format("invoke time gt 10,requestId:%s",payRequest.getOutTradeNo()));
                break;
            }
            invoke(payRequest.getOutTradeNo());
            payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
            count++;
        }
    }

    public void invoke(String outTradeNo) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);

        PayBiz payBiz = payBizCollection.getPayBiz(payRequest.getService());
        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus());
        switch (payRequestStatus) {
            case NEW:
                payService.saveOprLog(payRequest);
                break;
            case OPR_GENERATED:
                payBiz.doRemoteCall(payRequest);
                break;
            case PROCESSING:
                //do nothing
                break;
            case PAY_FAILED:
                //do nothing
                break;
            case OPR_SUCCESS:
                payBiz.notice(payRequest);
                break;
            case USER_NOTIFIED:
                payBiz.promote(payRequest);
                break;
            case OPR_FINISHED:
                //do nothing
                break;
            case PAY_SUCCESS:
                //do nothing
                break;
        }
    }
}
