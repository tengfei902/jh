package jh.biz.adapter.impl;

import hf.base.enums.ChannelProvider;
import hf.base.utils.MapUtils;
import hf.base.utils.Utils;
import jh.biz.adapter.Adapter;
import jh.biz.service.CacheService;
import jh.biz.service.PayService;
import jh.dao.local.PayRequestDao;
import jh.dao.local.UserGroupExtDao;
import jh.model.dto.trade.refund.HfRefundResponse;
import jh.model.enums.ErrCode;
import jh.model.po.PayRequest;
import jh.model.po.UserGroup;
import jh.model.po.UserGroupExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FxtRefundResponseAdapter implements Adapter<HfRefundResponse> {
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private PayService payService;

    @Override
    public HfRefundResponse adpat(Map<String, Object> request) {
        HfRefundResponse hfRefundResponse = new HfRefundResponse();

        String errcode = String.valueOf(request.get("errcode"));
        String message = String.valueOf(request.get("message"));

        hfRefundResponse.setErrcode(errcode);
        hfRefundResponse.setMessage(message);

        if(StringUtils.equals(errcode, String.valueOf(ErrCode.SUCCESS.getValue()))) {
            String out_trade_no = String.valueOf(request.get("out_trade_no"));
            PayRequest payRequest = payRequestDao.selectByTradeNo(out_trade_no);
            UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
            UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(), ChannelProvider.FXT.getCode());
            if(!Utils.checkEncrypt(request,userGroupExt.getCipherCode())) {
                //处理中
                hfRefundResponse.setErrcode("4");
                return hfRefundResponse;
            }
            payService.finishRefund(payRequest);

            hfRefundResponse.setNo(String.valueOf(payRequest.getId()));
            hfRefundResponse.setOut_trade_no(out_trade_no.split(",")[1]);
            hfRefundResponse.setRefund_fee(String.valueOf(payRequest.getTotalFee()));
            hfRefundResponse.setSign_type("MD5");

            String sign = Utils.encrypt(MapUtils.beanToMap(hfRefundResponse),userGroup.getCipherCode());
            hfRefundResponse.setSign(sign);
            return hfRefundResponse;
        }

        return hfRefundResponse;
    }
}
