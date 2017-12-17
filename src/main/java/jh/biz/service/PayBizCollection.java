package jh.biz.service;

import jh.biz.PayBiz;
import jh.dao.local.ChannelDao;
import jh.model.po.Channel;
import jh.utils.BeanContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class PayBizCollection {
    private Map<String,PayBiz> payBizMap;
    private Map<String,PayBiz> channelMap;
    @Autowired
    private ChannelDao channelDao;


    public PayBizCollection(Map<String,PayBiz> channelMap) {
        this.payBizMap = new ConcurrentHashMap<>();
        this.channelMap = channelMap;
    }

    public PayBiz getPayBiz(String service) {
        PayBiz payBiz = payBizMap.get(service);

        if(Objects.isNull(payBiz)) {
            Channel channel = channelDao.selectByCode(service);
            payBiz = channelMap.get(channel.getChannelNo());
            if(Objects.isNull(payBiz)) {
                return null;
            }
            payBizMap.put(service,payBiz);
            return payBiz;
        }
        return payBiz;
    }
}
