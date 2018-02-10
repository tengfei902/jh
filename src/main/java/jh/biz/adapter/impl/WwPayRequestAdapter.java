package jh.biz.adapter.impl;

import hf.base.contants.CodeManager;
import hf.base.enums.*;
import hf.base.enums.ChannelProvider;
import hf.base.exceptions.BizFailException;
import hf.base.utils.MapUtils;
import hf.base.utils.TypeConverter;
import hf.base.utils.Utils;
import jh.biz.adapter.Adapter;
import jh.biz.service.CacheService;
import jh.biz.service.PayService;
import jh.dao.local.ChannelDao;
import jh.dao.local.UserChannelDao;
import jh.dao.local.UserGroupExtDao;
import jh.model.PropertyConfig;
import jh.model.dto.trade.unifiedorder.HfPayRequest;
import jh.model.dto.trade.unifiedorder.WwPayRequest;
import jh.model.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Service
public class WwPayRequestAdapter implements Adapter<WwPayRequest> {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private PropertyConfig propertyConfig;
    @Autowired
    private PayService payService;

    @Override
    public WwPayRequest adpat(Map<String, Object> request) {
        HfPayRequest payRequest = TypeConverter.convert(request,HfPayRequest.class);

        String merchantNo = payRequest.getMerchant_no();
        String service = payRequest.getService();

        UserGroup userGroup = cacheService.getGroup(merchantNo);
        if(Objects.isNull(userGroup) || userGroup.getStatus() != GroupStatus.AVAILABLE.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        if(!Utils.checkEncrypt(request,userGroup.getCipherCode())) {
            throw new BizFailException(CodeManager.CHECK_ENCRIPT_FAILED,"验签失败");
        }

        UserChannel userChannel = userChannelDao.selectByGroupChannelCode(userGroup.getId(),service, ChannelProvider.WW.getCode());
        if(Objects.isNull(userChannel) || userChannel.getStatus() != UserChannelStatus.VALID.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        Channel channel = channelDao.selectByPrimaryKey(userChannel.getChannelId());
        if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.WW.getCode());
        if(Objects.isNull(userGroupExt)) {
            throw new BizFailException("未注册，权限不足");
        }

        ChannelCode channelCode = ChannelCode.parseFromCode(service);

        String outTradeNo = String.format("%s_%s",payRequest.getMerchant_no(),payRequest.getOut_trade_no());
        String outMerchantNo = userGroupExt.getMerchantNo();
        String outOutLetNo = userGroupExt.getOutletNo();
        String outCipherCode = userGroupExt.getCipherCode();

        WwPayRequest wwPayRequest = new WwPayRequest();

        if(channelCode == ChannelCode.WX_H5) {
            wwPayRequest.setCallbackUrl(propertyConfig.getWwCallbackUrl());
            wwPayRequest.setIp("127.0.0.1");
            wwPayRequest.setMemberCode(outMerchantNo);
            wwPayRequest.setOrderNum(outTradeNo);
            wwPayRequest.setPayMoney(String.valueOf(new BigDecimal(payRequest.getTotal()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)));
            wwPayRequest.setPayType("1");
            wwPayRequest.setChannelCode(payRequest.getService());
        } else if(channelCode == ChannelCode.WY) {
            wwPayRequest.setCallbackUrl(propertyConfig.getWwCallbackUrl());
            wwPayRequest.setMemberCode(outMerchantNo);
            wwPayRequest.setOrderNum(outTradeNo);
            wwPayRequest.setPayMoney(String.valueOf(new BigDecimal(payRequest.getTotal()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)));
            wwPayRequest.setChannelCode(payRequest.getService());
            if(!Utils.isEmpty(payRequest.getBank_code())) {
                wwPayRequest.setBankCode(payRequest.getBank_code());
            }
            if(!Utils.isEmpty(payRequest.getRemark())) {
                wwPayRequest.setGoodsName(payRequest.getRemark());
            }
        } else {
            throw new BizFailException("no channel defined");
        }

        PayMsgRecord inPayMsgRecord = new PayMsgRecord(outTradeNo,payRequest.getMerchant_no(),service, OperateType.USER_HF.getValue(), TradeType.PAY.getValue(),request);
        PayMsgRecord outPayMsgRecord = new PayMsgRecord(outTradeNo,outMerchantNo,channel.getChannelCode(),OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue(),outCipherCode, MapUtils.beanToMap(wwPayRequest));

        PayRequest hfPayRequest = new PayRequest();
        hfPayRequest.setOutTradeNo(outTradeNo);
        hfPayRequest.setBody(String.format("%s_%s",payRequest.getName(),payRequest.getRemark()));
        hfPayRequest.setMchId(payRequest.getMerchant_no());
        hfPayRequest.setSubOpenid(payRequest.getSub_openid());
        hfPayRequest.setBuyerId(payRequest.getBuyer_id());
        hfPayRequest.setService(payRequest.getService());
        hfPayRequest.setAppid("");
        hfPayRequest.setSign(payRequest.getSign());
        hfPayRequest.setTotalFee(Integer.parseInt(payRequest.getTotal()));
        hfPayRequest.setChannelProviderCode(ChannelProvider.WW.getCode());

        payService.savePayRequest(Arrays.asList(inPayMsgRecord,outPayMsgRecord),hfPayRequest);
        return wwPayRequest;
    }
}
