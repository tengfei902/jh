package jh.job;

import jh.biz.PayBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class PayResultJob {
    @Autowired
    @Qualifier("ysPayBiz")
    private PayBiz payBiz;

    @Scheduled(cron = "0 5/5 * * * *")
    public void checkPayResult() {
        //todo job log
//        payBiz.finishPay();
    }
}
