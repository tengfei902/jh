package jh.biz.service;

import jh.model.po.Account;
import jh.model.po.Channel;
import jh.model.po.UserGroup;
import jh.model.po.UserInfo;

public interface CacheService {

    UserInfo getUserInfo(Long userId);

    Account getAccount(Long userId);

    UserGroup getGroup(Long groupId);

    UserGroup getGroup(String groupNo);

    String getProp(String key,String defaultValue);

    String getPublicKey();

    String getPrivateKey();
}
