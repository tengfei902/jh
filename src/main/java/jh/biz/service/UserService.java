package jh.biz.service;

import jh.model.po.UserGroup;
import jh.model.po.UserInfo;

public interface UserService {
    void register(UserGroup userGroup,UserInfo userInfo);
}
