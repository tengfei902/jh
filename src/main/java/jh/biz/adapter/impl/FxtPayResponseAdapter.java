package jh.biz.adapter.impl;

import com.google.gson.Gson;
import hf.base.contants.CodeManager;
import hf.base.enums.OperateType;
import hf.base.enums.PayRequestStatus;
import hf.base.enums.TradeType;
import hf.base.exceptions.BizFailException;
import hf.base.utils.Utils;
import jh.api.PayApi;
import jh.biz.adapter.Adapter;
import jh.biz.service.CacheService;
import jh.biz.service.PayService;
import jh.dao.local.PayMsgRecordDao;
import jh.dao.local.PayRequestDao;
import jh.dao.local.UserGroupDao;
import jh.model.dto.trade.unifiedorder.FxtPayResponse;
import jh.model.dto.trade.unifiedorder.HfPayRequest;
import jh.model.dto.trade.unifiedorder.HfPayResponse;
import jh.model.po.PayMsgRecord;
import jh.model.po.PayRequest;
import jh.model.po.UserGroup;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
public class FxtPayResponseAdapter implements Adapter<HfPayResponse> {

    protected Logger logger = LoggerFactory.getLogger(FxtPayResponseAdapter.class);

    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PayMsgRecordDao payMsgRecordDao;
    @Autowired
    private PayService payService;
    @Autowired
    private CacheService cacheService;

    @Override
    public HfPayResponse adpat(Map<String, Object> request) {
        PayMsgRecord inputMsgRecord = (PayMsgRecord)request.get("inputMsg");
        request.remove("inputMsg");
        PayMsgRecord resultMsg = new PayMsgRecord(inputMsgRecord.getOutTradeNo(),inputMsgRecord.getMerchantNo(),inputMsgRecord.getService(), OperateType.CLIENT_HF.getValue(),TradeType.PAY.getValue(),request);
        try {
            payMsgRecordDao.insertSelective(resultMsg);
        } catch (DuplicateKeyException e) {
            logger.warn("msg already exist,%s",new Gson().toJson(resultMsg));
        }

        HfPayResponse defaultResponse = new HfPayResponse();
        defaultResponse.setErrcode(CodeManager.WAITING_RESPONSE);
        defaultResponse.setMessage("等待服务器响应");

        if(MapUtils.isEmpty(request) || Objects.isNull(request.get("errcode"))) {
            return defaultResponse;
        }

        String errCode = String.valueOf(request.get("errcode"));
        String message = String.valueOf(request.get("message"));

        HfPayResponse response = new HfPayResponse();
        response.setErrcode(errCode);
        response.setMessage(message);

        PayMsgRecord hfResultMsg = new PayMsgRecord(inputMsgRecord.getOutTradeNo(),inputMsgRecord.getMerchantNo(),inputMsgRecord.getService(), OperateType.HF_USER.getValue(),TradeType.PAY.getValue(),response);

        if(StringUtils.equalsIgnoreCase("0",errCode) || StringUtils.equalsIgnoreCase("4",errCode)) {
            PayRequest payRequest = payRequestDao.selectByTradeNo(String.valueOf(request.get("out_trade_no")));
            UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
            response.setNo(String.valueOf(payRequest.getId()));
            response.setOut_trade_no(payRequest.getOutTradeNo().split("_")[1]);
            if(!Objects.isNull(request.get("pay_info"))) {
                response.setPay_info(String.valueOf(request.get("pay_info")));
            }
            if(!Objects.isNull(request.get("total"))) {
                response.setTotal(String.valueOf(request.get("total")));
            }
            response.setSign_type("MD5");
            String sign = Utils.encrypt(hf.base.utils.MapUtils.beanToMap(response),userGroup.getCipherCode());

            response.setSign(sign);
            payService.remoteSuccess(payRequest,hfResultMsg);
        } else {
            payService.payFailed(inputMsgRecord.getOutTradeNo(),hfResultMsg);
        }
        return response;
    }
}
