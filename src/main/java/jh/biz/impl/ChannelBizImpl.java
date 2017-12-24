package jh.biz.impl;

import hf.base.exceptions.BizFailException;
import jh.biz.ChannelBiz;
import jh.dao.local.ChannelDao;
import jh.dao.local.UserChannelDao;
import jh.dao.local.UserGroupDao;
import jh.model.po.Channel;
import jh.model.po.UserChannel;
import jh.model.po.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ChannelBizImpl implements ChannelBiz {
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserChannelDao userChannelDao;

    @Override
    public List<Channel> getChanneList() {
        return null;
    }

    @Override
    public void saveUserChannel(UserChannel userChannel) {
        if(userChannel.getId()!=null) {
            userChannelDao.updateByPrimaryKeySelective(userChannel);
        } else {
            Channel channel = channelDao.selectByPrimaryKey(userChannel.getChannelId());
            userChannel.setChannelCode(channel.getCode());
            userChannel.setChannelName(channel.getChannelName());
            userChannel.setStandardFeeRate(channel.getFeeRate());
            UserGroup userGroup = userGroupDao.selectByPrimaryKey(userChannel.getGroupId());
            userChannel.setGroupName(userGroup.getName());
            userChannel.setCompanyId(userGroup.getCompanyId());

            UserChannel subUserChannel = getSubUserChannel(userGroup.getSubGroupId(),channel.getId());
            if(Objects.isNull(subUserChannel)) {
                userChannel.setSubFeeRate(channel.getFeeRate());
            } else {
                userChannel.setSubFeeRate(subUserChannel.getFeeRate());
            }

            userChannel.setSubGroupId(userGroup.getSubGroupId());

            if(userChannel.getFeeRate().compareTo(userChannel.getSubFeeRate())<0) {
                throw new BizFailException(String.format("费率必须大于%s",userChannel.getSubFeeRate()));
            }

            if(userChannel.getSubFeeRate().compareTo(userChannel.getStandardFeeRate())<0) {
                throw new BizFailException(String.format("费率必须大于%s",userChannel.getStandardFeeRate()));
            }

            userChannelDao.insertSelective(userChannel);
        }
    }

    private UserChannel getSubUserChannel(Long groupId,Long channelId) {
        return userChannelDao.selectByGroupChannel(groupId,channelId);
    }
}
