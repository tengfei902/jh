package jh.biz.trade.impl;

import hf.base.utils.MapUtils;
import hf.base.utils.Utils;
import jh.biz.adapter.Adapter;
import jh.biz.adapter.impl.*;
import jh.dao.remote.PayClient;
import jh.model.dto.trade.query.FxtQueryRequest;
import jh.model.dto.trade.refund.FxtRefundRequest;
import jh.model.dto.trade.refund.HfRefundResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FxtTradeBiz extends AbstractTradeBiz {
    @Autowired
    @Qualifier("fxtClient")
    private PayClient payClient;
    @Autowired
    private FxtPayRequestAdapter fxtPayRequestAdapter;
    @Autowired
    private FxtPayResponseAdapter fxtPayResponseAdapter;
    @Autowired
    private FxtQueryRequestAdapter fxtQueryRequestAdapter;
    @Autowired
    private FxtRefundRequestAdapter fxtRefundRequestAdapter;
    @Autowired
    private FxtRefundResponseAdapter fxtRefundResponseAdapter;

    @Override
    Adapter getRequestAdapter() {
        return fxtPayRequestAdapter;
    }

    @Override
    Adapter getResponseAdapter() {
        return fxtPayResponseAdapter;
    }

    @Override
    PayClient getClient() {
        return payClient;
    }

    @Override
    String getSign(Map<String, Object> map, String cipherCode) {
        return Utils.encrypt(map,cipherCode);
    }

    @Override
    public Map<String, Object> query(Map<String, Object> requestMap) {
        FxtQueryRequest fxtQueryRequest = fxtQueryRequestAdapter.adpat(requestMap);
        Map<String,Object> resultMap = payClient.orderinfo(MapUtils.beanToMap(fxtQueryRequest));
        return null;
    }

    @Override
    public Map<String, Object> refund(Map<String, Object> requestMap) {
        FxtRefundRequest fxtRefundRequest = fxtRefundRequestAdapter.adpat(requestMap);
        Map<String,Object> refundResult = payClient.refundorder(MapUtils.beanToMap(fxtRefundRequest));
        HfRefundResponse hfRefundResponse = fxtRefundResponseAdapter.adpat(refundResult);
        return MapUtils.beanToMap(hfRefundResponse);
    }
}
