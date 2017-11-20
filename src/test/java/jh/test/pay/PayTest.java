package jh.test.pay;

import com.google.gson.Gson;
import hf.base.enums.ChannelCode;
import jh.biz.PayBiz;
import jh.biz.service.PageService;
import jh.biz.service.PayService;
import jh.dao.local.*;
import jh.model.dto.PayRequestDto;
import jh.model.po.*;
import jh.test.BaseTestCase;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tengfei on 2017/10/29.
 */
public class PayTest extends BaseTestCase {
    @Autowired
    @Qualifier("ysPayBiz")
    private PayBiz payBiz;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private PayService payService;
    @Autowired
    private AccountOprLogDao accountOprLogDao;

    @Test
    public void testPay() {
        PayRequestDto payRequest = new PayRequestDto();
        payRequest.setAppid(String.valueOf(RandomUtils.nextLong()));
        payRequest.setBody("345676we");
        payRequest.setBuyer_id(String.valueOf(RandomUtils.nextLong()));
        payRequest.setMch_id(String.valueOf(RandomUtils.nextLong()));
        payRequest.setOut_trade_no(String.valueOf(RandomUtils.nextLong()));
        payRequest.setService(ChannelCode.ALI.getService());
        payRequest.setAppid(String.valueOf(RandomUtils.nextLong()));
        payRequest.setSign(String.valueOf(RandomUtils.nextLong()));
        payBiz.pay(payRequest);
    }

    @Test
    public void testSaveOprLog() {
        Long groupId = prepareData();
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);

        PayRequestDto payRequest = new PayRequestDto();
        payRequest.setAppid(String.valueOf(RandomUtils.nextLong()));
        payRequest.setBody("345676qwewe");
        payRequest.setBuyer_id(String.valueOf(RandomUtils.nextLong()));
        payRequest.setMch_id(userGroup.getGroupNo());
        payRequest.setOut_trade_no(String.valueOf(RandomUtils.nextLong()));
        payRequest.setService(ChannelCode.QQ.getService());
        payRequest.setAppid(String.valueOf(RandomUtils.nextLong()));
        payRequest.setSign(String.valueOf(RandomUtils.nextLong()));
        payRequest.setTotal_fee(10000);

        PayRequest request = new PayRequest(payRequest);
        payRequestDao.insertSelective(request);

        payService.saveOprLog(request);

        List<AccountOprLog> logs = accountOprLogDao.selectByTradeNo(payRequest.getOut_trade_no());

        for(AccountOprLog log:logs) {
            System.out.println(new Gson().toJson(log));
        }
    }

    public Long prepareData() {
        Channel channel = new Channel();
        channel.setChannelName("test");
        channel.setChannelCode(ChannelCode.QQ.getService());
        channel.setUrl("www.baidu.com");
        channel.setFeeRate(new BigDecimal("5.5"));
        channelDao.insertSelective(channel);

        UserGroup superUserGroup = new UserGroup();
        superUserGroup.setGroupNo(String.valueOf(RandomUtils.nextLong()));
        superUserGroup.setIdCard("37132619881122005X");
        superUserGroup.setName("test_");
        superUserGroup.setTel("1235554313456");
        superUserGroup.setOwnerName("test_");
        superUserGroup.setAddress("上海市浦东新区");
        superUserGroup.setCompanyId(0L);
        superUserGroup.setType(UserGroup.GroupType.SUPER.getValue());
        superUserGroup.setSubGroupName("");
        superUserGroup.setSubGroupId(0L);
        superUserGroup.setSubGroupNo("");

        userGroupDao.insertSelective(superUserGroup);

        superUserGroup = userGroupDao.selectByPrimaryKey(superUserGroup.getId());

        Account superAccount = new Account();
        superAccount.setGroupId(superUserGroup.getId());
        accountDao.insertSelective(superAccount);

        UserChannel userChannel = new UserChannel();
        userChannel.setGroupId(superUserGroup.getId());
        userChannel.setChannelId(channel.getId());
        userChannel.setMchId(superUserGroup.getGroupNo());
        userChannel.setFeeRate(new BigDecimal("8"));
        userChannel.setSubFeeRate(new BigDecimal("0"));
        userChannel.setSubGroupId(0L);
        userChannel.setCompanyId(0L);
        userChannel.setStandardFeeRate(new BigDecimal("0"));
        userChannel.setChannelName(channel.getChannelName());
        userChannel.setChannelCode(channel.getChannelCode());
        userChannel.setGroupName("test");
        userChannel.setCipherCode("123456786");

        userChannelDao.insertSelective(userChannel);

        UserGroup subGroup = superUserGroup;

         for(int i=0;i<10;i++) {
             UserGroup userGroup = new UserGroup();
             userGroup.setGroupNo(String.valueOf(RandomUtils.nextLong()));
             userGroup.setIdCard("37132619881122005X");
             userGroup.setName("test_"+i);
             userGroup.setTel("1235554313456");
             userGroup.setOwnerName("test_"+i);
             userGroup.setAddress("上海市浦东新区");
             userGroup.setType(UserGroup.GroupType.AGENT.getValue());
             userGroup.setCompanyId(superUserGroup.getId());
             userGroup.setSubGroupName(subGroup.getName());
             userGroup.setSubGroupId(subGroup.getId());
             userGroup.setSubGroupNo(subGroup.getGroupNo());

             userGroupDao.insertSelective(userGroup);
             subGroup = userGroupDao.selectByPrimaryKey(userGroup.getId());

             Account account = new Account();
             account.setGroupId(userGroup.getId());
             accountDao.insertSelective(account);

             UserChannel userChannel1 = new UserChannel();
             userChannel1.setGroupId(userGroup.getId());
             userChannel1.setChannelId(channel.getId());
             userChannel1.setMchId(userGroup.getGroupNo());
             userChannel1.setFeeRate(new BigDecimal("8").add(new BigDecimal(i+1)));
             userChannel1.setSubFeeRate(new BigDecimal("0"));
             userChannel1.setSubGroupId(subGroup.getId());
             userChannel1.setCompanyId(superUserGroup.getId());
             userChannel1.setStandardFeeRate(channel.getFeeRate());
             userChannel1.setChannelName(channel.getChannelName());
             userChannel1.setChannelCode(channel.getChannelCode());
             userChannel1.setGroupName(userGroup.getName());
             userChannel1.setCipherCode("123456786");

             userChannelDao.insertSelective(userChannel1);
         }

         //customer
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupNo(String.valueOf(RandomUtils.nextLong()));
        userGroup.setIdCard("37132619881122005X");
        userGroup.setName("test_customer");
        userGroup.setTel("1235554313456");
        userGroup.setOwnerName("test_customer");
        userGroup.setAddress("上海市浦东新区");
        userGroup.setType(UserGroup.GroupType.CUSTOMER.getValue());
        userGroup.setCompanyId(superUserGroup.getId());
        userGroup.setSubGroupName(subGroup.getName());
        userGroup.setSubGroupId(subGroup.getId());
        userGroup.setSubGroupNo(subGroup.getGroupNo());

        userGroupDao.insertSelective(userGroup);

        Account account = new Account();
        account.setGroupId(userGroup.getId());
        accountDao.insertSelective(account);

        UserChannel userChannel1 = new UserChannel();
        userChannel1.setGroupId(userGroup.getId());
        userChannel1.setChannelId(channel.getId());
        userChannel1.setMchId(userGroup.getGroupNo());
        userChannel1.setFeeRate(new BigDecimal("30"));
        userChannel1.setSubFeeRate(new BigDecimal("0"));
        userChannel1.setSubGroupId(subGroup.getId());
        userChannel1.setCompanyId(superUserGroup.getId());
        userChannel1.setStandardFeeRate(channel.getFeeRate());
        userChannel1.setChannelName(channel.getChannelName());
        userChannel1.setChannelCode(channel.getChannelCode());
        userChannel1.setGroupName(userGroup.getName());
        userChannel1.setCipherCode("123456786");

        userChannelDao.insertSelective(userChannel1);

        return userGroup.getId();
    }

}
