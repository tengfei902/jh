package jh.biz;


import jh.model.po.Channel;
import jh.model.po.UserChannel;

import java.util.List;

public interface ChannelBiz {

    List<Channel> getChanneList();

    void saveUserChannel(UserChannel userChannel);
}
