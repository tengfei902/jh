package jh.biz;

import jh.model.UserInfo;

/**
 * Created by tengfei on 2017/10/29.
 */
public interface UserBiz {

    Long register(String username,String password,String subUserId);

    boolean login(String loginId,String password);

    void edit(UserInfo userInfo);
}
