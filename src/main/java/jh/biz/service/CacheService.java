package jh.biz.service;

import jh.model.Account;
import jh.model.UserInfo;

public interface CacheService {

    UserInfo getUserInfo(Long userId);

    Account getAccount(Long userId);
}
