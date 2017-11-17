package jh.model.po;

import jh.model.dto.PayRequest;
import org.mybatis.generator.api.dom.java.Interface;

import java.util.Date;

public class PayTrdOrder {
    private Long id;

    private String service;

    private String merchantNo;

    private String outletNo;

    private Integer total;

    private String name;

    private String remark;

    private String outTradeNo;

    private String createIp;

    private String outNotifyUrl;

    private String subOpenid;

    private String buyerId;

    private String authcode;

    private String nonceStr;

    private String signType;

    private String sign;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private Integer version;

    public PayTrdOrder(PayRequest request) {
        this.buyerId = request.getBuyer_id();
        this.createIp = request.getCreate_ip();
        this.outTradeNo = request.getOut_trade_no();
        this.authcode = request.getAuthcode();
        this.merchantNo = request.getMerchant_no();
        this.name = request.getName();
        this.nonceStr = request.getNonce_str();
        this.outletNo = request.getOutlet_no();
        this.outNotifyUrl = request.getOut_notify_url();
        this.outTradeNo = request.getOut_trade_no();
        this.remark = request.getRemark();
        this.service = request.getService();
        this.sign = request.getSign();
        this.signType = request.getSign_type();
        this.subOpenid = request.getSub_openid();
        this.total = request.getTotal();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getOutletNo() {
        return outletNo;
    }

    public void setOutletNo(String outletNo) {
        this.outletNo = outletNo;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }

    public String getOutNotifyUrl() {
        return outNotifyUrl;
    }

    public void setOutNotifyUrl(String outNotifyUrl) {
        this.outNotifyUrl = outNotifyUrl;
    }

    public String getSubOpenid() {
        return subOpenid;
    }

    public void setSubOpenid(String subOpenid) {
        this.subOpenid = subOpenid;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public interface STATUS {
        int INIT = 10;
        int SUCCESS = 0;
        int CHECK_FAILED = 1;
        int PARAM_FAILED = 2;
        int FAILED = 3;
        int WAITING_PAY = 4;
        int REFUND = 5;
        int REVERSE = 6;
    }
}