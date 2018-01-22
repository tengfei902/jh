package jh.biz.adapter.impl;

import hf.base.contants.CodeManager;
import hf.base.exceptions.BizFailException;
import hf.base.utils.MapUtils;
import hf.base.utils.Utils;
import jh.biz.adapter.Adapter;
import jh.biz.service.CacheService;
import jh.dao.local.UserGroupExtDao;
import jh.model.dto.trade.query.FxtQueryRequest;
import jh.model.po.ChannelProvider;
import jh.model.po.UserGroup;
import jh.model.po.UserGroupExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class FxtQueryRequestAdapter implements Adapter<FxtQueryRequest> {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;

    @Override
    public FxtQueryRequest adpat(Map<String, Object> request) {
        String merchant_no = String.valueOf(request.get("mchId"));
        String out_trade_no = String.valueOf(request.get("outTradeNo"));

        UserGroup userGroup = cacheService.getGroup(merchant_no);

        if(Objects.isNull(userGroup)) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"商户不存在");
        }

        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(), hf.base.enums.ChannelProvider.FXT.getCode());

        FxtQueryRequest fxtQueryRequest = new FxtQueryRequest();
        fxtQueryRequest.setOut_trade_no(out_trade_no);
        fxtQueryRequest.setVersion("1.0");
        fxtQueryRequest.setMerchant_no(userGroupExt.getMerchantNo());
        fxtQueryRequest.setSign_type("MD5");
        fxtQueryRequest.setNonce_str(Utils.getRandomString(10));
        String sign = Utils.encrypt(MapUtils.beanToMap(fxtQueryRequest),userGroupExt.getCipherCode());
        fxtQueryRequest.setSign(sign);
        return fxtQueryRequest;
    }
}
