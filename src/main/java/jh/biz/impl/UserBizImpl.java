package jh.biz.impl;

import jh.biz.UserBiz;
import jh.dao.local.UserInfoDao;
import jh.model.UserInfo;
import jh.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by tengfei on 2017/10/29.
 */
@Service
public class UserBizImpl implements UserBiz {
    @Autowired
    private UserInfoDao userInfoDao;

    @Override
    public Long register(String username, String password, String email) {
        UserInfo userInfo = new UserInfo();
        userInfo.setLoginId(username);
        userInfo.setPassword(Utils.convertPassword(password));
        userInfo.setEmail(email);

        userInfoDao.insertSelective(userInfo);

        return userInfo.getId();
    }

    @Override
    public boolean login(String loginId, String password) {
        password = Utils.convertPassword(password);
        UserInfo userInfo = userInfoDao.checkLogin(loginId,password);
        return !Objects.isNull(userInfo) && !Objects.isNull(userInfo.getId()) && userInfo.getId()>0;
    }
}
