package jh.dao.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

/**
 * Created by tengfei on 2017/10/28.
 */
@Component
public class FxtClient implements PayClient {

    private String URL = "http://pay.51fuxintong.com/wx.php/OpenApi";
    private String UNIFIEDORDER = "/unifiedorder";

    @Autowired
    private RestTemplate restTemplate;

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

//    public PayResponse unifiedorder(PayRequestDto payRequestDto) {
//        ResponseEntity<PayResponse> response = restTemplate.postForEntity(URL+UNIFIEDORDER, payRequestDto, PayResponse.class, Collections.EMPTY_MAP);
//        return response.getBody();
//    }
//
//    public RefundResponse refund(RefundRequest refundRequest) {
//        ResponseEntity<RefundResponse> response = restTemplate.postForEntity(URL+UNIFIEDORDER,refundRequest, RefundResponse.class, Collections.EMPTY_MAP);
//        return response.getBody();
//    }
//
//    public ReverseResponse reverse(ReverseRequest reverseRequest) {
//        ResponseEntity<ReverseResponse> response = restTemplate.postForEntity(URL+UNIFIEDORDER,reverseRequest, ReverseResponse.class, Collections.EMPTY_MAP);
//        return response.getBody();
//    }
}
