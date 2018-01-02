package jh.test.pay;

import com.google.gson.Gson;
import hf.base.enums.*;
import hf.base.enums.ChannelProvider;
import hf.base.utils.MapUtils;
import hf.base.utils.TypeConverter;
import hf.base.utils.Utils;
import jh.api.PayApi;
import jh.biz.ChannelBiz;
import jh.biz.PayBiz;
import jh.biz.UserBiz;
import jh.biz.service.PayBizCollection;
import jh.dao.local.*;
import jh.dao.remote.CallBackClient;
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

public class PayBizTest extends BaseTestCase {
    @Autowired
    private PayApi payApi;

    private String mchId;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private ChannelBiz channelBiz;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private PayBizCollection payBizCollection;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private ChannelProviderDao channelProviderDao;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PayMsgRecordDao payMsgRecordDao;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    @Qualifier("ysPayBiz")
    private PayBiz payBiz;
    @Mock
    private PayClient payClient;

    private List<Long> groupIds;
    @Autowired
    private CallBackClient callBackClient;

    @Before
    public void prepareData() throws Exception {

        List<Channel> channelList = channelDao.selectForAvaList();
        jh.model.po.ChannelProvider channelProvider = channelProviderDao.selectByCode(ChannelProvider.YS.getCode());

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
            Map<String,Object> userChannelMap = MapUtils.buildMap("groupId",userGroup.getId(),"channelId",channel.getId(),"feeRate",channel.getFeeRate(),"providerCode", hf.base.enums.ChannelProvider.YS.getCode());
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
                Map<String,Object> userChannelMap = MapUtils.buildMap("groupId",agentUserGroup.getId(),"channelId",channel.getId(),"feeRate",channel.getFeeRate().add(new BigDecimal(i)),"providerCode", hf.base.enums.ChannelProvider.YS.getCode());
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
                    Map<String,Object> userChannelMap = MapUtils.buildMap("groupId",customerUserGroup.getId(),"channelId",channel.getId(),"feeRate",channel.getFeeRate().add(new BigDecimal(i)).add(new BigDecimal(index)),"providerCode", hf.base.enums.ChannelProvider.YS.getCode());
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

            MockitoAnnotations.initMocks(this);
            ReflectionTestUtils.setField(payBiz,"payClient",payClient);
        }
    }

    @Test
    public void testPrepareData() {

    }

    @Test
    public void testGetPayBiz() {
        PayBiz payBiz = payBizCollection.getPayBiz("01");
        Assert.assertEquals(payBiz.getProvider(), ChannelProvider.YS);
        payBiz = payBizCollection.getPayBiz("05");
        Assert.assertEquals(payBiz.getProvider(), ChannelProvider.YS);
        payBiz = payBizCollection.getPayBiz("06");
        Assert.assertEquals(payBiz.getProvider(), ChannelProvider.YS);
    }

    @Test
    public void testCheckParam() {
        Map<String,Object> params = new HashMap<>();
        params.put("service","01");
        params.put("merchant_no",String.valueOf(RandomUtils.nextLong()));
        params.put("outlet_no","132454");
        params.put("total","100000");
        params.put("name","test");
        params.put("remark","adsfdsa");
        params.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        params.put("create_ip","127.0.0.1");
        params.put("out_notify_url","www.baidu.com");
        params.put("version","1.0");
        params.put("authcode",String.valueOf(RandomUtils.nextLong()));
        params.put("nonce_str",Utils.getRandomString(10));
        params.put("sub_openid",String.valueOf(RandomUtils.nextLong()));
        String cipherCode = "123456";
        String sign = Utils.encrypt(params,cipherCode);
        params.put("sign",sign);

        PayBiz payBiz = payBizCollection.getPayBiz("01");
        payBiz.checkParam(params);
    }

    @Test
    public void testSavePayRequest() {
        Long groupId1 = groupIds.get(0);
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId1);
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.YS.getCode());
        List<UserChannel> userChannels = userChannelDao.selectByGroupId(userGroup.getId());

        PayBiz payBiz = payBizCollection.getPayBiz("01");
        Map<String,Object> params = new HashMap<>();
        params.put("service","01");
        params.put("merchant_no",userGroup.getGroupNo());
        params.put("outlet_no","132454");
        params.put("total","100000");
        params.put("name","test");
        params.put("remark","adsfdsa");
        params.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        params.put("create_ip","127.0.0.1");
        params.put("out_notify_url","www.baidu.com");
        params.put("version","1.0");
        params.put("authcode",String.valueOf(RandomUtils.nextLong()));
        params.put("nonce_str",Utils.getRandomString(10));
        params.put("sub_openid",String.valueOf(RandomUtils.nextLong()));
        String cipherCode = userGroup.getCipherCode();
        String sign = Utils.encrypt(params,cipherCode);
        params.put("sign",sign);
        payBiz.savePayRequest(params);

        String outTradeNo = String.format("%s_%s",String.valueOf(params.get("merchant_no")),String.valueOf(params.get("out_trade_no")));

        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        Assert.assertNotNull(payRequest);
        Assert.assertEquals(payRequest.getStatus().intValue(), PayRequestStatus.NEW.getValue());
        System.out.println(new Gson().toJson(payRequest));

        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(outTradeNo, OperateType.USER_HF.getValue(),TradeType.PAY.getValue());
        Assert.assertNotNull(payMsgRecord);

        System.out.println("payMsgRecord:"+new Gson().toJson(payMsgRecord));

        PayMsgRecord payMsgRecord1 = payMsgRecordDao.selectByTradeNo(outTradeNo,OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue());
        Assert.assertNotNull(payMsgRecord1);
        System.out.println("payMsgRecord,hf_client:"+new Gson().toJson(payMsgRecord1));

        Map<String,Object> resultMap = MapUtils.buildMap(
                "total_fee","100000",
                "body","success",
                "out_trade_no",outTradeNo,
                "mch_id",userGroupExt.getMerchantNo(),
                "status","0",
                "signType","MD5");
        Mockito.when(payClient.unifiedorder(Mockito.anyMap())).thenReturn(resultMap);
        payBiz.pay(payRequest);

        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(payRequest.getStatus().intValue(),PayRequestStatus.PROCESSING.getValue());

        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        Assert.assertNotNull(adminAccountOprLog);
        System.out.println("adminAccountOprLog:"+new Gson().toJson(adminAccountOprLog));

        List<AccountOprLog> accountOprLogs = accountOprLogDao.selectByTradeNo(outTradeNo);
        Assert.assertTrue(accountOprLogs.size()>0);

        System.out.println("opr logs:"+new Gson().toJson(accountOprLogs));

        PayMsgRecord msg2User = payMsgRecordDao.selectByTradeNo(outTradeNo,OperateType.HF_USER.getValue(),TradeType.PAY.getValue());
        System.out.println(new Gson().toJson(msg2User));

        Long groupId2 = groupIds.get(1);
        userGroup = userGroupDao.selectByPrimaryKey(groupId2);
        userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.YS.getCode());
        userChannels = userChannelDao.selectByGroupId(userGroup.getId());

        payBiz = payBizCollection.getPayBiz("01");
        params = new HashMap<>();
        params.put("service","01");
        params.put("merchant_no",userGroup.getGroupNo());
        params.put("outlet_no","132454");
        params.put("total","100000");
        params.put("name","test");
        params.put("remark","adsfdsa");
        params.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        params.put("create_ip","127.0.0.1");
        params.put("out_notify_url","www.baidu.com");
        params.put("version","1.0");
        params.put("authcode",String.valueOf(RandomUtils.nextLong()));
        params.put("nonce_str",Utils.getRandomString(10));
        params.put("sub_openid",String.valueOf(RandomUtils.nextLong()));
        cipherCode = userGroup.getCipherCode();
        sign = Utils.encrypt(params,cipherCode);
        params.put("sign",sign);
        payBiz.savePayRequest(params);

        outTradeNo = String.format("%s_%s",String.valueOf(params.get("merchant_no")),String.valueOf(params.get("out_trade_no")));

        payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        Assert.assertNotNull(payRequest);
        Assert.assertEquals(payRequest.getStatus().intValue(), PayRequestStatus.NEW.getValue());
        System.out.println(new Gson().toJson(payRequest));

        payMsgRecord = payMsgRecordDao.selectByTradeNo(outTradeNo, OperateType.USER_HF.getValue(),TradeType.PAY.getValue());
        Assert.assertNotNull(payMsgRecord);

        System.out.println("payMsgRecord:"+new Gson().toJson(payMsgRecord));

        payMsgRecord1 = payMsgRecordDao.selectByTradeNo(outTradeNo,OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue());
        Assert.assertNotNull(payMsgRecord1);
        System.out.println("payMsgRecord,hf_client:"+new Gson().toJson(payMsgRecord1));

        resultMap = MapUtils.buildMap(
                "total_fee","100000",
                "body","success",
                "out_trade_no",outTradeNo,
                "mch_id",userGroupExt.getMerchantNo(),
                "status","1",
                "signType","MD5");
        Mockito.when(payClient.unifiedorder(Mockito.anyMap())).thenReturn(resultMap);
        payBiz.pay(payRequest);

        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(payRequest.getStatus().intValue(),PayRequestStatus.PAY_FAILED.getValue());

        adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        Assert.assertNotNull(adminAccountOprLog);
        System.out.println("adminAccountOprLog:"+new Gson().toJson(adminAccountOprLog));

        accountOprLogs = accountOprLogDao.selectByTradeNo(outTradeNo);
        Assert.assertTrue(accountOprLogs.size()>0);
        for(AccountOprLog accountOprLog:accountOprLogs) {
            Assert.assertEquals(accountOprLog.getStatus().intValue(),OprStatus.PAY_FAILED.getValue());
        }

        System.out.println("opr logs:"+new Gson().toJson(accountOprLogs));
    }

    @Test
    public void testPay() {
        Map<String,Object> params = new HashMap<>();
        String result = payApi.unifiedorder(params);
        System.out.println("empty params:"+result);

        params.put("service","01");
        result = payApi.unifiedorder(params);
        System.out.println("exist service:"+result);

        params.put("version","1.0");
        //"service","version","merchant_no","outlet_no","total","name","remark","out_trade_no",
//        "create_ip","out_notify_url","authcode","nonce_str","sign"
        params.put("merchant_no",mchId);
        params.put("outlet_no","");
        params.put("total",new BigDecimal("100000"));
        params.put("name","转账1000.00");
        params.put("remark","转账1000.00");
        params.put("create_ip","127.0.0.1");
        params.put("out_notify_url","http://127.0.0.1:8087/test");
        params.put("authcode",String.valueOf(RandomUtils.nextLong()));
        params.put("nonce_str", Utils.getRandomString(10));
    }


    @Test
    public void testPaySuccess() {
        Long groupId1 = groupIds.get(0);
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId1);
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.YS.getCode());
        List<UserChannel> userChannels = userChannelDao.selectByGroupId(userGroup.getId());

        PayBiz payBiz = payBizCollection.getPayBiz("01");
        Map<String,Object> params = new HashMap<>();
        params.put("service","01");
        params.put("merchant_no",userGroup.getGroupNo());
        params.put("outlet_no","132454");
        params.put("total","100000");
        params.put("name","test");
        params.put("remark","adsfdsa");
        params.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        params.put("create_ip","127.0.0.1");
        params.put("out_notify_url","www.baidu.com");
        params.put("version","1.0");
        params.put("authcode",String.valueOf(RandomUtils.nextLong()));
        params.put("nonce_str",Utils.getRandomString(10));
        params.put("sub_openid",String.valueOf(RandomUtils.nextLong()));
        String cipherCode = userGroup.getCipherCode();
        String sign = Utils.encrypt(params,cipherCode);
        params.put("sign",sign);
        payBiz.savePayRequest(params);

        String outTradeNo = String.format("%s_%s",String.valueOf(params.get("merchant_no")),String.valueOf(params.get("out_trade_no"))) ;

        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        Assert.assertNotNull(payRequest);
        Assert.assertEquals(payRequest.getStatus().intValue(), PayRequestStatus.NEW.getValue());
        System.out.println(new Gson().toJson(payRequest));

        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(outTradeNo, OperateType.USER_HF.getValue(),TradeType.PAY.getValue());
        Assert.assertNotNull(payMsgRecord);

        System.out.println("payMsgRecord:"+new Gson().toJson(payMsgRecord));

        PayMsgRecord payMsgRecord1 = payMsgRecordDao.selectByTradeNo(outTradeNo,OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue());
        Assert.assertNotNull(payMsgRecord1);
        System.out.println("payMsgRecord,hf_client:"+new Gson().toJson(payMsgRecord1));

        Map<String,Object> resultMap = MapUtils.buildMap(
                "total_fee","100000",
                "body","success",
                "out_trade_no",outTradeNo,
                "mch_id",userGroupExt.getMerchantNo(),
                "status","0",
                "signType","MD5");
        Mockito.when(payClient.unifiedorder(Mockito.anyMap())).thenReturn(resultMap);
        payBiz.pay(payRequest);

        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        Assert.assertNotNull(adminAccountOprLog);
        System.out.println("adminAccountOprLog:"+new Gson().toJson(adminAccountOprLog));

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(adminAccountOprLog.getGroupId());
        BigDecimal adminAmount = adminAccount.getAmount();

        List<AccountOprLog> accountOprLogs = accountOprLogDao.selectByTradeNo(outTradeNo);
        Assert.assertTrue(accountOprLogs.size()>0);
        Map<Long,BigDecimal> groupAmountMap = new HashMap<>();
        for(AccountOprLog accountOprLog:accountOprLogs) {
            Account account = accountDao.selectByGroupId(accountOprLog.getGroupId());
            groupAmountMap.put(account.getGroupId(),account.getAmount());
        }

        Map<String,Object> payResult = new HashMap<>();
        payResult.put("mch_id",userGroupExt.getMerchantNo());
        payResult.put("out_trade_no",outTradeNo);
        payResult.put("trade_type","pay.weixin.jspay");
        payResult.put("pay_result","0");
        payBiz.finishPay(payResult);

        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(payRequest.getStatus().intValue(),PayRequestStatus.OPR_SUCCESS.getValue());
        Assert.assertEquals(payRequest.getPayResult(),"0");

        adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(),OprStatus.PAY_SUCCESS.getValue());
        adminAccount = adminAccountDao.selectByGroupId(adminAccountOprLog.getGroupId());
//        Assert.assertTrue((adminAccount.getAmount().subtract(adminAmount)).compareTo(new BigDecimal(payRequest.getTotalFee()))==0);
        System.out.println("opr logs:"+new Gson().toJson(accountOprLogs));

        accountOprLogs = accountOprLogDao.selectByTradeNo(outTradeNo);
        for(AccountOprLog oprLog:accountOprLogs) {
            Assert.assertEquals(oprLog.getStatus().intValue(),OprStatus.PAY_SUCCESS.getValue());
//            Account account = accountDao.selectByGroupId(oprLog.getGroupId());
//            System.out.println(account.getAmount().subtract(groupAmountMap.get(oprLog.getGroupId())));
        }

        payRequest  = payRequestDao.selectByPrimaryKey(payRequest.getId());
        payBiz.notice(payRequest);

        payRequest  = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(payRequest.getStatus().intValue(),PayRequestStatus.USER_NOTIFIED.getValue());

        payBiz.promote(payRequest);
    }

    @Test
    public void testPayFailed() {
        Long groupId1 = groupIds.get(0);
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId1);
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.YS.getCode());
        List<UserChannel> userChannels = userChannelDao.selectByGroupId(userGroup.getId());

        PayBiz payBiz = payBizCollection.getPayBiz("01");
        Map<String,Object> params = new HashMap<>();
        params.put("service","01");
        params.put("merchant_no",userGroup.getGroupNo());
        params.put("outlet_no","132454");
        params.put("total","100000");
        params.put("name","test");
        params.put("remark","adsfdsa");
        params.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        params.put("create_ip","127.0.0.1");
        params.put("out_notify_url","www.baidu.com");
        params.put("version","1.0");
        params.put("authcode",String.valueOf(RandomUtils.nextLong()));
        params.put("nonce_str",Utils.getRandomString(10));
        params.put("sub_openid",String.valueOf(RandomUtils.nextLong()));
        String cipherCode = userGroup.getCipherCode();
        String sign = Utils.encrypt(params,cipherCode);
        params.put("sign",sign);
        payBiz.savePayRequest(params);

        String outTradeNo = String.format("%s_%s",String.valueOf(params.get("merchant_no")),String.valueOf(params.get("out_trade_no"))) ;

        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        Assert.assertNotNull(payRequest);
        Assert.assertEquals(payRequest.getStatus().intValue(), PayRequestStatus.NEW.getValue());
        System.out.println(new Gson().toJson(payRequest));

        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(outTradeNo, OperateType.USER_HF.getValue(),TradeType.PAY.getValue());
        Assert.assertNotNull(payMsgRecord);

        System.out.println("payMsgRecord:"+new Gson().toJson(payMsgRecord));

        PayMsgRecord payMsgRecord1 = payMsgRecordDao.selectByTradeNo(outTradeNo,OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue());
        Assert.assertNotNull(payMsgRecord1);
        System.out.println("payMsgRecord,hf_client:"+new Gson().toJson(payMsgRecord1));

        Map<String,Object> resultMap = MapUtils.buildMap(
                "total_fee","100000",
                "body","success",
                "out_trade_no",outTradeNo,
                "mch_id",userGroupExt.getMerchantNo(),
                "status","0",
                "signType","MD5");
        Mockito.when(payClient.unifiedorder(Mockito.anyMap())).thenReturn(resultMap);
        payBiz.pay(payRequest);

        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        Assert.assertNotNull(adminAccountOprLog);
        System.out.println("adminAccountOprLog:"+new Gson().toJson(adminAccountOprLog));

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(adminAccountOprLog.getGroupId());
        BigDecimal adminAmount = adminAccount.getAmount();

        List<AccountOprLog> accountOprLogs = accountOprLogDao.selectByTradeNo(outTradeNo);
        Assert.assertTrue(accountOprLogs.size()>0);
        Map<Long,BigDecimal> groupAmountMap = new HashMap<>();
        for(AccountOprLog accountOprLog:accountOprLogs) {
            Account account = accountDao.selectByGroupId(accountOprLog.getGroupId());
            groupAmountMap.put(account.getGroupId(),account.getAmount());
        }

        Map<String,Object> payResult = new HashMap<>();
        payResult.put("mch_id",userGroupExt.getMerchantNo());
        payResult.put("out_trade_no",outTradeNo);
        payResult.put("trade_type","pay.weixin.jspay");
        payResult.put("pay_result","1");
        payBiz.finishPay(payResult);

        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(payRequest.getStatus().intValue(),PayRequestStatus.OPR_SUCCESS.getValue());

        adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(),OprStatus.PAY_FAILED.getValue());

        accountOprLogs = accountOprLogDao.selectByTradeNo(outTradeNo);
        for(AccountOprLog oprLog:accountOprLogs) {
            Assert.assertEquals(oprLog.getStatus().intValue(),OprStatus.PAY_FAILED.getValue());
        }

        payBiz.notice(payRequest);
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(PayRequestStatus.OPR_FINISHED.getValue(),payRequest.getStatus().intValue());
    }

    @Test
    public void testCallRemoteUrl() {
        String url = "http://tingt5.cn/WechatHuiFuFuPay/Notify.html";
        Map<String,Object> resutMap = new HashMap<>();
        resutMap.put("errcode","0");
        //msg
        resutMap.put("message","支付成功");

        resutMap.put("no","1234312");
        //out_trade_no
        resutMap.put("out_trade_no","123456");
        //mch_id
        resutMap.put("merchant_no","1234546");
        //total
        resutMap.put("total","12345");
        //fee
        resutMap.put("fee","22");
        //trade_type 1:收单 2:撤销 3:退款
        resutMap.put("trade_type","1");
        //sign_type
        resutMap.put("sign_type","MD5");
        String sign = Utils.encrypt(resutMap,"1234312");
        resutMap.put("sign",sign);

        callBackClient.post(url,resutMap);
    }
}
