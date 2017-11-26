package jh.test;

import hf.base.enums.*;
import hf.base.utils.MapUtils;
import hf.base.utils.Utils;
import jh.dao.local.*;
import jh.model.po.*;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private UserBankCardDao userBankCardDao;
    @Autowired
    private AdminBankCardDao adminBankCardDao;

    @Test
    public void testSaveUserGroup() {
        UserGroup userGroup = new UserGroup();

        userGroup.setIdCard("37132612641275151");
        userGroup.setAddress("上海市浦东新区");
        userGroup.setGroupNo("XTASDYAF1234567");
        userGroup.setName("慧富");
        userGroup.setType(UserGroup.GroupType.SUPER.getValue());
        userGroup.setTel("1234567813");

        userGroupDao.insertSelective(userGroup);

        userGroup = userGroupDao.selectByPrimaryKey(userGroup.getId());

        //add 分公司
        for(int i=0;i<10;i++) {
            UserGroup userGroup2 = new UserGroup();

            userGroup2.setIdCard("37132612641275151");
            userGroup2.setAddress("上海市浦东新区");
            userGroup2.setGroupNo("XTASDYAF1234569_"+i);
            userGroup2.setName("分公司_"+i);
            userGroup2.setType(UserGroup.GroupType.COMPANY.getValue());
            userGroup2.setTel("1234567813");
            userGroup2.setSubGroupId(userGroup.getId());
            userGroup2.setSubGroupNo(userGroup.getGroupNo());
            userGroup2.setSubGroupName(userGroup.getName());
            userGroup2.setCompanyId(userGroup.getCompanyId());
            userGroupDao.insertSelective(userGroup2);

            userGroup2 = userGroupDao.selectByPrimaryKey(userGroup2.getId());

            UserInfo userInfo2 = new UserInfo();
            userInfo2.setGroupId(userGroup2.getId());
            userInfo2.setLoginId("part_admin_"+i);
            userInfo2.setName("分公司管理员_"+i);
            userInfo2.setPassword(Utils.convertPassword("123456"));
            userInfo2.setTel("12342536768");
            userInfo2.setType(UserInfo.TYPE.ADMIN.getValue());
            userInfo2.setQq("321567545412");

            userInfoDao.insertSelective(userInfo2);

            UserInfo userInfo2_1 = new UserInfo();
            userInfo2_1.setGroupId(userGroup2.getId());
            userInfo2_1.setLoginId("part_user_"+i);
            userInfo2_1.setName("分公司普通用户_"+i);
            userInfo2_1.setPassword(Utils.convertPassword("123456"));
            userInfo2_1.setTel("12342536768");
            userInfo2_1.setType(UserInfo.TYPE.CUSTOMER.getValue());
            userInfo2_1.setQq("321567545412");

            userInfoDao.insertSelective(userInfo2_1);

            //add 代理商
            for(int index= 0 ;index<10;index++) {
                UserGroup userGroup3 = new UserGroup();

                userGroup3.setIdCard("37132612641275151");
                userGroup3.setAddress("上海市浦东新区");
                userGroup3.setGroupNo("XTASDYAF1234569_"+i+"_"+index);
                userGroup3.setName("代理商_"+i+"_"+index);
                userGroup3.setType(UserGroup.GroupType.AGENT.getValue());
                userGroup3.setTel("1234567813");
                userGroup3.setSubGroupId(userGroup2.getId());
                userGroup3.setSubGroupNo(userGroup2.getGroupNo());
                userGroup3.setSubGroupName(userGroup2.getName());
                userGroup3.setCompanyId(userGroup2.getId());
                userGroupDao.insertSelective(userGroup3);

                UserInfo userInfo3 = new UserInfo();
                userInfo3.setGroupId(userGroup3.getId());
                userInfo3.setLoginId("agent_admin_"+i+"_"+index);
                userInfo3.setName("代理商管理员_"+i+"_"+index);
                userInfo3.setPassword(Utils.convertPassword("123456"));
                userInfo3.setTel("12342536768");
                userInfo3.setType(UserInfo.TYPE.ADMIN.getValue());
                userInfo3.setQq("321567545412");

                userInfoDao.insertSelective(userInfo3);

                UserInfo userInfo3_1 = new UserInfo();
                userInfo3_1.setGroupId(userGroup3.getId());
                userInfo3_1.setLoginId("agent_user_"+i+"_"+index);
                userInfo3_1.setName("分公司普通用户_"+i+"_"+index);
                userInfo3_1.setPassword(Utils.convertPassword("123456"));
                userInfo3_1.setTel("12342536768");
                userInfo3_1.setType(UserInfo.TYPE.CUSTOMER.getValue());
                userInfo3_1.setQq("321567545412");

                userInfoDao.insertSelective(userInfo3_1);

                //add 普通商户
                for(int index2 = 0;index2<10;index2++) {
                    UserGroup userGroup4 = new UserGroup();

                    userGroup4.setIdCard("37132612641275151");
                    userGroup4.setAddress("上海市浦东新区");
                    userGroup4.setGroupNo("XTASDYAF1234569_"+i+"_"+index+"_"+index2);
                    userGroup4.setName("普通商户_"+i+"_"+index+"_"+index2);
                    userGroup4.setType(UserGroup.GroupType.CUSTOMER.getValue());
                    userGroup4.setTel("1234567813");
                    userGroup4.setSubGroupId(userGroup3.getId());
                    userGroup4.setSubGroupNo(userGroup3.getGroupNo());
                    userGroup4.setSubGroupName(userGroup3.getName());
                    userGroup4.setCompanyId(userGroup2.getId());
                    userGroupDao.insertSelective(userGroup4);

                    UserInfo userInfo4 = new UserInfo();
                    userInfo4.setGroupId(userGroup4.getId());
                    userInfo4.setLoginId("customer_admin_"+i+"_"+index+"_"+index2);
                    userInfo4.setName("普通商户管理员_"+i+"_"+index+"_"+index2);
                    userInfo4.setPassword(Utils.convertPassword("123456"));
                    userInfo4.setTel("12342536768");
                    userInfo4.setType(UserInfo.TYPE.ADMIN.getValue());
                    userInfo4.setQq("321567545412");

                    userInfoDao.insertSelective(userInfo4);

                    UserInfo userInfo4_1 = new UserInfo();
                    userInfo4_1.setGroupId(userGroup4.getId());
                    userInfo4_1.setLoginId("customer_user_"+i+"_"+index+"_"+index2);
                    userInfo4_1.setName("普通商户普通用户_"+i+"_"+index);
                    userInfo4_1.setPassword(Utils.convertPassword("123456"));
                    userInfo4_1.setTel("12342536768");
                    userInfo4_1.setType(UserInfo.TYPE.CUSTOMER.getValue());
                    userInfo4_1.setQq("321567545412");

                    userInfoDao.insertSelective(userInfo4_1);
                }
            }

        }

        for(int i=0;i<3;i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setGroupId(userGroup.getId());
            userInfo.setLoginId("root_admin_"+i);
            userInfo.setName("总部管理员_"+i);
            userInfo.setPassword(Utils.convertPassword("123456"));
            userInfo.setTel("12342536768");
            userInfo.setType(UserInfo.TYPE.ADMIN.getValue());
            userInfo.setQq("321567545412");

            userInfoDao.insertSelective(userInfo);
        }

        for(int i=0;i<10;i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setGroupId(userGroup.getId());
            userInfo.setLoginId("root_user_"+i);
            userInfo.setName("总部普通用户_"+i);
            userInfo.setPassword(Utils.convertPassword("123456"));
            userInfo.setTel("12342536768");
            userInfo.setType(UserInfo.TYPE.CUSTOMER.getValue());
            userInfo.setQq("321567545412");

            userInfoDao.insertSelective(userInfo);
        }
    }

    @Test
    public void testSaveAgentGroup() {
        UserGroup superGroup = userGroupDao.selectByGroupNo("XTASDYAF1234567");

        for(int i=0;i<10;i++) {
            UserGroup userGroup = new UserGroup();

            userGroup.setIdCard("37132612641275151");
            userGroup.setAddress("上海市浦东新区");
            userGroup.setGroupNo("XTASDYAF1234569");
            userGroup.setName("代理商_"+i);
            userGroup.setType(UserGroup.GroupType.AGENT.getValue());
            userGroup.setTel("1234567813");
            userGroup.setSubGroupId(superGroup.getId());
            userGroup.setSubGroupNo(superGroup.getGroupNo());
            userGroup.setSubGroupName(superGroup.getName());
            userGroup.setCompanyId(superGroup.getCompanyId());
            userGroupDao.insertSelective(userGroup);

            userGroup = userGroupDao.selectByPrimaryKey(userGroup.getId());

            for(int index=0;index<10;index++) {

                UserGroup userGroup2 = new UserGroup();

                userGroup2.setIdCard("37132612641275151");
                userGroup2.setAddress("上海市浦东新区");
                userGroup2.setGroupNo("XTASDYAF1234569");
                userGroup2.setName("普通商户_"+i+"_"+index);
                userGroup2.setType(UserGroup.GroupType.CUSTOMER.getValue());
                userGroup2.setTel("1234567813");
                userGroup2.setSubGroupId(userGroup.getId());
                userGroup2.setSubGroupNo(userGroup.getGroupNo());
                userGroup2.setSubGroupName(userGroup.getName());
                userGroup2.setCompanyId(userGroup.getCompanyId());
                userGroupDao.insertSelective(userGroup2);
            }
        }
    }

    @Test
    public void saveChannel() {
        Channel channel = new Channel();
        channel.setChannelName("阿是大富大贵");
        channel.setChannelCode(ChannelCode.QQ.getService());
        channel.setFeeRate(new BigDecimal("10"));
        channel.setUrl("");

        channelDao.insertSelective(channel);
    }

    @Test
    public void testBuildAdmin() {
        UserGroup userGroup = new UserGroup();
        userGroup.setType(UserGroup.GroupType.SUPER.getValue());
        userGroup.setOwnerName("test");
        userGroup.setTel("15920530607");
        userGroup.setName("慧富");
        userGroup.setIdCard("");
        userGroup.setGroupNo("");
        userGroup.setSubGroupId(0L);
        userGroup.setSubGroupName("");
        userGroup.setCompanyId(0L);
        userGroup.setAddress("湖南省长沙市");
        userGroup.setSubGroupNo("");
        userGroup.setStatus(GroupStatus.AVAILABLE.getValue());

        userGroupDao.insertSelective(userGroup);

        UserInfo userInfo = new UserInfo();
        userInfo.setGroupId(userGroup.getId());
        userInfo.setAddress("湖南省长沙市");
        userInfo.setQq("3396665157");
        userInfo.setTel("15920530607");
        userInfo.setLoginId("admin");
        userInfo.setPassword(Utils.convertPassword("123456"));
        userInfo.setName("管理员");
        userInfo.setType(UserType.ADMIN.getValue());
        userInfoDao.insertSelective(userInfo);

        Account account = new Account();
        account.setGroupId(userGroup.getId());
        accountDao.insertSelective(account);

        AdminAccount adminAccount = new AdminAccount();
        adminAccount.setGroupId(userGroup.getId());
        adminAccountDao.insertSelective(adminAccount);
    }

    @Test
    public void testSavePay() {
        List<UserGroup> userGroups = userGroupDao.select(MapUtils.buildMap("status",10));

        for(UserGroup userGroup:userGroups) {

            for(int i=0;i<100;i++) {
                PayRequest payRequest = new PayRequest();
                payRequest.setService(ChannelCode.QQ.getService());
                payRequest.setActualAmount(new BigDecimal(i+100));
                payRequest.setFee(new BigDecimal(i));
                payRequest.setMchId(userGroup.getGroupNo());
                payRequest.setOutTradeNo(String.valueOf(RandomUtils.nextLong()));
                payRequest.setTotalFee(payRequest.getFee().add(payRequest.getActualAmount()).intValue());
                payRequest.setBody("test trd body");
                payRequest.setSign("test Sig");
                payRequest.setAppid("123456");
                payRequest.setBuyerId("1234");
                payRequest.setSubOpenid("12364567");
                payRequestDao.insertSelective(payRequest);
            }

        }
    }

    @Test
    public void prepareAccountData() {
        List<UserGroup> userGroups = userGroupDao.select(MapUtils.buildMap("status",10));
        int i=0;
        for(UserGroup userGroup:userGroups) {
            Account account2 = accountDao.selectByGroupId(userGroup.getId());
            if(Objects.isNull(account2)) {
                Account account = new Account();
                account.setGroupId(userGroup.getId());
                account.setAmount(new BigDecimal(1000+100*i));
                account.setLockAmount(new BigDecimal(RandomUtils.nextInt(1000)));
                account.setFee(new BigDecimal(RandomUtils.nextInt(100)));
                account.setTotalAmount(new BigDecimal(RandomUtils.nextInt(10000)));
                account.setPaidAmount(new BigDecimal(RandomUtils.nextInt(10000)));
                accountDao.insertSelective(account);
            }
            account2 = accountDao.selectByGroupId(userGroup.getId());

            i++;

            for(int index=0;index<10;index++) {
                AccountOprLog accountOprLog = new AccountOprLog();
                accountOprLog.setGroupId(account2.getGroupId());
                accountOprLog.setAccountId(account2.getId());
                accountOprLog.setOutTradeNo(String.valueOf(RandomUtils.nextLong()));
                accountOprLog.setAmount(new BigDecimal(100+10*index));
                accountOprLog.setType(OprType.PAY.getValue());
                accountOprLog.setStatus(OprStatus.PAY_SUCCESS.getValue());
                accountOprLog.setRemark("");
                accountOprLogDao.insertSelective(accountOprLog);
            }
        }
    }

    @Test
    public void testSaveBankCard() {
        UserBankCard userBankCard = new UserBankCard();
        userBankCard.setBank("中国建设银行");
        userBankCard.setBankNo("622345672234532");
        userBankCard.setCity("上海");
        userBankCard.setDeposit("上海支行");
        userBankCard.setGroupId(8L);
        userBankCard.setOwner("test");
        userBankCard.setProvince("上海");
        userBankCardDao.insertSelective(userBankCard);
    }

    @Test
    public void testSaveAdminBankCard() {
        AdminBankCard userBankCard = new AdminBankCard();
        userBankCard.setBank("中国建设银行");
        userBankCard.setBankNo("622345672234532");
        userBankCard.setCity("上海");
        userBankCard.setDeposit("上海支行");
        userBankCard.setGroupId(8L);
        userBankCard.setCompanyId(1L);
        userBankCard.setOwner("test");
        userBankCard.setProvince("上海");
        adminBankCardDao.insertSelective(userBankCard);
    }
}
