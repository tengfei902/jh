package jh.test.pay;

import hf.base.utils.Utils;
import jh.test.BaseCommitTestCase;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class RemoteTest extends BaseCommitTestCase {

    @Test
    public void testPay() {
        Map<String,Object> payParams = new HashMap<>();
        payParams.put("version","1.0");
        payParams.put("service","01");
        payParams.put("merchant_no","123456");
        payParams.put("total","1000000");//10000.00
        payParams.put("name","转账10000");
        payParams.put("remark","转账10000");
        payParams.put("out_trade_no","123456231");
        payParams.put("create_ip","127.0.0.1");
        payParams.put("sub_openid","123454125");
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("sign_type","MD5");
        String cipherCode = "123445566";
        String sign = Utils.encrypt(payParams,cipherCode);
        payParams.put("sign",sign);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> entity = restTemplate.postForEntity("http://127.0.0.1:8080/jh/pay/unifiedorder",payParams,String.class, new Object[0]);
        System.out.println(entity.getBody());
    }
}
