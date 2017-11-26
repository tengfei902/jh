package jh.test.user;

import com.google.gson.Gson;
import hf.base.enums.GroupType;
import hf.base.exceptions.BizFailException;
import hf.base.utils.MapUtils;
import hf.base.utils.SegmentLock;
import hf.base.utils.TypeConverter;
import hf.base.utils.Utils;
import jh.biz.ChannelBiz;
import jh.biz.UserBiz;
import jh.dao.local.*;
import jh.model.po.*;
import jh.test.BaseTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private UserBankCardDao userBankCardDao;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private ChannelBiz channelBiz;
    @Autowired
    private AdminBankCardDao adminBankCardDao;

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

    @Test
    public void testEditUserGroup() {
        Map<String,Object> data = MapUtils.buildMap("groupId",8,"name","泛微网络","address","上海市闵行区","ownerName","滕飞","tel","13611681327","idCard","37123619881122005X");
        try {
            UserGroup userGroup = TypeConverter.convert(data, UserGroup.class);
            userBiz.edit(userGroup);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizFailException(e);
        }

        UserGroup userGroup = userGroupDao.selectByPrimaryKey(8L);
        Assert.assertEquals(userGroup.getName(),data.get("name"));
        Assert.assertEquals(userGroup.getAddress(),data.get("address"));
        Assert.assertEquals(userGroup.getOwnerName(),data.get("ownerName"));
        Assert.assertEquals(userGroup.getTel(),data.get("tel"));

    }

    @Test
    public void selectUserBankCards() {
        userBankCardDao.selectByUser(8L);
    }

    @Test
    public void testSaveBankCard() {
        Map<String,Object> map = MapUtils.buildMap("groupId",8,
                "bankNo","62222222222222",
                "bank","中国银行",
                "deposit","浦东分行",
                "owner","滕飞",
                "province","上海",
                "city","上海");
        try {
            UserBankCard userBankCard = TypeConverter.convert(map,UserBankCard.class);
            userBiz.saveBankCard(userBankCard);
            userBankCard = userBankCardDao.selectByPrimaryKey(userBankCard.getId());
            Assert.assertEquals(userBankCard.getStatus().intValue(),10);

            map.put("bankNo","63323125421525");
            userBankCard = TypeConverter.convert(map,UserBankCard.class);
            userBiz.saveBankCard(userBankCard);
            userBankCard = userBankCardDao.selectByPrimaryKey(userBankCard.getId());
            Assert.assertEquals(userBankCard.getStatus().intValue(),0);
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
    }

    @Test
    public void testLock() {

        SegmentLock<String> segmentLock = new SegmentLock<>();
        String key = "abcdefg";
        segmentLock.lock(key);

        segmentLock.unlock(key);
    }

    @Test
    public void testSubmit() {
        Long userId = 8L;
        Long groupId = 5L;

        userBiz.submit(userId,groupId);

        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);
        Assert.assertEquals(userGroup.getStatus().intValue(),1);

        UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);
        Assert.assertEquals(userInfo.getStatus().intValue(),1);
    }

    @Test
    public void testSaveUserChannel() {

        UserChannel userChannel = new UserChannel();
        userChannel.setCallbackUrl("www.baidu.com");
        userChannel.setCipherCode("erweeqwrerhgrereg");
        userChannel.setChannelId(10L);
        userChannel.setFeeRate(new BigDecimal(10));
        userChannel.setGroupId(10L);
        userChannel.setMchId("12562wefefdg");

        channelBiz.saveUserChannel(userChannel);

        UserChannel userChannel1 = userChannelDao.selectByPrimaryKey(userChannel.getId());
        System.out.println(new Gson().toJson(userChannel1));
    }

    @Test
    public void testGetChannel() {
        List<Channel> channels = channelDao.selectForList();
        System.out.println(new Gson().toJson(channels));
    }

    @Test
    public void testGetBankCard() {
        AdminBankCard adminBankCard = adminBankCardDao.selectByCompanyId(10L,102L);
    }

    @Test
    public void testUserPass() {
        userBiz.userPass(10L);
    }

    @Test
    public void testGetUserGroup() {
        Map<String,String> params = new HashMap<>();
        params.put("groupId","1");
        List<UserGroup> list = userGroupDao.select(hf.base.utils.MapUtils.buildMap("exceptGroupId",params.get("groupId")));
        List<Map<String,Object>> result = new ArrayList<>();
        list.stream().forEach(userGroup -> {
            result.add(hf.base.utils.MapUtils.buildMap("id",userGroup.getId(),"name",userGroup.getName()));
        });
    }

    @Test
    public void testSaveUserGroup() {
        Map<String,Object> map = new HashMap<>();
        map.put("name","test代理");
        map.put("ownerName","滕飞");
        map.put("idCard","3712361988222345");
        map.put("tel","13611681327");
        map.put("address","我当时的地方");
        map.put("type", GroupType.AGENT.getValue());
        map.put("subGroupId","1");

        try {
            UserGroup userGroup = TypeConverter.convert(map,UserGroup.class);
            userBiz.saveUserGroup(userGroup);

            UserGroup userGroup1 = userGroupDao.selectByPrimaryKey(userGroup.getId());
            System.out.println(new Gson().toJson(userGroup1));

            map.put("name","test代理1");
            map.put("groupId",userGroup1.getId());

            userGroup = TypeConverter.convert(map,UserGroup.class);
            userBiz.saveUserGroup(userGroup);

            userGroup = userGroupDao.selectByPrimaryKey(userGroup.getId());
            System.out.println(new Gson().toJson(userGroup));
            Assert.assertEquals(userGroup.getName(),"test代理1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
