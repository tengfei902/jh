package jh.biz.impl;

import jh.biz.UserBiz;
import jh.biz.service.CacheService;
import jh.dao.local.UserInfoDao;
import jh.exceptions.BizException;
import jh.model.Account;
import jh.model.UserInfo;
import jh.model.dto.UserInfoDto;
import jh.model.dto.UserInfoRequest;
import jh.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by tengfei on 2017/10/29.
 */
@Service
public class UserBizImpl implements UserBiz {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private CacheService cacheService;

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
        if(user.getStatus() != UserInfo.STATUS.init.getValue()) {
            throw new BizException("用户状态不允许修改");
        }

        int count = userInfoDao.updateByPrimaryKeySelective(userInfo);

        if(count <=0) {
            throw new BizException("用户更新失败");
        }
    }

    @Override
    public List<UserInfoDto> getUserList(UserInfoRequest request) {
        UserInfo admin = userInfoDao.selectByPrimaryKey(Long.parseLong(request.getAdminId()));
        if(admin.getType() == UserInfo.TYPE.SUPER_ADMIN.getValue()) {
            request.setAdminId(null);
        }

        List<UserInfo> list = userInfoDao.select(request);
        List<UserInfoDto> result = new ArrayList<>();

        list.stream().forEach(userInfo -> result.add(buildUserDto(userInfo)));

        return result;
    }

    private UserInfoDto buildUserDto(UserInfo userInfo) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userInfo.getId());
        userInfoDto.setName(userInfo.getName());
        userInfoDto.setCreateTime(userInfo.getCreateTime());
        userInfoDto.setStatus(userInfo.getStatus());
        userInfoDto.setStatusDesc(UserInfo.STATUS.parse(userInfo.getSex()).getDesc());

        UserInfo subUserInfo = cacheService.getUserInfo(userInfo.getSubUserId());

        if(!Objects.isNull(subUserInfo)) {
            userInfoDto.setSubUserId(userInfo.getSubUserId());
            userInfoDto.setSubUserName(subUserInfo.getName());
        }

        Account account = cacheService.getAccount(userInfo.getId());
        if(!Objects.isNull(account)) {
            userInfoDto.setAmount(account.getAmount().subtract(account.getLockAmount()));
            userInfoDto.setLockAmount(account.getLockAmount());
        }

        userInfoDto.setType(UserInfo.TYPE.parse(userInfo.getType()).getDesc());
        userInfoDto.setUserNo(userInfo.getUserNo());
        userInfoDto.setSubUserNo(userInfo.getSubUserNo());
        userInfoDto.setAdminId(userInfo.getAdminId());
        return userInfoDto;
    }
}
