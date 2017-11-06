package jh.biz.impl;

import jh.biz.UserBiz;
import jh.dao.local.UserInfoDao;
import jh.exceptions.BizException;
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
    public Long register(String username, String password, String subUserId) {
        UserInfo subUser = userInfoDao.selectByPrimaryKey(Long.parseLong(subUserId));

        UserInfo userInfo = new UserInfo();
        userInfo.setLoginId(username);
        userInfo.setPassword(Utils.convertPassword(password));
        userInfo.setSubUserId(subUser.getId());

        userInfoDao.insertSelective(userInfo);
        return userInfo.getId();
    }

    @Override
    public boolean login(String loginId, String password) {
        password = Utils.convertPassword(password);
        UserInfo userInfo = userInfoDao.checkLogin(loginId,password);
        return !Objects.isNull(userInfo) && !Objects.isNull(userInfo.getId()) && userInfo.getId()>0;
    }

    @Override
    public void edit(UserInfo userInfo) {
        UserInfo user = userInfoDao.selectByPrimaryKey(userInfo.getId());
        if(user.getStatus() != UserInfo.STATUS.INIT) {
            throw new BizException("用户状态不允许修改");
        }

        int count = userInfoDao.udpate(userInfo);

        if(count <=0) {
            throw new BizException("用户更新失败");
        }
    }
}
