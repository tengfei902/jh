package jh.biz;

/**
 * Created by tengfei on 2017/10/29.
 */
public interface UserBiz {

    Long register(String username,String password,String email);

    boolean login(String loginId,String password);
}
