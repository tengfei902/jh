package jh.job.pay;

import hf.base.enums.PayRequestStatus;
import hf.base.enums.TradeType;
import hf.base.exceptions.BizFailException;
import hf.base.utils.MapUtils;
import jh.biz.service.PayService;
import jh.biz.service.TradeBizFactory;
import jh.biz.trade.TradeBiz;
import jh.dao.local.PayRequestDao;
import jh.model.po.PayRequest;
import jh.model.po.UserGroup;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PayJob {
    protected Logger logger = LoggerFactory.getLogger(PayJob.class);
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private TradeBizFactory tradeBizFactory;
    @Autowired
    private PayService payService;

    //银行受理中的交易
    @Scheduled(cron = "0/2 * * * * ?")
    public void handleProcessingPayRequest() {
        Long startId = 0L;
        int page = 1;
        int pageSize = 500;
        while (page<100) {
            Map<String,Object> map = MapUtils.buildMap("status", PayRequestStatus.PROCESSING.getValue(),
                    "type", TradeType.PAY.getValue(),"startId",startId,
                    "startIndex",(page-1)*pageSize,
                    "pageSize",pageSize,"sortType","asc");
            List<PayRequest> list = payRequestDao.select(map);

            logger.warn("handleProcessingPayRequest , list size:"+list.size());

            if(CollectionUtils.isEmpty(list)) {
                break;
            }

            page++;
            startId = list.get(list.size()-1).getId();

            list.parallelStream().forEach(payRequest -> {
                try {
                    TradeBiz tradeBiz = tradeBizFactory.getTradeBiz(payRequest.getChannelProviderCode());
                    tradeBiz.handleProcessingRequest(payRequest);
                } catch (BizFailException e) {
                    logger.error(String.format("pay processing job failed,%s,%s",e.getMessage(),payRequest.getOutTradeNo()));
                }
            });
        }
    }

    @Scheduled(cron = "0/2 * * * * ?")
    public void handleNoNoticingRequest() {
        logger.info("Start handleNoNoticingRequest");
        Long startId = 0L;
        int page = 1;
        int pageSize = 500;
        while(page<100) {
            Map<String,Object> map = MapUtils.buildMap("status", PayRequestStatus.OPR_SUCCESS.getValue(),
                    "type", TradeType.PAY.getValue(),"startId",startId,
                    "startIndex",(page-1)*pageSize,
                    "pageSize",pageSize,"sortType","asc");
            List<PayRequest> list = payRequestDao.select(map);

            if(CollectionUtils.isEmpty(list)) {
                break;
            }

            page++;
            startId = list.get(list.size()-1).getId();

            list.parallelStream().forEach(payRequest -> {
                TradeBiz tradeBiz = tradeBizFactory.getTradeBiz(payRequest.getChannelProviderCode());
                tradeBiz.notice(payRequest);
            });
        }

    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void doPromote() {
        List<PayRequest> list = payRequestDao.selectWaitingPromote();
        list.stream().forEach(payRequest -> {
            try {
                payService.payPromote(payRequest.getOutTradeNo());
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        });
    }
}
