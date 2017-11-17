package jh.model.po;

import java.math.BigDecimal;

public class Channel {
    private Long id;

    private String channelCode;

    private String channelName;

    private String channelType;

    private BigDecimal feeRate;

    private String mchId;

    private String cipherCode;

    private String url;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode == null ? null : channelCode.trim();
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName == null ? null : channelName.trim();
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType == null ? null : channelType.trim();
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getCipherCode() {
        return cipherCode;
    }

    public void setCipherCode(String cipherCode) {
        this.cipherCode = cipherCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public enum ChannelStatus {
        VALID(0,"可用"),INVALID(99,"不可用");

        private int value;
        private String desc;

        ChannelStatus(int value,String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum ChannelType {
        WE_CODE("1","微信扫码"),
        WE_H5("2","微信H5"),
        ZFB("3","支付宝扫码"),
        ZFB_H5("4","支付宝H5"),
        BANK("5","网银跳转"),
        BANK_DIR("6","网银直连"),
        BAIDU("7","百度钱包"),
        QQ("8","QQ钱包"),
        JD("9","京东钱包");

        private String value;
        private String desc;

        ChannelType(String value,String desc) {
            this.value = value;
            this.desc = desc;
        }

        public String getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }
}