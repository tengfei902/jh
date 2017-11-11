package jh.model;

import jh.model.annotations.Field;

import java.util.Date;

public class UserInfo {
    @Field(required = true,alias = "userId")
    private Long id;

    private String loginId;

    private String password;
    @Field(required = true)
    private String name;
    @Field(required = true)
    private String idCard;
    @Field(required = true)
    private String tel;
    @Field(required = true)
    private String qq;
    @Field(required = true)
    private Date birthdate;
    @Field(required = true)
    private Integer sex;
    @Field(required = true)
    private String address;

    private Integer type;
    @Field(required = true)
    private Integer status;

    private Long subUserId;

    private Long adminId;

    private Date createTime;

    private String userNo;
    private String subUserNo;

    private String subUserName;
    private String inviteCode;
    private Long groupId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId == null ? null : loginId.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getSubUserId() {
        return subUserId;
    }

    public void setSubUserId(Long subUserId) {
        this.subUserId = subUserId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSubUserName() {
        return subUserName;
    }

    public void setSubUserName(String subUserName) {
        this.subUserName = subUserName;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getSubUserNo() {
        return subUserNo;
    }

    public void setSubUserNo(String subUserNo) {
        this.subUserNo = subUserNo;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public enum STATUS {
        init(0,"用户完善资料"),
        processing(1,"待认证"),
        available(2,"正常"),
        failed(3,"禁用");

        private int value;
        private String desc;

        STATUS(int value,String desc) {
            this.value = value;
            this.desc = desc;
        }

        public static STATUS parse(int value) {
            for(STATUS status: STATUS.values()) {
                if(value == status.value) {
                    return status;
                }
            }
            return null;
        }

        public String getDesc() {
            return this.desc;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum TYPE {
        INIT(0,"初始"),
        ADMIN(1,"管理员"),
        AGENT(2,"代理商"),
        CUSTOMER(3,"普通商户"),
        SUPER_ADMIN(4,"超级管理员");

        private int value;
        private String desc;

        TYPE(int value,String desc) {
            this.value = value;
            this.desc = desc;
        }

        public static TYPE parse(int value) {
            for(TYPE type:TYPE.values()) {
                if(type.value == value) {
                    return type;
                }
            }
            return null;
        }

        public String getDesc() {
            return this.desc;
        }

        public int getValue() {
            return this.value;
        }
    }
}