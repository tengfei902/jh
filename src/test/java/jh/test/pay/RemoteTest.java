package jh.test.pay;

import com.google.gson.Gson;
import hf.base.utils.MapUtils;
import hf.base.utils.Utils;
import jh.dao.local.UserGroupDao;
import jh.dao.remote.YsClient;
import jh.model.po.UserGroup;
import jh.test.BaseCommitTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class RemoteTest extends BaseCommitTestCase {
    @Autowired
    private YsClient ysClient;
    @Autowired
    private UserGroupDao userGroupDao;

    @Test
    public void testPay() {
        UserGroup userGroup = userGroupDao.selectByGroupNo("13588");

        Map<String,Object> payParams = new HashMap<>();
        payParams.put("version","1.0");
        payParams.put("service","02");
        payParams.put("merchant_no",userGroup.getGroupNo());
        payParams.put("total","100");//10000.00
        payParams.put("name","转账1");
        payParams.put("remark","转账1");
        payParams.put("out_trade_no","42136512514");
        payParams.put("create_ip","127.0.0.1");
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("sign_type","MD5");
        String cipherCode = userGroup.getCipherCode();
        String sign = Utils.encrypt(payParams,cipherCode);
        payParams.put("sign",sign);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> entity = restTemplate.postForEntity("http://127.0.0.1:8080/jh/pay/unifiedorder",payParams,String.class, new Object[0]);
        System.out.println(entity.getBody());
    }

    @Test
    public void testSign() {
        Map<String,Object> map = new HashMap<>();
        map.put("out_trade_no","5139_20180102164506104");
        map.put("service","pay.weixin.jspay");
        map.put("total_fee","1000000");
        map.put("body","转账10000");
        map.put("mch_id","102555074371");
        map.put("sub_openid","ojoKev39p15cuREQKbNnRSmjN9EY");

        String sign = Utils.encrypt2(map,"d4653889e27b45fb51bae4eb427c1a92");
        Assert.assertEquals("15452C3B22FFBFF177254024731B8850",sign);
    }

    @Test
    public void testQueryOrder() {
        String mchId = "102555074371";
        String outTradeNo = "5139_20180102175624604";
        Map<String,Object> result = ysClient.orderinfo(MapUtils.buildMap("mch_id",mchId,"out_trade_no",outTradeNo));
        System.out.println(new Gson().toJson(result));
    }
}
