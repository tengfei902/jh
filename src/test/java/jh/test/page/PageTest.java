package jh.test.page;

import com.google.gson.Gson;
import hf.base.utils.Utils;
import jh.biz.service.PageService;
import jh.model.dto.PageInfo;
import jh.test.BaseTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tengfei on 2017/10/29.
 */
public class PageTest extends BaseTestCase {
    @Autowired
    private PageService pageService;

    @Test
    public void getPageInfo() {
        List<PageInfo> pageInfoList = pageService.getPageList();

        System.out.println(new Gson().toJson(pageInfoList));

    }

    @Test
    public void testGetCipherCode() {
        System.out.println(Utils.getRandomString(8));
    }

    @Test
    public void testGetRandom() {
        Map<String,Object> param = new HashMap<>();
        param.put("total","1200");
        param.put("buyer_id","");
        param.put("merchant_no","5151");
        param.put("remark","");
        param.put("sub_open_id",null);
        param.put("sign_type","MD5");
        param.put("create_ip","127.0.0.1");
        param.put("nonce_str","1516542284541363");
        param.put("out_trade_no","1516542284541363");
        param.put("version","1.0");
        param.put("name","Lotto");
        param.put("service","02");
        param.put("auth_code",null);
        param.put("outlet_no",null);

        String sign = Utils.encrypt(param,"y6sfdfdf");
        System.out.println(sign);
    }
}
