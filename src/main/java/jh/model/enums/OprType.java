package jh.model.enums;

/**
 * Created by tengfei on 2017/11/5.
 */
public enum OprType {
    UNIFIED(0,"付款"),
    REFUND(1,"退款"),
    REVERSE(2,"撤销交易"),
    WITHDRAW(3,"结算"),
    BONUS(4,"提成");

    private int value;
    private String desc;

    OprType(int value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static OprType parse(int value) {
        for(OprType oprType:OprType.values()) {
            if(oprType.value == value) {
                return oprType;
            }
        }
        return null;
    }

    public String getDesc() {
        return this.desc;
    }
}
