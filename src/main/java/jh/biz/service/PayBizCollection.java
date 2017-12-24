package jh.biz.service;

import hf.base.enums.ChannelStatus;
import jh.biz.PayBiz;
import jh.dao.local.ChannelDao;
import jh.model.po.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
@Service
public class PayBizCollection {
    @Autowired
    @Qualifier("ysPayBiz")
    private PayBiz ysPayBiz;
    @Autowired
    @Qualifier("fxtPayBiz")
    private PayBiz fxtPayBiz;
    @Autowired
    private ChannelDao channelDao;

    public PayBiz getPayBiz(String service) {
        List<PayBiz> payBizs = Arrays.asList(ysPayBiz,fxtPayBiz);
        for(PayBiz payBiz: payBizs) {
            Channel channel = channelDao.selectByCode(service,payBiz.getProvider().getCode());
            if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
                continue;
            }
            return payBiz;
        }
        return null;
    }
}
