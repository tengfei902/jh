package jh.test.user;

import com.google.gson.Gson;
import jh.api.UserController;
import jh.biz.UserBiz;
import jh.dao.local.UserInfoDao;
import jh.model.UserInfo;
import jh.test.BaseTestCase;
import jh.utils.Utils;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.*;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * Created by tengfei on 2017/10/29.
 */
public class UserTest extends BaseTestCase {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private UserController userController;
    @Autowired
    private UserBiz userBiz;

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

    @Test
    public void testResgister() {
        String username = "test";
        String password = "1234567";
        String subUserId = "1";
        Long userId = userBiz.register(username,password,subUserId);
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);

        System.out.println(new Gson().toJson(userInfo));
    }

    @Test
    public void testGetAnnotations() throws Exception {
        Field[] fields = UserInfo.class.getDeclaredFields();

        for(Field field:fields) {
            jh.model.annotations.Field field1 = field.getDeclaredAnnotation(jh.model.annotations.Field.class);
            System.out.println("---------");
        }

        BeanInfo beanInfo = Introspector.getBeanInfo(UserInfo.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for(PropertyDescriptor propertyDescriptor:propertyDescriptors) {
            
        }
    }
}
