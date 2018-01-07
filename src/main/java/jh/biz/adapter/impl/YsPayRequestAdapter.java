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
import jh.model.dto.trade.unifiedorder.HfPayRequest;
import jh.model.dto.trade.unifiedorder.YsPayRequest;
import jh.model.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Service
public class YsPayRequestAdapter implements Adapter<YsPayRequest> {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private PayService payService;

    @Override
    public YsPayRequest adpat(Map<String, Object> request) {
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

        UserChannel userChannel = userChannelDao.selectByGroupChannelCode(userGroup.getId(),service, ChannelProvider.YS.getCode());
        if(Objects.isNull(userChannel) || userChannel.getStatus() != UserChannelStatus.VALID.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        Channel channel = channelDao.selectByPrimaryKey(userChannel.getChannelId());
        if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.YS.getCode());
        if(Objects.isNull(userGroupExt)) {
            throw new BizFailException("未注册，权限不足");
        }

        ChannelCode channelCode = ChannelCode.parseFromCode(service);

        if(channelCode == ChannelCode.WX_OPEN && Utils.isEmpty(payRequest.getSub_openid())) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"参数错误,微信公众号支付sub_openid不能为空");
        }
        if(channelCode == ChannelCode.ALI_OPEN && Utils.isEmpty(payRequest.getBuyer_id())) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"支付宝服务窗体支付buyer_id不能为空");
        }

        String outTradeNo = String.format("%s_%s",payRequest.getMerchant_no(),payRequest.getOut_trade_no());
        String outMerchantNo = userGroupExt.getMerchantNo();
        String outOutLetNo = userGroupExt.getOutletNo();
        String outCipherCode = userGroupExt.getCipherCode();

        YsPayRequest ysPayRequest = new YsPayRequest();
        ysPayRequest.setTotal_fee(payRequest.getTotal());
        ysPayRequest.setBody(payRequest.getName());
        ysPayRequest.setOut_trade_no(outTradeNo);
        ysPayRequest.setMch_id(outMerchantNo);
        if(!Utils.isEmpty(payRequest.getSub_openid())) {
            ysPayRequest.setSub_openid(payRequest.getSub_openid());
        }
        if(!Utils.isEmpty(payRequest.getBuyer_id())) {
            ysPayRequest.setBuyer_id(payRequest.getBuyer_id());
        }
        ysPayRequest.setService(ChannelCode.parseFromCode(payRequest.getService()).getYsCode());
//        ysPayRequest.setAppid();
        PayMsgRecord inPayMsgRecord = new PayMsgRecord(outTradeNo,payRequest.getMerchant_no(),service, OperateType.USER_HF.getValue(), TradeType.PAY.getValue(),request);
        PayMsgRecord outPayMsgRecord = new PayMsgRecord(outTradeNo,payRequest.getMerchant_no(),channel.getChannelCode(),OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue(),outCipherCode, MapUtils.beanToMap(ysPayRequest));

        PayRequest hfPayRequest = new PayRequest();
        hfPayRequest.setOutTradeNo(outTradeNo);
        hfPayRequest.setBody(hfPayRequest.getBody());
        hfPayRequest.setMchId(hfPayRequest.getMchId());
        hfPayRequest.setSubOpenid(hfPayRequest.getSubOpenid());
        hfPayRequest.setBuyerId(hfPayRequest.getBuyerId());
        hfPayRequest.setService(hfPayRequest.getService());
        hfPayRequest.setAppid(hfPayRequest.getAppid());
        hfPayRequest.setSign(hfPayRequest.getSign());
        hfPayRequest.setTotalFee(hfPayRequest.getTotalFee());
        hfPayRequest.setChannelProviderCode(ChannelProvider.YS.getCode());
        payService.savePayRequest(Arrays.asList(inPayMsgRecord,outPayMsgRecord),hfPayRequest);

        return ysPayRequest;
    }
}
