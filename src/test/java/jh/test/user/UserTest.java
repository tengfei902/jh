package jh.test.user;

import com.google.gson.Gson;
import hf.base.utils.Utils;
import jh.biz.UserBiz;
import jh.dao.local.UserGroupDao;
import jh.dao.local.UserInfoDao;
import jh.model.po.UserGroup;
import jh.model.po.UserInfo;
import jh.test.BaseTestCase;
import org.junit.Assert;
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
    @Autowired
    private UserGroupDao userGroupDao;

    @Test
    public void testRegister() {
        String loginId = "test";
        String password = "123456";
        String inviteCode = "";
        userBiz.register(loginId,password,inviteCode);

        UserInfo userInfo = userInfoDao.selectByLoginId(loginId, Utils.convertPassword(password));
        Assert.assertNotNull(userInfo);
        Assert.assertEquals(userInfo.getStatus().intValue(),UserInfo.STATUS.init.getValue());
        Assert.assertEquals(userInfo.getType().intValue(),UserInfo.TYPE.ADMIN.getValue());

        System.out.println(new Gson().toJson(userInfo));

        UserGroup userGroup = userGroupDao.selectByPrimaryKey(userInfo.getGroupId());
        Assert.assertEquals(userGroup.getSubGroupId().longValue(),1L);
        Assert.assertEquals(userGroup.getType().intValue(),UserGroup.GroupType.CUSTOMER.getValue());

        System.out.println(new Gson().toJson(userGroup));
    }
}
