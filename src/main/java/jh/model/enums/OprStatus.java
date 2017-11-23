package jh.model.enums;

public enum OprStatus {
    NEW(0),
    PAY_SUCCESS(1),
    PAY_FAILED(99),
    FINISHED(10),
    REFUND(98);

    private int value;

    OprStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
