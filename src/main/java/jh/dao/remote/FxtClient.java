package jh.dao.remote;

import jh.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
/**
 * Created by tengfei on 2017/10/28.
 */
@Component
public class FxtClient {

    private String URL = "http://pay.51fuxintong.com/wx.php/OpenApi";
    private String UNIFIEDORDER = "/unifiedorder";

    @Autowired
    private RestTemplate restTemplate;

    public PayResponse unifiedorder(PayRequest payRequest) {
        ResponseEntity<PayResponse> response = restTemplate.postForEntity(URL+UNIFIEDORDER,payRequest, PayResponse.class, Collections.EMPTY_MAP);
        return response.getBody();
    }

    public RefundResponse refund(RefundRequest refundRequest) {
        ResponseEntity<RefundResponse> response = restTemplate.postForEntity(URL+UNIFIEDORDER,refundRequest, RefundResponse.class, Collections.EMPTY_MAP);
        return response.getBody();
    }

    public ReverseResponse reverse(ReverseRequest reverseRequest) {
        ResponseEntity<ReverseResponse> response = restTemplate.postForEntity(URL+UNIFIEDORDER,reverseRequest, ReverseResponse.class, Collections.EMPTY_MAP);
        return response.getBody();
    }
}
