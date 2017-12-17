package jh.biz.handler;

import hf.base.exceptions.BizFailException;
import hf.base.utils.TypeConverter;
import jh.biz.PayBiz;
import jh.biz.validator.ValidateResult;
import jh.biz.validator.Validator;
import jh.model.dto.PayResponse;
import jh.model.po.PayRequest;
import jh.model.remote.FxtRefundRequest;

import java.util.List;
import java.util.Map;

public class PreHandler implements Handler {

    private List<Validator> validators;
    private PayBiz payBiz;

    @Override
    public void handle(Map<String, Object> map) {
//        for(Validator validator:validators) {
//            ValidateResult validateResult = validator.validate(map);
//            if(!validateResult.isSuccess()) {
//                throw new BizFailException(validateResult.getMsg());
//            }
//        }
//
//        FxtRefundRequest inRequest;
//        try {
//            inRequest = TypeConverter.convert(map, FxtRefundRequest.class);
//        } catch (Exception e) {
//            throw new BizFailException(e.getMessage());
//        }
//
//
//
//        FxtRefundRequest outRequest = convert(inRequest);
//        PayRequest payRequest = new PayRequest(inRequest);
//        PayResponse payResponse = payBiz.pay();

    }

    private FxtRefundRequest convert(FxtRefundRequest inRequest) {
        return new FxtRefundRequest();
    }
}
