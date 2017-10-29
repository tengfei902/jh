package jh.model;

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

    public interface OPR_TYPE {
        int UNIFIED = 0;
        int REFUND = 1;
        int REVERSE = 2;
        int WITHDRAW = 3;
    }

    public interface STATUS {
        int INIT = 0;
        int SUCCESS = 1;
        int FAILED = 99;
    }
}