package jh.test.pay;

import com.google.gson.Gson;
import hf.base.enums.ChannelProvider;
import hf.base.enums.GroupType;
import hf.base.enums.OprStatus;
import hf.base.enums.PayRequestStatus;
import hf.base.utils.MapUtils;
import hf.base.utils.TypeConverter;
import hf.base.utils.Utils;
import jh.api.PayApi;
import jh.biz.ChannelBiz;
import jh.biz.UserBiz;
import jh.biz.service.TradeBizFactory;
import jh.biz.trade.TradeBiz;
import jh.dao.local.*;
import jh.dao.remote.PayClient;
import jh.model.po.*;
import jh.test.BaseTestCase;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeBizTest extends BaseTestCase {
    @Autowired
    private PayApi payApi;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private ChannelProviderDao channelProviderDao;
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private ChannelBiz channelBiz;
    @Mock
    private TradeBizFactory mockTradeBizFactory;
    @Autowired
    @Qualifier("fxtTradeBiz")
    private TradeBiz fxtTradeBiz;
    @Mock
    private PayClient fxtPayClient;
    @Autowired
    private TradeBizFactory tradeBizFactory;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;

    private List<Long> groupIds;

    @Before
    public void prepareData() {
        MockitoAnnotations.initMocks(this);

        ChannelProvider provider = ChannelProvider.FXT;

        List<Channel> channelList = channelDao.selectForAvaList();
        jh.model.po.ChannelProvider channelProvider = channelProviderDao.selectByCode(provider.getCode());

        Map<String,Object> map = MapUtils.buildMap(
                "name","测试company",
                "ownerName","张三",
                "idCard","37132619881122005X",
                "tel","1234565423",
                "address","上海市浦东新区",
                "type", GroupType.COMPANY.getValue(),
                "subGroupId",0,
                "cipherCode","123452s",
                "callbackUrl","http://127.0.0.1:8080/jh/jhTest");

        UserGroup userGroup = TypeConverter.convert(map,UserGroup.class);
        userBiz.saveUserGroup(userGroup);

        UserGroupExt userGroupExt = new UserGroupExt();
        userGroupExt.setGroupId(userGroup.getId());
        userGroupExt.setProviderName(channelProvider.getProviderName());
        userGroupExt.setProviderCode(channelProvider.getProviderCode());
        userGroupExt.setCipherCode(Utils.getRandomString(8));
        userGroupExt.setMerchantNo(Utils.getRandomString(16));
        userGroupExt.setOutletNo(Utils.getRandomString(8));
        userGroupExtDao.insertSelective(userGroupExt);

//        Account account = new Account();
//        account.setGroupId(userGroup.getId());
//        account.setAmount(new BigDecimal("1000000"));
//        accountDao.insertSelective(account);

        AdminAccount adminAccount = new AdminAccount();
        adminAccount.setGroupId(userGroup.getId());
        adminAccount.setAmount(new BigDecimal("1000000"));
        adminAccountDao.insertSelective(adminAccount);

        for(Channel channel:channelList) {
            Map<String,Object> userChannelMap = MapUtils.buildMap("groupId",userGroup.getId(),"channelId",channel.getId(),"feeRate",channel.getFeeRate().add(new BigDecimal("0.5")),"providerCode", provider.getCode());
            UserChannel userChannel = TypeConverter.convert(userChannelMap,UserChannel.class);
            channelBiz.saveUserChannel(userChannel);
        }

        userBiz.userPass(userGroup.getId());
//        channelBiz.saveUserChannel();

        for(int i=0;i<10;i++) {
            Map<String,Object> agentMap = MapUtils.buildMap(
                    "name","测试agent",
                    "ownerName","李四",
                    "idCard","37132619881122005X",
                    "tel","1234565423",
                    "address","上海市浦东新区",
                    "type", GroupType.AGENT.getValue(),
                    "subGroupId",userGroup.getId(),
                    "cipherCode","123452s",
                    "callbackUrl","www.baidu2.com");

            UserGroup agentUserGroup = TypeConverter.convert(agentMap,UserGroup.class);
            userBiz.saveUserGroup(agentUserGroup);

            UserGroupExt agentUserGroupExt = new UserGroupExt();
            agentUserGroupExt.setGroupId(agentUserGroup.getId());
            agentUserGroupExt.setProviderName(channelProvider.getProviderName());
            agentUserGroupExt.setProviderCode(channelProvider.getProviderCode());
            agentUserGroupExt.setCipherCode(Utils.getRandomString(8));
            agentUserGroupExt.setMerchantNo(Utils.getRandomString(16));
            agentUserGroupExt.setOutletNo(Utils.getRandomString(8));
            userGroupExtDao.insertSelective(agentUserGroupExt);

            for(Channel channel:channelList) {
                Map<String,Object> userChannelMap = MapUtils.buildMap("groupId",agentUserGroup.getId(),"channelId",channel.getId(),"feeRate",channel.getFeeRate().add(new BigDecimal(i+1)),"providerCode", provider.getCode());
                UserChannel userChannel = TypeConverter.convert(userChannelMap,UserChannel.class);
                channelBiz.saveUserChannel(userChannel);
            }

//            Account agentAccount = new Account();
//            agentAccount.setGroupId(agentUserGroup.getId());
//            agentAccount.setAmount(new BigDecimal("100000"));
//            accountDao.insertSelective(agentAccount);

            userBiz.userPass(agentUserGroup.getId());

            List<Long> newGroupIds = new ArrayList<>();

            for(int index=0;index<10;index++) {
                Map<String,Object> customerMap = MapUtils.buildMap(
                        "name","测试customer",
                        "ownerName","撒的上升",
                        "idCard","37132619881122005X",
                        "tel","1234565423",
                        "address","上海市浦东新区",
                        "type", GroupType.CUSTOMER.getValue(),
                        "subGroupId",agentUserGroup.getId(),
                        "cipherCode","123452s",
                        "callbackUrl","http://127.0.0.1:8080/jh/jhTest");

                UserGroup customerUserGroup = TypeConverter.convert(customerMap,UserGroup.class);
                userBiz.saveUserGroup(customerUserGroup);

                UserGroupExt customerUserGroupExt = new UserGroupExt();
                customerUserGroupExt.setGroupId(customerUserGroup.getId());
                customerUserGroupExt.setProviderName(channelProvider.getProviderName());
                customerUserGroupExt.setProviderCode(channelProvider.getProviderCode());
                customerUserGroupExt.setCipherCode(Utils.getRandomString(8));
                customerUserGroupExt.setMerchantNo(Utils.getRandomString(16));
                customerUserGroupExt.setOutletNo(Utils.getRandomString(8));
                userGroupExtDao.insertSelective(customerUserGroupExt);

                newGroupIds.add(customerUserGroup.getId());

                for(Channel channel:channelList) {
                    Map<String,Object> userChannelMap = MapUtils.buildMap("groupId",customerUserGroup.getId(),"channelId",channel.getId(),"feeRate",channel.getFeeRate().add(new BigDecimal(i+1+0.1)).add(new BigDecimal(index)),"providerCode", provider.getCode());
                    UserChannel userChannel = TypeConverter.convert(userChannelMap,UserChannel.class);
                    channelBiz.saveUserChannel(userChannel);
                }

//                Account customerAccount = new Account();
//                customerAccount.setGroupId(customerAccount.getId());
//                customerAccount.setAmount(new BigDecimal("10000"));
//                accountDao.insertSelective(customerAccount);

                userBiz.userPass(customerUserGroup.getId());
            }

            groupIds = newGroupIds;

            ReflectionTestUtils.setField(payApi,"tradeBizFactory",mockTradeBizFactory);
            Mockito.when(mockTradeBizFactory.getTradeBiz(Mockito.anyString(),Mockito.anyString())).thenReturn(fxtTradeBiz);
            ReflectionTestUtils.setField(fxtTradeBiz,"payClient",fxtPayClient);
        }
    }

    @Test
    public void testGetTradeBiz() {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupIds.get(0));
        TradeBiz tradeBiz = tradeBizFactory.getTradeBiz(userGroup.getGroupNo(),"01");
        System.out.println(tradeBiz.getClass());
    }

    @Test
    public void testDoPay() {
        Long groupId = groupIds.get(0);
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);

        Map<String,Object> successMap = new HashMap<>();
        successMap.put("errcode","0");
        successMap.put("message","成功");
        successMap.put("no",String.valueOf(RandomUtils.nextLong()));
        successMap.put("out_trade_no",String.format("%s_%s",userGroup.getGroupNo(),"123456231"));
        successMap.put("sign_type","MD5");
        successMap.put("sign",Utils.getRandomString(8));
        Mockito.when(fxtPayClient.unifiedorder(Mockito.anyMap())).thenReturn(successMap);

        Map<String,Object> payParams = new HashMap<>();
        payParams.put("version","1.0");
        payParams.put("service","01");
        payParams.put("merchant_no",userGroup.getGroupNo());
//        payParams.put("outlet_no","1234321");
        payParams.put("total","1000000");//10000.00
        payParams.put("name","转账10000");
        payParams.put("remark","转账10000");
        payParams.put("out_trade_no","123456231");
        payParams.put("create_ip","127.0.0.1");
        payParams.put("sub_openid","123454125");
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("sign_type","MD5");
        payParams.put("authcode",String.valueOf(RandomUtils.nextLong()));

        String cipherCode = userGroup.getCipherCode();
        String sign = Utils.encrypt(payParams,cipherCode);
        payParams.put("sign",sign);
        String result = payApi.unifiedorder(payParams);
        System.out.println(result);

        result = payApi.unifiedorder(payParams);
        System.out.println(result);

        String tradeNo = String.format("%s_%s",payParams.get("merchant_no"),payParams.get("out_trade_no"));
        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);

        System.out.println("payRequest amount:"+payRequest.getTotalFee()+",actualAmount:"+payRequest.getActualAmount()+",fee:"+payRequest.getFee());

        Assert.assertEquals(payRequest.getStatus().intValue(), PayRequestStatus.PROCESSING.getValue());

        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(tradeNo);
        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(), OprStatus.NEW.getValue());
        System.out.println(new Gson().toJson(adminAccountOprLog));

        List<AccountOprLog> oprLogs = accountOprLogDao.selectByTradeNo(tradeNo);
        for(AccountOprLog oprLog:oprLogs) {
            Assert.assertEquals(oprLog.getStatus().intValue(),OprStatus.NEW.getValue());
            System.out.println(new Gson().toJson(oprLog));
        }

        //todo callback
        Map<String,Object> callBackSuccessMap = new HashMap<>();
        callBackSuccessMap.put("errcode","0");
        callBackSuccessMap.put("message","成功");
        callBackSuccessMap.put("no",String.valueOf(RandomUtils.nextLong()));
        callBackSuccessMap.put("out_trade_no",tradeNo);
        callBackSuccessMap.put("total","1000000");
        callBackSuccessMap.put("transaction_id",String.valueOf(RandomUtils.nextLong()));
        callBackSuccessMap.put("sign_type","MD5");
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.FXT.getCode());
        sign = Utils.encrypt(callBackSuccessMap,userGroupExt.getCipherCode());
        callBackSuccessMap.put("sign",sign);
        payApi.fxtCallBack(callBackSuccessMap);
//        AdminAccount adminAccount = adminAccountDao.selectByGroupId(adminAccountOprLog.getGroupId());
//        Assert.assertTrue((adminAccount.getAmount().subtract(adminAmount)).compareTo(new BigDecimal(payRequest.getTotalFee()))==0);
//        System.out.println("opr logs:"+new Gson().toJson(accountOprLogs));
        adminAccountOprLog = adminAccountOprLogDao.selectByNo(tradeNo);
        Assert.assertTrue(adminAccountOprLog.getStatus() == OprStatus.PAY_SUCCESS.getValue());
        oprLogs = accountOprLogDao.selectByTradeNo(tradeNo);
        for(AccountOprLog oprLog:oprLogs) {
            Assert.assertEquals(oprLog.getStatus().intValue(),OprStatus.PAY_SUCCESS.getValue());
            System.out.println(new Gson().toJson(oprLog));
        }

        //todo test callback failed
        groupId = groupIds.get(3);
        userGroup = userGroupDao.selectByPrimaryKey(groupId);

        payParams = new HashMap<>();
        payParams.put("version","1.0");
        payParams.put("service","01");
        payParams.put("merchant_no",userGroup.getGroupNo());
//        payParams.put("outlet_no","1234321");
        payParams.put("total","1000000");//10000.00
        payParams.put("name","转账10000");
        payParams.put("remark","转账10000");
        payParams.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        payParams.put("create_ip","127.0.0.1");
        payParams.put("sub_openid","123454125");
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("sign_type","MD5");
        payParams.put("authcode",String.valueOf(RandomUtils.nextLong()));

        cipherCode = userGroup.getCipherCode();
        sign = Utils.encrypt(payParams,cipherCode);

        payParams.put("sign",sign);
        successMap = new HashMap<>();
        successMap.put("errcode","0");
        successMap.put("message","成功");
        successMap.put("no",String.valueOf(RandomUtils.nextLong()));
        successMap.put("out_trade_no",String.format("%s_%s",userGroup.getGroupNo(),payParams.get("out_trade_no")));
        successMap.put("sign_type","MD5");
        successMap.put("sign",Utils.getRandomString(8));
        Mockito.when(fxtPayClient.unifiedorder(Mockito.anyMap())).thenReturn(successMap);

        result = payApi.unifiedorder(payParams);
        System.out.println(result);

        result = payApi.unifiedorder(payParams);
        System.out.println(result);

        tradeNo = String.format("%s_%s",payParams.get("merchant_no"),payParams.get("out_trade_no"));
        payRequest = payRequestDao.selectByTradeNo(tradeNo);

        Assert.assertEquals(payRequest.getStatus().intValue(), PayRequestStatus.PROCESSING.getValue());

        adminAccountOprLog = adminAccountOprLogDao.selectByNo(tradeNo);
        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(), OprStatus.NEW.getValue());
        System.out.println(new Gson().toJson(adminAccountOprLog));

        oprLogs = accountOprLogDao.selectByTradeNo(tradeNo);
        for(AccountOprLog oprLog:oprLogs) {
            Assert.assertEquals(oprLog.getStatus().intValue(),OprStatus.NEW.getValue());
            System.out.println(new Gson().toJson(oprLog));
        }

        //todo callback
        callBackSuccessMap = new HashMap<>();
        callBackSuccessMap.put("errcode","1");
        callBackSuccessMap.put("message","失败");
        callBackSuccessMap.put("no",String.valueOf(RandomUtils.nextLong()));
        callBackSuccessMap.put("out_trade_no",tradeNo);
        callBackSuccessMap.put("total","1000000");
        callBackSuccessMap.put("transaction_id",String.valueOf(RandomUtils.nextLong()));
        callBackSuccessMap.put("sign_type","MD5");
        userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.FXT.getCode());
        sign = Utils.encrypt(callBackSuccessMap,userGroupExt.getCipherCode());
        callBackSuccessMap.put("sign",sign);
        payApi.fxtCallBack(callBackSuccessMap);
//        AdminAccount adminAccount = adminAccountDao.selectByGroupId(adminAccountOprLog.getGroupId());
//        Assert.assertTrue((adminAccount.getAmount().subtract(adminAmount)).compareTo(new BigDecimal(payRequest.getTotalFee()))==0);
//        System.out.println("opr logs:"+new Gson().toJson(accountOprLogs));
        adminAccountOprLog = adminAccountOprLogDao.selectByNo(tradeNo);
        Assert.assertTrue(adminAccountOprLog.getStatus() == OprStatus.PAY_FAILED.getValue());
        oprLogs = accountOprLogDao.selectByTradeNo(tradeNo);
        for(AccountOprLog oprLog:oprLogs) {
            Assert.assertEquals(oprLog.getStatus().intValue(),OprStatus.PAY_FAILED.getValue());
            System.out.println(new Gson().toJson(oprLog));
        }

        //todo test failed
        groupId = groupIds.get(1);
        userGroup = userGroupDao.selectByPrimaryKey(groupId);
        Map<String,Object> failedMap = new HashMap<>();
        failedMap.put("errcode","1");
        failedMap.put("message","失败");
        Mockito.when(fxtPayClient.unifiedorder(Mockito.anyMap())).thenReturn(failedMap);

        payParams = new HashMap<>();
        payParams.put("version","1.0");
        payParams.put("service","01");
        payParams.put("merchant_no",userGroup.getGroupNo());
//        payParams.put("outlet_no","1234321");
        payParams.put("total","1000000");//10000.00
        payParams.put("name","转账10000");
        payParams.put("remark","转账10000");
        payParams.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        payParams.put("create_ip","127.0.0.1");
        payParams.put("sub_openid","123454125");
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("sign_type","MD5");
        payParams.put("authcode",String.valueOf(RandomUtils.nextLong()));

        cipherCode = userGroup.getCipherCode();
        sign = Utils.encrypt(payParams,cipherCode);
        payParams.put("sign",sign);

        result = payApi.unifiedorder(payParams);
        System.out.println(result);
        tradeNo = String.format("%s_%s",payParams.get("merchant_no"),payParams.get("out_trade_no"));
        adminAccountOprLog = adminAccountOprLogDao.selectByNo(tradeNo);
        System.out.println(new Gson().toJson(adminAccountOprLog));
        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(),OprStatus.PAY_FAILED.getValue());

        oprLogs = accountOprLogDao.selectByTradeNo(tradeNo);
        Assert.assertTrue(oprLogs.size()>0);
        for(AccountOprLog oprLog:oprLogs) {
            System.out.println(new Gson().toJson(oprLog));
            Assert.assertTrue(oprLog.getStatus() == OprStatus.PAY_FAILED.getValue());
        }
    }
}
