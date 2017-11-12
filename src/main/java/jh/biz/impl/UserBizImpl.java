package jh.biz.impl;

import jh.biz.UserBiz;
import jh.biz.service.CacheService;
import jh.dao.local.UserGroupDao;
import jh.dao.local.UserInfoDao;
import jh.exceptions.BizException;
import jh.model.Account;
import jh.model.UserGroup;
import jh.model.UserInfo;
import jh.model.dto.UserGroupDto;
import jh.model.dto.UserGroupRequest;
import jh.model.dto.UserInfoDto;
import jh.model.dto.UserInfoRequest;
import jh.utils.Utils;
import org.apache.commons.collections.CollectionUtils;
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
    @Autowired
    private UserGroupDao userGroupDao;

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
        List<UserInfo> list = new ArrayList<>();

        UserGroup userGroup = userGroupDao.selectByPrimaryKey(Long.parseLong(request.getAdminId()));
        getUserListByGroup(list,userGroup,request);

        list.parallelStream().distinct();

        List<UserInfoDto> result = new ArrayList<>();

        list.stream().forEach(userInfo -> result.add(buildUserDto(userInfo)));

        return result;
    }

    private void getUserListByGroup(List<UserInfo> list,UserGroup userGroup,UserInfoRequest request) {
        request.setGroupId(userGroup.getId());
        List<UserInfo> groupUserList = userInfoDao.select(request);
        list.addAll(groupUserList);
        List<UserGroup> subUserGroups = userGroupDao.selectBySubGroupId(userGroup.getId());
        if(CollectionUtils.isEmpty(subUserGroups)) {
            return;
        }
        for(UserGroup sub:subUserGroups) {
            getUserListByGroup(list,sub,request);
        }
    }

    private UserInfoDto buildUserDto(UserInfo userInfo) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userInfo.getId());
        userInfoDto.setType(UserInfo.TYPE.parse(userInfo.getType()).getDesc());
        userInfoDto.setStatus(userInfo.getStatus());
        userInfoDto.setStatusDesc(UserInfo.STATUS.parse(userInfo.getStatus()).getDesc());
        userInfoDto.setCreateTime(userInfo.getCreateTime());
        userInfoDto.setName(userInfo.getName());
        userInfoDto.setInviteCode(userInfo.getInviteCode());
        userInfoDto.setInviteSite(String.format("/%s",userInfo.getInviteCode()));
        userInfoDto.setTel(userInfo.getTel());
        UserGroup userGroup = cacheService.getGroup(userInfo.getGroupId());
        if(!Objects.isNull(userGroup)) {
            userInfoDto.setGroup(userGroup.getName());
        }
        return userInfoDto;
    }

    @Override
    public List<UserGroupDto> getUserGroupList(UserGroupRequest request) {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(Long.parseLong(request.getCompanyId()));
        if(userGroup.getType() == UserGroup.GroupType.SUPER.getValue()) {
            request.setCompanyId(null);
        }

        List<UserGroup> list = userGroupDao.select(Utils.bean2Map(request));

        List<UserGroupDto> result = new ArrayList<>();

        list.stream().forEach(group -> result.add(buildGroup(group)));
        return result;
    }

    private UserGroupDto buildGroup(UserGroup userGroup) {
        UserGroupDto userGroupDto = new UserGroupDto();
        userGroupDto.setId(userGroup.getId());

        Account account = cacheService.getAccount(userGroup.getId());
        if(!Objects.isNull(account)) {
            userGroupDto.setAmount(account.getAmount());
            userGroupDto.setLockAmount(account.getLockAmount());
        }
        userGroupDto.setCompanyId(userGroup.getCompanyId());
        userGroupDto.setCreateTime(userGroup.getCreateTime());
        userGroupDto.setGroupNo(userGroup.getGroupNo());
        userGroupDto.setName(userGroup.getName());
        userGroupDto.setStatus(userGroup.getStatus());
        userGroupDto.setStatusDesc(UserGroup.Status.parse(userGroup.getStatus()).getDesc());
        userGroupDto.setSubGroupId(userGroup.getSubGroupId());
        userGroupDto.setSubGroupName(userGroup.getSubGroupName());
        userGroupDto.setSubGroupNo(userGroup.getSubGroupNo());
        userGroupDto.setType(UserGroup.GroupType.parse(userGroup.getType()).getDesc());
        return userGroupDto;
    }
}
