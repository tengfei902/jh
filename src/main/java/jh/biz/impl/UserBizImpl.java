package jh.biz.impl;

import hf.base.enums.*;
import hf.base.exceptions.BizFailException;
import hf.base.utils.SegmentLock;
import hf.base.utils.Utils;
import jh.biz.UserBiz;
import jh.biz.service.CacheService;
import jh.biz.service.UserService;
import jh.dao.local.*;
import jh.model.po.*;
import jh.model.dto.UserGroupDto;
import jh.model.dto.UserGroupRequest;
import jh.model.dto.UserInfoDto;
import jh.model.dto.UserInfoRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @Autowired
    private UserService userService;
    @Autowired
    private UserBankCardDao userBankCardDao;
    @Autowired
    private AdminBankCardDao adminBankCardDao;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private AccountDao accountDao;

    @Override
    public void register(String loginId, String password, String inviteCode) {
        UserGroup subUserGroup;
        if(StringUtils.isEmpty(inviteCode)) {
            subUserGroup = userGroupDao.selectDefaultUserGroup();
        } else {
            UserInfo subUserInfo = userInfoDao.selectByInviteCode(inviteCode);
            if(Objects.isNull(subUserInfo)) {
                subUserGroup = userGroupDao.selectDefaultUserGroup();
            } else {
                subUserGroup = userGroupDao.selectByPrimaryKey(subUserInfo.getGroupId());
            }
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setLoginId(loginId);
        userInfo.setPassword(Utils.convertPassword(password));
        userInfo.setType(UserInfo.TYPE.ADMIN.getValue());

        UserGroup userGroup = new UserGroup();
        userGroup.setType(UserGroup.GroupType.CUSTOMER.getValue());
        userGroup.setSubGroupId(subUserGroup.getId());
        userGroup.setSubGroupName(subUserGroup.getName());
        userGroup.setSubGroupNo(subUserGroup.getGroupNo());
        userGroup.setCompanyId(subUserGroup.getCompanyId());

        userService.register(userGroup,userInfo);
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
            throw new BizFailException("用户状态不允许修改");
        }

        int count = userInfoDao.updateByPrimaryKeySelective(userInfo);

        if(count <=0) {
            throw new BizFailException("用户更新失败");
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
    public List<UserGroup> getUserGroupList(UserGroupRequest request) {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(Long.parseLong(request.getCompanyId()));
        if(userGroup.getType() == UserGroup.GroupType.SUPER.getValue()) {
            request.setCompanyId(null);
        }

        List<UserGroup> list = userGroupDao.select(Utils.bean2Map(request));
        return list;
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

    @Override
    public void edit(UserGroup userGroup) {
        int count = userGroupDao.updateByPrimaryKeySelective(userGroup);
        if(count<=0) {
            throw new BizFailException("update group failed");
        }
    }

    @Override
    @Transactional
    public void submit(Long userId, Long groupId) {
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);

        if(userInfo.getStatus() == UserStatus.NEW.getValue()) {
            int count = userInfoDao.updateStatusById(userId, UserStatus.NEW.getValue(),UserStatus.SUBMITED.getValue());
            if(count<=0) {
                throw new BizFailException("update userinfo failed");
            }
        }

        int count = userGroupDao.updateStatusById(groupId, GroupStatus.NEW.getValue(),GroupStatus.SUBMITED.getValue());
        if(count<=0) {
            throw new BizFailException("update user group failed");
        }
    }

    @Override
    public void saveBankCard(UserBankCard userBankCard) {
        SegmentLock<Long> lock = new SegmentLock<>();
        lock.lock(userBankCard.getGroupId());

        try {
            List<UserBankCard> list = userBankCardDao.selectByUser(userBankCard.getGroupId());
            list = list.stream().filter(card -> card.getStatus()== CardStatus.IN_USE.getValue()).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(list)) {
                userBankCard.setStatus(10);
            }

            userBankCardDao.insertSelective(userBankCard);
        } finally {
            lock.unlock(userBankCard.getGroupId());
        }

    }

    @Override
    public void saveAdminBankCard(AdminBankCard adminBankCard) {
        if(adminBankCard.getId()!= null) {
            int count = adminBankCardDao.updateByPrimaryKeySelective(adminBankCard);
            if(count<=0) {
                throw new BizFailException("update admin card failed");
            }
        } else {
            adminBankCardDao.insertSelective(adminBankCard);
        }
    }

    @Override
    public void userTurnBack(Long groupId, String remark) {
        int count = userGroupDao.updateStatusById(groupId,GroupStatus.SUBMITED.getValue(),GroupStatus.NEW.getValue());
        if(count <= 0) {
            throw new BizFailException("update user group failed");
        }
    }

    @Transactional
    @Override
    public void userPass(Long groupId) {
        //admin bank
        List<AdminBankCard> cards = adminBankCardDao.selectByGroupId(groupId);
        if(CollectionUtils.isEmpty(cards)) {
            throw new BizFailException("结算账号未设置");
        }

        List<UserChannel> channels = userChannelDao.selectByGroupId(groupId);
        if(CollectionUtils.isEmpty(channels)) {
            throw new BizFailException("用户渠道未设置");
        }

        int count = userGroupDao.updateStatusById(groupId,GroupStatus.SUBMITED.getValue(),GroupStatus.AVAILABLE.getValue());
        if(count<=0) {
            throw new BizFailException("update user group status failed");
        }
        UserInfoRequest request = new UserInfoRequest();
        request.setGroupId(groupId);
        request.setType(UserType.ADMIN.getValue());
        request.setStatus(UserStatus.SUBMITED.getValue());
        List<UserInfo> list =  userInfoDao.select(request);
        for(UserInfo userInfo:list) {
            count = userInfoDao.updateStatusById(userInfo.getId(),UserStatus.SUBMITED.getValue(),UserStatus.AVAILABLE.getValue());
            if(count<=0) {
                throw new BizFailException("update userInfo status failed");
            }
        }

        Account account = new Account();
        account.setGroupId(groupId);
        accountDao.insertSelective(account);
    }

    @Transactional
    @Override
    public void saveAminGroup(UserGroup userGroup) {
        userGroup.setType(hf.base.model.UserGroup.GroupType.COMPANY.getValue());
        userGroupDao.insertSelective(userGroup);
        AdminAccount adminAccount = new AdminAccount();
        adminAccount.setGroupId(userGroup.getId());
        adminAccountDao.insertSelective(adminAccount);
    }

    @Override
    public void saveUserGroup(UserGroup userGroup) {
        Long subGroupId = userGroup.getSubGroupId();
        UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(subGroupId);
        if(!Objects.isNull(subUserGroup) && subUserGroup.getStatus() == GroupStatus.AVAILABLE.getValue()) {
            userGroup.setSubGroupNo(subUserGroup.getGroupNo());
            userGroup.setSubGroupId(subUserGroup.getId());
            userGroup.setSubGroupName(subUserGroup.getName());
            Long companyId = (subUserGroup.getType() == GroupType.COMPANY.getValue() || subUserGroup.getType() == GroupType.SUPER.getValue()) ? subUserGroup.getId(): subUserGroup.getCompanyId();
            userGroup.setCompanyId(companyId);
        }

        userGroup.setStatus(GroupStatus.SUBMITED.getValue());

        if(!Objects.isNull(userGroup.getId())) {
            int count = userGroupDao.updateByPrimaryKeySelective(userGroup);
            if(count<=0) {
                throw new BizFailException("update user group failed");
            }
        } else {
            userGroupDao.insertSelective(userGroup);
        }
    }
}
