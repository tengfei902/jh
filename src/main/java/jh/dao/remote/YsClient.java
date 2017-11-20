package jh.dao.remote;

import hf.base.client.BaseClient;
import hf.base.model.RemoteParams;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class YsClient extends BaseClient {
    private static final String payUrl = "https://www.uuplus.cc/index.php?g=Wap&m=BankPay&a=apiPay";

    public String pay(Map<String,Object> data) {
        RemoteParams params = new RemoteParams(payUrl).withParams(data);
        return super.post(params);
    }
}
