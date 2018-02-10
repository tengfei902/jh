package jh.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hf.base.client.BaseClient;
import hf.base.exceptions.BizFailException;
import hf.base.model.RemoteParams;
import hf.base.utils.EpaySignUtil;
import hf.base.utils.MapUtils;
import hf.base.utils.Utils;
import jh.biz.service.CacheService;
import jh.utils.CipherUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WwClient extends BaseClient implements PayClient {
    @Autowired
    private CacheService cacheService;

    private static final String H5_PAY_URL = "http://47.97.175.195:8692/pay/payment/toH5";
    private static final String WY_PAY_URL = "http://pay1.hlqlb.cn:8692/pay/payment/toPayment";
    private static final String QUERY_URL = "http://47.97.175.195:8682/posp/cashierDesk/orderQuery";

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        String url = null;
        if(StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"04")) {
            url = H5_PAY_URL;
        }
        if(StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"09")) {
            url = WY_PAY_URL;
        }
        if(url == null) {
            throw new BizFailException("channelCode not exist");
        }
        params.remove("channelCode");
        String sign = EpaySignUtil.sign(CipherUtils.private_key, Utils.getEncryptStr(params));
        params.put("signStr",sign);

        RemoteParams remoteParams = new RemoteParams(url).withParams(params);
        String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED);
        String outTradeNo = String.valueOf(params.get("orderNum"));
        return MapUtils.buildMap("payResult",result,"outTradeNo",outTradeNo);
    }

    @Override
    public Map<String, Object> refundorder(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> reverseorder(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> orderinfo(Map<String, Object> params) {
        RemoteParams remoteParams = new RemoteParams(QUERY_URL).withParams(params);
        String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED);
        return new Gson().fromJson(result,new TypeToken<Map<String,String>>(){}.getType());
    }

    @Override
    public Map<String, Object> refundorderinfo(Map<String, Object> params) {
        return null;
    }
}
