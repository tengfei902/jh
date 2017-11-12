package jh.biz.impl;

import jh.biz.ChannelBiz;
import jh.dao.local.ChannelDao;
import jh.model.enums.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelBizImpl implements ChannelBiz {
    @Autowired
    private ChannelDao channelDao;

    @Override
    public List<Channel> getChanneList() {


        return null;
    }
}
