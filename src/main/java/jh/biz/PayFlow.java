package jh.biz;

import hf.base.enums.PayRequestStatus;
import jh.biz.service.PayBizCollection;
import jh.biz.service.PayService;
import jh.dao.local.PayRequestDao;
import jh.model.po.PayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayFlow {
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PayBizCollection payBizCollection;
    @Autowired
    private PayService payService;

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
