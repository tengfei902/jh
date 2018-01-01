package jh.job;

import jh.biz.PayBiz;
import jh.dao.local.PayRequestDao;
import jh.model.po.PayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * payRequest状态5->10,完成收款
 */
@Component
public class PayPromoteJob {
    @Autowired
    @Qualifier("ysPayBiz")
    private PayBiz payBiz;
    @Autowired
    private PayRequestDao payRequestDao;

    @Scheduled(cron = "0 0 1 * * ?")
    public void doPromote() {
        List<PayRequest> list = payRequestDao.selectWaitingPromote();
//        list.parallelStream().forEach(payRequest -> payBiz.promote(payRequest.getOutTradeNo()));
    }
}
