package jh.model;

import jh.model.enums.OprType;

import java.math.BigDecimal;
import java.util.Date;

public class AccountOprLog {
    private Long id;

    private Long accountId;

    private Long userId;

    private Long trdOrderId;

    private String outTradeNo;

    private String no;

    private Integer oprType;

    private String remark;

    private BigDecimal price;

    private Integer status;

    private Integer version;

    private Date createTime;

    private Date updateTime;

    private Long channelId;

    private Integer channelType;

    private String channel;

    private BigDecimal userFeeRate;

    private BigDecimal priceIn;

    private BigDecimal fee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTrdOrderId() {
        return trdOrderId;
    }

    public void setTrdOrderId(Long trdOrderId) {
        this.trdOrderId = trdOrderId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no == null ? null : no.trim();
    }

    public Integer getOprType() {
        return oprType;
    }

    public void setOprType(Integer oprType) {
        this.oprType = oprType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    public BigDecimal getUserFeeRate() {
        return userFeeRate;
    }

    public void setUserFeeRate(BigDecimal userFeeRate) {
        this.userFeeRate = userFeeRate;
    }

    public BigDecimal getPriceIn() {
        return priceIn;
    }

    public void setPriceIn(BigDecimal priceIn) {
        this.priceIn = priceIn;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public enum OprStatus {
        INIT(0,"待付款"),
        PAID(1,"已付款"),
        REVERSE(2,"已撤销交易"),
        REFUND(3,"已退款"),
        CLOSE(4,"已结算"),
        CLOSING(10,"结算中"),
        FAILED(99,"结算失败");

        private int value;
        private String desc;

        OprStatus(int value,String desc) {
            this.value = value;
            this.desc = desc;
        }

        public static OprStatus parse(int value) {
            for(OprStatus oprStatus:OprStatus.values()) {
                if(value == oprStatus.value) {
                    return oprStatus;
                }
            }
            return null;
        }

        public String getDesc() {
            return this.desc;
        }
    }
}