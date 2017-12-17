package jh.test.pay;

import com.google.gson.Gson;
import hf.base.contants.CodeManager;
import hf.base.enums.*;
import hf.base.exceptions.BizFailException;
import hf.base.utils.MapUtils;
import hf.base.utils.Utils;
import jh.biz.PayBiz;
import jh.biz.service.PayBizCollection;
import jh.biz.service.PayService;
import jh.dao.local.*;
import jh.model.dto.PayRequestDto;
import jh.model.po.*;
import jh.test.BaseTestCase;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tengfei on 2017/10/29.
 */
public class PayTest extends BaseTestCase {
    @Autowired
    @Qualifier("ysPayBiz")
    private PayBiz payBiz;
    @Autowired
    @Qualifier("fxtPayBiz")
    private PayBiz fxtBiz;
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
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;
    @Autowired
    private PayBizCollection payBizCollection;
    @Autowired
    private AdminBankCardDao adminBankCardDao;
    @Autowired
    private PayMsgRecordDao payMsgRecordDao;

    private Map<String,Object> params = new HashMap<>();

    @Before
    public void initData() {
        params.put("service","02");
        params.put("version","1.0");
        params.put("merchant_no","123456");
        params.put("outlet_no","1234321");
        params.put("total","100000");
        params.put("name","234521");
        params.put("remark","备注");
        params.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        params.put("create_ip","127.0.0.1");
        params.put("out_notify_url","www.baidu.com");
        params.put("sub_openid","12132");
        params.put("buyer_id","1234521");
        params.put("authcode","120061098828009406");
        params.put("nonce_str", Utils.getRandomString(10));
        params.put("sign_type","MD5");
        String sign = Utils.encrypt(params,"123456786");
        params.put("sign",sign);
    }

    @Test
    public void testBizCollection() {
        for(ChannelCode channelCode:ChannelCode.values()) {
            PayBiz payBiz = payBizCollection.getPayBiz(channelCode.getService());
            Assert.assertNotNull(payBiz);
        }
    }

    @Test
    public void testCheckParams() {
        prepareData();
        fxtBiz.checkParam(params);
        params.put("service","06");
        params.remove("buyer_id");
        try {
            fxtBiz.checkParam(params);
        } catch (BizFailException e) {
            Assert.assertEquals(e.getCode(), CodeManager.PARAM_CHECK_FAILED);
        }

        params.put("service","01");
        params.remove("sub_openid");
        try {
            fxtBiz.checkParam(params);
        } catch (BizFailException e) {
            Assert.assertEquals(e.getCode(), CodeManager.PARAM_CHECK_FAILED);
        }
    }

    @Test
    public void testSavePayRequest() {
        Long groupId = prepareData();
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);
        params.put("merchant_no",userGroup.getGroupNo());
        String sign = Utils.encrypt(params,"123456786");
        params.put("sign",sign);
        fxtBiz.savePayRequest(params);
        String outTradeNo = String.valueOf(params.get("out_trade_no"));

        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        System.out.println(new Gson().toJson(payRequest));

        PayMsgRecord inMsg = payMsgRecordDao.selectByTradeNo(outTradeNo,OperateType.USER_HF.getValue(),TradeType.PAY.getValue());
        System.out.println(new Gson().toJson(inMsg));

        PayMsgRecord outMsg = payMsgRecordDao.selectByTradeNo(outTradeNo,OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue());
        System.out.println(new Gson().toJson(outMsg));
    }

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
//        payBiz.pay(MapUtils.beanToMap(payRequest));
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

        request = payRequestDao.selectByPrimaryKey(request.getId());
        Assert.assertEquals(request.getStatus().intValue(), PayRequestStatus.OPR_GENERATED.getValue());

        payRequestDao.updateStatusById(request.getId(),PayRequestStatus.OPR_GENERATED.getValue(),PayRequestStatus.REMOTE_CALL_FINISHED.getValue());
        request = payRequestDao.selectByPrimaryKey(request.getId());
        Assert.assertEquals(request.getStatus().intValue(),PayRequestStatus.REMOTE_CALL_FINISHED.getValue());

//        payBiz.finishPay();

        request = payRequestDao.selectByPrimaryKey(request.getId());
        Assert.assertEquals(request.getStatus().intValue(),PayRequestStatus.PAY_SUCCESS.getValue());
        AdminAccountOprLog adminAccountOprLog =adminAccountOprLogDao.selectByNo(request.getOutTradeNo());

        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(), OprStatus.PAY_SUCCESS.getValue());
        List<AccountOprLog> oprLogs = accountOprLogDao.selectByTradeNo(request.getOutTradeNo());

        for(AccountOprLog log:oprLogs) {
            Assert.assertEquals(log.getStatus().intValue(),OprStatus.PAY_SUCCESS.getValue());
        }

//        payBiz.promote();

        request = payRequestDao.selectByPrimaryKey(request.getId());
        Assert.assertEquals(request.getStatus().intValue(),PayRequestStatus.OPR_SUCCESS.getValue());
        adminAccountOprLog = adminAccountOprLogDao.selectByNo(request.getOutTradeNo());

        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(),OprStatus.FINISHED.getValue());
        AdminAccount adminAccount = adminAccountDao.selectByPrimaryKey(adminAccountOprLog.getAdminAccountId());
        Assert.assertTrue(adminAccount.getAmount().compareTo(adminAccountOprLog.getAmount())== 0);

        logs = accountOprLogDao.selectByTradeNo(request.getOutTradeNo());
        for(AccountOprLog log:logs) {
            Assert.assertEquals(log.getStatus().intValue(),OprStatus.FINISHED.getValue());
            Account account = accountDao.selectByPrimaryKey(log.getAccountId());
            Assert.assertTrue(account.getAmount().compareTo(log.getAmount())== 0);
        }
    }

    public Long prepareData() {
        Channel channel = new Channel();
        channel.setChannelName("test");
        channel.setChannelCode(ChannelCode.FXT_WX.getService());
        channel.setChannelNo("fxt");
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

        AdminBankCard adminBankCard = new AdminBankCard();
        adminBankCard.setBank("中国工商银行");
        adminBankCard.setBankNo("62222222222");
        adminBankCard.setCity("上海");
        adminBankCard.setCompanyId(superUserGroup.getId());
        adminBankCard.setDeposit("上海支行");
        adminBankCard.setGroupId(superUserGroup.getId());
        adminBankCard.setOwner("滕飞");
        adminBankCard.setProvince("上海");
        adminBankCard.setCipherCode("612342");
        adminBankCard.setLimitAmount(new BigDecimal("10000000"));
        adminBankCard.setMchId("818234213");
        adminBankCard.setOutletNo("123421345");
        adminBankCard.setStatus(CardStatus.IN_USE.getValue());
        adminBankCardDao.insertSelective(adminBankCard);

        payService.updateDailyLimit();

        superUserGroup = userGroupDao.selectByPrimaryKey(superUserGroup.getId());

        Account superAccount = new Account();
        superAccount.setGroupId(superUserGroup.getId());
        accountDao.insertSelective(superAccount);

        AdminAccount adminAccount = new AdminAccount();
        adminAccount.setGroupId(superUserGroup.getId());
        adminAccountDao.insertSelective(adminAccount);

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
        userGroup.setStatus(GroupStatus.AVAILABLE.getValue());

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

    @Test
    public void testBeanToMap() {
        PayRequestDto payRequest = new PayRequestDto();
        payRequest.setAppid(String.valueOf(RandomUtils.nextLong()));
        payRequest.setBody("345676qwewe");
        payRequest.setBuyer_id(String.valueOf(RandomUtils.nextLong()));
        payRequest.setMch_id(null);
        payRequest.setOut_trade_no(String.valueOf(RandomUtils.nextLong()));
        payRequest.setService(ChannelCode.QQ.getService());
        payRequest.setAppid(String.valueOf(RandomUtils.nextLong()));
        payRequest.setSign(String.valueOf(RandomUtils.nextLong()));
        payRequest.setTotal_fee(10000);

        Map<String,Object> map = MapUtils.beanToMap(payRequest);
        System.out.println(new Gson().toJson(map));
    }
}
