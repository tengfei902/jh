package jh.job;

import jh.biz.PayBiz;
import jh.dao.local.PayRequestDao;
import jh.model.po.PayRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PayPayJob {

    private Logger logger = LoggerFactory.getLogger(PayPayJob.class);

    @Autowired
    @Qualifier("ysPayBiz")
    private PayBiz payBiz;
    @Autowired
    private PayRequestDao payRequestDao;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void doPay() {
        logger.info(String.format("Start do pay,current time:%s",new Date()));
        Date date = DateUtils.addMinutes(new Date(),-1);
        List<PayRequest> payRequestList = payRequestDao.selectUnfinishedList(0L,date);
        int count = 0;
        while (!CollectionUtils.isEmpty(payRequestList)) {
            count++;
            logger.info(String.format("do pay loop size:%s",count));
            payRequestList.parallelStream().forEach(payRequest -> payBiz.pay(payRequest));

            if(count>=1000) {
                logger.warn("loop more than 1000,break");
                break;
            }

            Long maxId = payRequestList.get(payRequestList.size()-1).getId();
            payRequestList = payRequestDao.selectUnfinishedList(maxId,new Date());
        }
    }
}
