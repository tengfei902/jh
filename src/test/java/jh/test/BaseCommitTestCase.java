package jh.test;

import com.google.gson.Gson;
import jh.dao.local.UserGroupDao;
import jh.dao.local.UserInfoDao;
import jh.model.UserGroup;
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
    @Autowired
    private UserGroupDao userGroupDao;

    @Test
    public void saveUser() {
        String baseLoginId = "test";
        String baseUserName = "张三";

        for(int i=0;i<100;i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setTel("");
            userInfo.setLoginId(baseLoginId+"_"+i);
            userInfo.setPassword(Utils.convertPassword("123456"));
            userInfo.setAddress("");
            userInfo.setName(baseUserName+"_"+i);
            userInfo.setIdCard("622210001010200412");
            userInfo.setAdminId(0L);
            userInfo.setTel("13465787652");
            userInfo.setType(UserInfo.TYPE.ADMIN.getValue());
            userInfo.setUserNo(userInfo.getLoginId());
            userInfoDao.insertSelective(userInfo);

            UserInfo userInfo1 = userInfoDao.selectByPrimaryKey(userInfo.getId());

            //save agent
            for(int index=0;index<10;index++) {
                UserInfo userInfo2 = new UserInfo();
                userInfo2.setTel("");
                userInfo2.setLoginId(baseLoginId+"_"+i+"_"+index);
                userInfo2.setPassword(Utils.convertPassword("123456"));
                userInfo2.setAddress("");
                userInfo2.setName(baseUserName+"_"+i+"_"+index);
                userInfo2.setIdCard("622210001010200412");
                userInfo2.setAdminId(userInfo1.getId());
                userInfo2.setTel("13465787652");
                userInfo2.setType(UserInfo.TYPE.AGENT.getValue());
                userInfo2.setUserNo(userInfo2.getLoginId());
                userInfo2.setSubUserId(userInfo1.getId());
                userInfo2.setSubUserNo(userInfo1.getUserNo());
                userInfo2.setSubUserName(userInfo1.getName());
                userInfoDao.insertSelective(userInfo2);

                userInfo2 = userInfoDao.selectByPrimaryKey(userInfo.getId());

                for(int is = 0;is<10;is++) {
                    UserInfo userInfo3 = new UserInfo();
                    userInfo3.setTel("");
                    userInfo3.setLoginId(baseLoginId+"_"+i+"_"+index+"_"+is);
                    userInfo3.setPassword(Utils.convertPassword("123456"));
                    userInfo3.setAddress("");
                    userInfo3.setName(baseUserName+"_"+i+"_"+index+"_"+is);
                    userInfo3.setIdCard("622210001010200412");
                    userInfo3.setAdminId(userInfo1.getId());
                    userInfo3.setTel("13465787652");
                    userInfo3.setType(UserInfo.TYPE.CUSTOMER.getValue());
                    userInfo3.setUserNo(userInfo3.getLoginId());
                    userInfo3.setSubUserId(userInfo2.getId());
                    userInfo3.setSubUserNo(userInfo2.getUserNo());
                    userInfo3.setSubUserName(userInfo2.getName());
                    userInfoDao.insertSelective(userInfo3);
                }
            }

            //customer
            for(int index = 0;index<10;index++) {
                UserInfo userInfo4 = new UserInfo();
                userInfo4.setTel("");
                userInfo4.setLoginId(baseLoginId+"_"+i+"_"+index+"_customer");
                userInfo4.setPassword(Utils.convertPassword("123456"));
                userInfo4.setAddress("");
                userInfo4.setName(baseUserName+"_"+i+"_"+index+"_customer");
                userInfo4.setIdCard("622210001010200412");
                userInfo4.setAdminId(userInfo1.getId());
                userInfo4.setTel("13465787652");
                userInfo4.setType(UserInfo.TYPE.CUSTOMER.getValue());
                userInfo4.setUserNo(userInfo4.getLoginId());
                userInfo4.setSubUserId(userInfo1.getId());
                userInfo4.setSubUserNo(userInfo1.getUserNo());
                userInfo4.setSubUserName(userInfo1.getName());
                userInfoDao.insertSelective(userInfo4);
            }
        }
    }

    @Test
    public void testSaveUserGroup() {
        UserGroup userGroup = new UserGroup();


    }
}
