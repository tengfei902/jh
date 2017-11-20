package jh.api;

import com.google.gson.Gson;
import jh.biz.PayBiz;
import jh.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by tengfei on 2017/10/27.
 */
@Controller("/pay")
public class PayController {
//    @Autowired
//    private PayBiz payBiz;
//
//    /**
//     * 收单
//     */
//    @RequestMapping(value = "/unifiedorder",method = RequestMethod.POST,produces = "text/json;charset=UTF-8")
//    public @ResponseBody String unifiedorder(@RequestBody PayRequestDto request) {
//        PayResponse response = payBiz.pay(request);
//        return new Gson().toJson(response);
//    }
//
//    /**
//     * 退款
//     * @return
//     */
//    @RequestMapping(value = "/refundorder",method = RequestMethod.POST,produces = "text/json;charset=UTF-8")
//    public @ResponseBody String refundorder(@RequestBody RefundRequest refundRequest) {
//        RefundResponse response = payBiz.refund(refundRequest);
//        return new Gson().toJson(response);
//    }
//
//    @RequestMapping(value = "/reverseorder",method = RequestMethod.POST,produces = "text/json;charset=UTF-8")
//    public @ResponseBody String reverseorder(@RequestBody ReverseRequest reverseRequest) {
//        ReverseResponse response = payBiz.reverse(reverseRequest);
//        return new Gson().toJson(response);
//    }
}
