package jh.biz.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jh.biz.service.CacheService;
import jh.dao.local.AccountDao;
import jh.dao.local.UserInfoDao;
import jh.model.Account;
import jh.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private AccountDao accountDao;

    private LoadingCache<Long,UserInfo> userCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .refreshAfterWrite(10,TimeUnit.MINUTES)
            .build(new CacheLoader<Long, UserInfo>() {
                @Override
                public UserInfo load(Long userId) throws Exception {
                    return userInfoDao.selectByPrimaryKey(userId);
                }
            });

    private LoadingCache<Long,Account> accountCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10,TimeUnit.MINUTES)
            .maximumSize(1000)
            .refreshAfterWrite(10,TimeUnit.MINUTES)
            .build(new CacheLoader<Long, Account>() {
                @Override
                public Account load(Long userId) throws Exception {
                    return accountDao.selectByUserId(userId);
                }
            });

    @Override
    public UserInfo getUserInfo(Long userId) {
        try {
            return userCache.get(userId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Account getAccount(Long userId) {
        try {
            return accountCache.get(userId);
        } catch (Exception e) {
            return null;
        }
    }
}
