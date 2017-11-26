package jh.biz.service.impl;

import hf.base.enums.GroupType;
import jh.biz.service.UserService;
import jh.dao.local.UserGroupDao;
import jh.dao.local.UserInfoDao;
import jh.model.po.UserGroup;
import jh.model.po.UserInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public List<UserGroup> getChildMchIds(Long groupId) {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);
        GroupType groupType = GroupType.parse(userGroup.getType());

        List<UserGroup> allGroups = new ArrayList<>();

        switch (groupType) {
            case AGENT:
                allGroups.add(userGroup);
                allGroups = getChild(allGroups,userGroup);
                break;
            case CUSTOMER:
                allGroups.add(userGroup);
                allGroups = getChild(allGroups,userGroup);
                break;
            case SUPER:
                break;
            case COMPANY:
                allGroups = userGroupDao.selectByCompanyId(userGroup.getId());
                break;
        }
        return allGroups;
    }

    private List<UserGroup> getChild(List<UserGroup> list,UserGroup userGroup) {
        List<UserGroup> children = userGroupDao.selectBySubGroupId(userGroup.getId());
        if(CollectionUtils.isEmpty(children)) {
            return list;
        }

        list.addAll(children);

        for(UserGroup child:children) {
            getChild(list,child);
        }
        return list;
    }

    @Override
    public List<UserGroup> getChildCompany(Long groupId) {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);
        if(Objects.isNull(userGroup) || (userGroup.getType() != GroupType.SUPER.getValue() && userGroup.getType() != GroupType.COMPANY.getValue())) {
            return Collections.EMPTY_LIST;
        }

        List<UserGroup> list = new ArrayList<>();
        list.add(userGroup);

        getAdminChild(list,userGroup);
        return list;
    }

    private List<UserGroup> getAdminChild(List<UserGroup> list,UserGroup userGroup) {
        List<UserGroup> children = userGroupDao.selectAdminBySubGroupId(userGroup.getId());

        if(CollectionUtils.isEmpty(children)) {
            return list;
        }

        list.addAll(children);

        for(UserGroup child:children) {
            getAdminChild(list,child);
        }
        return list;
    }
}
