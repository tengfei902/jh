package jh.biz.service;

import hf.base.contants.CodeManager;
import hf.base.enums.ChannelCode;
import hf.base.enums.ChannelProvider;
import hf.base.enums.ChannelStatus;
import hf.base.exceptions.BizFailException;
import jh.biz.trade.TradeBiz;
import jh.dao.local.ChannelDao;
import jh.dao.local.UserChannelDao;
import jh.model.po.Channel;
import jh.model.po.UserChannel;
import jh.model.po.UserGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TradeBizFactory {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    @Qualifier("fxtTradeBiz")
    private TradeBiz fxtTradeBiz;
    @Autowired
    @Qualifier("ysTradeBiz")
    private TradeBiz ysTradeBiz;

    public TradeBiz getTradeBiz(String mchId,String service) {
        if(StringUtils.isEmpty(mchId) || StringUtils.isEmpty(service)) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"商户编号参数错误");
        }

        ChannelCode channelCode = ChannelCode.parseFromCode(service);
        if(null == channelCode) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,String.format("服务类型错误,%s",service));
        }

        UserGroup userGroup = cacheService.getGroup(mchId);
        for(ChannelProvider provider:ChannelProvider.values()) {
            Channel channel = channelDao.selectByCode(service,channelCode.getCode());

            if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
                continue;
            }

            UserChannel userChannel = userChannelDao.selectByGroupChannel(userGroup.getId(),channel.getId());
            if(Objects.isNull(userChannel) || userChannel.getStatus() != ChannelStatus.VALID.getStatus()) {
                continue;
            }

            if(ChannelProvider.FXT == provider) {
                return fxtTradeBiz;
            }

            if(ChannelProvider.YS == provider) {
                return ysTradeBiz;
            }
        }

        throw new BizFailException("用户无收单权限");
    }
}
