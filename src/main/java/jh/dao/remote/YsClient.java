package jh.dao.remote;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class YsClient implements PayClient {
    private static final String payUrl = "https://www.uuplus.cc/index.php?g=Wap&m=BankPay&a=apiPay";
    private static final String checkPayUrl = "https://www.uuplus.cc/index.php?g=Wap&m=BankPay&a=queryOrder";

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        return null;
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
        return null;
    }

    @Override
    public Map<String, Object> refundorderinfo(Map<String, Object> params) {
        return null;
    }

//    public String pay(Map<String,Object> data) {
//        RemoteParams params = new RemoteParams(payUrl).withParams(data);
//        return super.post(params);
//    }
//
//    public Map<String,String> getPayResult(String mchId,String outTradeNo) {
//        return MapUtils.buildMap("trade_state","SUCCESS","out_trade_no",outTradeNo);
////        RemoteParams params = new RemoteParams(checkPayUrl).withParam("mch_id",mchId).withParam("out_trade_no",outTradeNo);
////        String result = super.post(params);
////        return new Gson().fromJson(result,new TypeToken<Map<String,String>>(){}.getType());
//    }
}
