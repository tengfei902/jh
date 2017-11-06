package jh.test;

import jh.dao.local.UserInfoDao;
import jh.model.UserInfo;
import jh.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by tengfei on 2017/11/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:/META-INF/spring/app-bootstrap.xml"})
public class BaseCommitTestCase {
    @Autowired
    private UserInfoDao userInfoDao;

    @Test
    public void saveUser() {
        UserInfo userInfo = new UserInfo();

        userInfo.setSubUserId(0L);
        userInfo.setTel("");
        userInfo.setLoginId("admin");
        userInfo.setPassword(Utils.convertPassword("123456"));
        userInfo.setAddress("");
        userInfo.setName("test");
        userInfo.setIdCard("622210001010200412");

        userInfoDao.insertSelective(userInfo);
    }
}
