package jh.test.user;

import com.google.gson.Gson;
import jh.biz.UserBiz;
import jh.dao.local.UserInfoDao;
import jh.model.UserInfo;
import jh.test.BaseTestCase;
import jh.utils.Utils;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tengfei on 2017/10/29.
 */
public class UserTest extends BaseTestCase {
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private UserInfoDao userInfoDao;

    @Test
    public void register() {
        Long userId = userBiz.register("tengfei","tengfei881122","teng20061001@126.com");
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);
        System.out.println(new Gson().toJson(userInfo));
    }

    @Test
    public void login() {
        Assert.assertFalse(userBiz.login("tengfei","tengfei881122"));

        register();

        Assert.assertTrue(userBiz.login("tengfei","tengfei881122"));
    }

    @Test
    public void testCrypt() {
        String password = "tengfei881122";
        System.out.println(Utils.convertPassword(password));
        System.out.println(Utils.convertPassword(password));
        System.out.println(Utils.convertPassword(password));
        System.out.println(Utils.convertPassword(password));
    }
}
