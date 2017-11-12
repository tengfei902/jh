package jh.test.user;

import com.google.gson.Gson;
import jh.api.UserController;
import jh.biz.UserBiz;
import jh.dao.local.UserGroupDao;
import jh.dao.local.UserInfoDao;
import jh.model.UserGroup;
import jh.model.UserInfo;
import jh.model.dto.UserGroupDto;
import jh.model.dto.UserGroupRequest;
import jh.model.dto.UserInfoDto;
import jh.model.dto.UserInfoRequest;
import jh.test.BaseTestCase;
import jh.utils.Utils;
import junit.framework.Assert;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private UserGroupDao userGroupDao;

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

    @Test
    public void testMd5() {
        String md5 = DigestUtils.md5Hex("tengfei");
        String md5_1 = DigestUtils.md5Hex("tengfei");
        String md5_2 = DigestUtils.md5Hex("tengfei");
        String md5_3 = DigestUtils.md5Hex("123456");
        System.out.println(md5);
        System.out.println(md5_1);
        System.out.println(md5_2);
        System.out.println(md5_3);
    }

    @Test
    public void testGetUserGroupList() {
        UserGroupRequest request = new UserGroupRequest();
        request.setCompanyId("2");

        List<UserGroup> list = userGroupDao.select(Utils.bean2Map(request));

        Assert.assertEquals(list.size(),110);

        request.setType(UserGroup.GroupType.AGENT.getValue());
        list = userGroupDao.select(Utils.bean2Map(request));

        Assert.assertEquals(list.size(),10);

        List<UserGroupDto> userGroupDtos = userBiz.getUserGroupList(request);
        Assert.assertEquals(userGroupDtos.size(),10);

        request = new UserGroupRequest();
        request.setCompanyId("1");
        userGroupDtos = userBiz.getUserGroupList(request);
        Assert.assertEquals(userGroupDtos.size(),1111);
    }

    @Test
    public void testGetUserList() {
        UserInfoRequest request =new UserInfoRequest();
        request.setAdminId("2");
        List<UserInfoDto> list = userBiz.getUserList(request);

        List<UserGroup> userGroups = userGroupDao.selectByCompanyId(2L);

        List<Long> groupIds = userGroups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());

        for(UserInfoDto userInfoDto:list) {
            Long userId = userInfoDto.getUserId();
            UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);
            System.out.println(userInfo.getId());
            Assert.assertTrue(groupIds.contains(userInfo.getGroupId()) || userInfo.getGroupId() == 2L);
        }
    }
}
