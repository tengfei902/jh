package jh.job;

import jh.biz.PayBiz;
import jh.biz.service.TradeBizFactory;
import jh.biz.trade.TradeBiz;
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
    private PayRequestDao payRequestDao;
    @Autowired
    private TradeBizFactory tradeBizFactory;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void doPay() {
        logger.info(String.format("Start do pay,current time:%s",new Date()));
        Date date = DateUtils.addMinutes(new Date(),-1);
        List<PayRequest> payRequestList = payRequestDao.selectUnfinishedList(0L,date);
        int count = 0;
        while (!CollectionUtils.isEmpty(payRequestList)) {
            count++;
            logger.info(String.format("do pay loop size:%s",count));

            payRequestList.parallelStream().forEach(payRequest -> {
                TradeBiz tradeBiz = tradeBizFactory.getTradeBiz(payRequest.getMchId(),payRequest.getService());
                tradeBiz.doPayFlow(payRequest);
            });

            if(count>=1000) {
                logger.warn("loop more than 1000,break");
                break;
            }

            Long maxId = payRequestList.get(payRequestList.size()-1).getId();
            payRequestList = payRequestDao.selectUnfinishedList(maxId,new Date());
        }
    }
}
