package jh.biz.trade.impl;

import hf.base.utils.MapUtils;
import hf.base.utils.Utils;
import jh.biz.PayBiz;
import jh.biz.adapter.Adapter;
import jh.biz.adapter.impl.*;
import jh.biz.service.CacheService;
import jh.dao.local.UserGroupExtDao;
import jh.dao.remote.PayClient;
import jh.model.dto.trade.query.FxtQueryRequest;
import jh.model.dto.trade.refund.FxtRefundRequest;
import jh.model.dto.trade.refund.HfRefundResponse;
import jh.model.po.PayRequest;
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
    @Qualifier("fxtPayBiz")
    private PayBiz payBiz;
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
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;

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
    public Map<String, Object> query(PayRequest payRequest) {
        FxtQueryRequest fxtQueryRequest = fxtQueryRequestAdapter.adpat(MapUtils.beanToMap(payRequest));
        Map<String,Object> resultMap = payClient.orderinfo(MapUtils.beanToMap(fxtQueryRequest));

//        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
//        UserGroupExt userGroupExt =userGroupExtDao.selectByUnq(userGroup.getId(), ChannelProvider.FXT.getCode());

//        if(!Utils.checkEncrypt(resultMap,userGroupExt.getCipherCode())) {
//            logger.warn(String.format("check encrypt failed,%s,%s,%s",));
//        }

        return resultMap;
    }

    @Override
    public Map<String, Object> refund(Map<String, Object> requestMap) {
        FxtRefundRequest fxtRefundRequest = fxtRefundRequestAdapter.adpat(requestMap);
        Map<String,Object> refundResult = payClient.refundorder(MapUtils.beanToMap(fxtRefundRequest));
        HfRefundResponse hfRefundResponse = fxtRefundResponseAdapter.adpat(refundResult);
        return MapUtils.beanToMap(hfRefundResponse);
    }

    @Override
    PayBiz getPayBiz() {
        return payBiz;
    }
}
