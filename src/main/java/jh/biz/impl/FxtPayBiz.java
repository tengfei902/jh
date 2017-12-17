package jh.biz.impl;

import hf.base.contants.CodeManager;
import hf.base.enums.*;
import hf.base.exceptions.BizFailException;
import hf.base.utils.Utils;
import jh.biz.service.CacheService;
import jh.biz.service.PayService;
import jh.dao.local.*;
import jh.dao.remote.PayClient;
import jh.model.dto.*;
import jh.model.po.*;
import jh.model.remote.RefundRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class FxtPayBiz extends AbstractPayBiz {
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private PayService payService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private PayMsgRecordDao payMsgRecordDao;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    @Qualifier("fxtClient")
    private PayClient payClient;

    private String[] needFields = {"service","version","merchant_no","outlet_no","total","name","remark","out_trade_no",
    "create_ip","out_notify_url","authcode","nonce_str","sign"};

    @Override
    public void checkParam(Map<String, Object> map) {
        checkField(map);
        String service = String.valueOf(map.get("service"));
        String sub_openid = String.valueOf(map.get("sub_openid"));
        String buyer_id = String.valueOf(map.get("buyer_id"));

        if("01".equalsIgnoreCase(service) && Utils.isEmpty(sub_openid)) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"参数错误,微信公众号支付sub_openid不能为空");
        }

        if("06".equalsIgnoreCase(service) && Utils.isEmpty(buyer_id)) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"支付宝服务窗体支付buyer_id不能为空");
        }
    }

    @Override
    public Long savePayRequest(Map<String, Object> map) {
        UserGroup userGroup = cacheService.getGroup(String.valueOf(map.get("merchant_no")));
        if(Objects.isNull(userGroup) || userGroup.getStatus() != GroupStatus.AVAILABLE.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        UserChannel userChannel = userChannelDao.selectByMchId(String.valueOf(map.get("merchant_no")),String.valueOf(map.get("service")));
        if(Objects.isNull(userChannel) || userChannel.getStatus() != UserChannelStatus.VALID.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        Channel channel = channelDao.selectByPrimaryKey(userChannel.getChannelId());
        if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        //check enth
        if(!Utils.checkEncrypt(map,userChannel.getCipherCode())) {
            throw new BizFailException(CodeManager.CHECK_ENCRIPT_FAILED,"验签失败");
        }

        String service = String.valueOf(map.get("service"));
        String version = String.valueOf(map.get("version"));
        String merchant_no = String.valueOf(map.get("merchant_no"));
        String outlet_no = String.valueOf(map.get("outlet_no"));
        int total = Integer.parseInt(map.get("total").toString());
        String name = String.valueOf(map.get("name"));
        String remark = String.valueOf(null==map.get("remark")?"":map.get("remark"));
        String out_trade_no = String.valueOf(map.get("out_trade_no"));
        String create_ip = String.valueOf(map.get("create_ip"));
        String out_notify_url = String.valueOf(map.get("out_notify_url"));
        String sub_openid = String.valueOf(map.get("sub_openid"));
        String buyer_id = String.valueOf(map.get("buyer_id"));
        String authcode = String.valueOf(map.get("authcode"));
        String nonce_str = String.valueOf(map.get("nonce_str"));
        String sign_type = "MD5";
        String sign = String.valueOf(map.get("sign"));

        AdminBankCard adminBankCard = payService.chooseAdminBank1(userGroup.getCompanyId(),new BigDecimal(total));
        Map<String,Object> outRequest = new HashMap<>();
        outRequest.put("service",service);
        outRequest.put("version",version);
        outRequest.put("merchant_no",adminBankCard.getMchId());
        outRequest.put("outlet_no",adminBankCard.getOutletNo());
        outRequest.put("total",total);
        outRequest.put("name",name);
        outRequest.put("remark",remark);
        outRequest.put("out_trade_no",out_trade_no);
        outRequest.put("create_ip",create_ip);
        outRequest.put("out_notify_url",channel.getUrl());
        if(StringUtils.isNotEmpty(sub_openid)) {
            outRequest.put("sub_openid",sub_openid);
        }
        if(StringUtils.isNotEmpty(buyer_id)) {
            outRequest.put("buyer_id",buyer_id);
        }
        outRequest.put("authcode",authcode);
        outRequest.put("nonce_str",Utils.getRandomString(8));
        outRequest.put("sign_type","MD5");
        sign = Utils.encrypt(outRequest,adminBankCard.getCipherCode());
        outRequest.put("sign",sign);

        //save record
        PayMsgRecord inPayMsgRecord = new PayMsgRecord(out_trade_no,merchant_no,service, OperateType.USER_HF.getValue(), TradeType.PAY.getValue(),map);
        PayMsgRecord outPayMsgRecord = new PayMsgRecord(out_trade_no,adminBankCard.getMchId(),service,OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue(),outRequest);

        PayRequest payRequest = new PayRequest();
        payRequest.setOutTradeNo(out_trade_no);
        payRequest.setBody(String.format("%s,%s",name,remark));
        payRequest.setMchId(merchant_no);
        payRequest.setSubOpenid(sub_openid);
        payRequest.setBuyerId(buyer_id);
        payRequest.setService(service);
        payRequest.setAppid("");
        payRequest.setSign(sign);
        payRequest.setTotalFee(total);
        payService.savePayRequest(Arrays.asList(inPayMsgRecord,outPayMsgRecord),payRequest);

        return payRequest.getId();
    }

    private void checkField(Map<String,Object> map) {
        for(String field:needFields) {
            if(Objects.isNull(map.get(field)) || StringUtils.isBlank(map.get(field).toString()) || StringUtils.equalsIgnoreCase("null",map.get(field).toString())) {
                throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,String.format("参数错误:%s",field));
            }
        }
    }

    @Override
    PayClient getPayClient() {
        return payClient;
    }

    @Override
    public RefundResponse refund(RefundRequest refundRequest) {
        return null;
    }

    @Override
    public ReverseResponse reverse(ReverseRequest reverseRequest) {
        return null;
    }
}
