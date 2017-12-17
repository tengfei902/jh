package jh.model.po;

import hf.base.annotations.Field;

import java.math.BigDecimal;

public class Channel {
    private Long id;
    @Field(required = true)
    private String channelCode;
    @Field(required = true)
    private String codeDesc;
    @Field(required = true)
    private String channelName;
    @Field(required = true,type = Field.Type.number)
    private BigDecimal feeRate;
    @Field(required = true)
    private String url;
    @Field(required = true,type = Field.Type.number)
    private Integer status;
    @Field(required = true)
    private String channelNo;

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
        this.channelCode = channelCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }
}