package jh.biz.service.impl;

import jh.biz.service.UserService;
import jh.dao.local.UserGroupDao;
import jh.dao.local.UserInfoDao;
import jh.model.po.UserGroup;
import jh.model.po.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultUserService implements UserService {
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserInfoDao userInfoDao;

    @Transactional
    @Override
    public void register(UserGroup userGroup, UserInfo userInfo) {
        userGroupDao.insertSelective(userGroup);
        userInfo.setGroupId(userGroup.getId());
        userInfoDao.insertSelective(userInfo);
    }
}
