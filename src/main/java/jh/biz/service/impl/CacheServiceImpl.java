package jh.biz.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jh.biz.service.CacheService;
import jh.dao.local.AccountDao;
import jh.dao.local.HfPropertiesDao;
import jh.dao.local.UserGroupDao;
import jh.dao.local.UserInfoDao;
import jh.model.po.Account;
import jh.model.po.UserGroup;
import jh.model.po.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private HfPropertiesDao hfPropertiesDao;

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

    private LoadingCache<Long,UserGroup> groupCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10,TimeUnit.MINUTES)
            .maximumSize(1000)
            .refreshAfterWrite(10,TimeUnit.MINUTES)
            .build(new CacheLoader<Long, UserGroup>() {
                @Override
                public UserGroup load(Long groupId) throws Exception {
                    return userGroupDao.selectByPrimaryKey(groupId);
                }
            });

    private LoadingCache<String,UserGroup> groupNoCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10,TimeUnit.MINUTES)
            .maximumSize(1000)
            .refreshAfterWrite(10,TimeUnit.MINUTES)
            .build(new CacheLoader<String, UserGroup>() {
                @Override
                public UserGroup load(String groupNo) throws Exception {
                    return userGroupDao.selectByGroupNo(groupNo);
                }
            });

    private LoadingCache<String,String> propCache = CacheBuilder.newBuilder().expireAfterAccess(10,TimeUnit.MINUTES)
            .maximumSize(1000).refreshAfterWrite(10,TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return hfPropertiesDao.selectByKey(s);
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
//            return accountCache.get(userId);
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public UserGroup getGroup(Long groupId) {
        try {
            return groupCache.get(groupId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserGroup getGroup(String groupNo) {
        try {
            return groupNoCache.get(groupNo);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getProp(String key,String defaultValue) {
        try {
            return propCache.get(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
