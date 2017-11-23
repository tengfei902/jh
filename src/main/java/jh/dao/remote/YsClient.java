package jh.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hf.base.client.BaseClient;
import hf.base.model.RemoteParams;
import hf.base.utils.MapUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class YsClient extends BaseClient {
    private static final String payUrl = "https://www.uuplus.cc/index.php?g=Wap&m=BankPay&a=apiPay";
    private static final String checkPayUrl = "https://www.uuplus.cc/index.php?g=Wap&m=BankPay&a=queryOrder";

    public String pay(Map<String,Object> data) {
        RemoteParams params = new RemoteParams(payUrl).withParams(data);
        return super.post(params);
    }

    public Map<String,String> getPayResult(String mchId,String outTradeNo) {
        return MapUtils.buildMap("trade_state","SUCCESS","out_trade_no",outTradeNo);
//        RemoteParams params = new RemoteParams(checkPayUrl).withParam("mch_id",mchId).withParam("out_trade_no",outTradeNo);
//        String result = super.post(params);
//        return new Gson().fromJson(result,new TypeToken<Map<String,String>>(){}.getType());
    }
}
