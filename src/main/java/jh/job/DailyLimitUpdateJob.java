package jh.job;

import jh.biz.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class DailyLimitUpdateJob {
    @Autowired
    private PayService payService;

    @Scheduled(cron = "0 0 1 * * ? ")
    public void checkPayResult() {
        payService.updateDailyLimit();
    }
}
