package jh.biz.service;

import jh.model.po.UserGroup;
import jh.model.po.UserInfo;

import java.util.List;

public interface UserService {
    void register(UserGroup userGroup,UserInfo userInfo);
    List<UserGroup> getChildMchIds(Long groupId);
    List<UserGroup> getChildCompany(Long groupId);
}
