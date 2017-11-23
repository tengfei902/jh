package jh.model.enums;

public enum PayRequestStatus {
    NEW(0),
    OPR_GENERATED(1),
    REMOTE_CALL_FINISHED(2),
    PAY_SUCCESS(5),
    OPR_SUCCESS(10),
    PAY_FAILED(98),
    OPR_FINISHED(99);

    private int value;

    PayRequestStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PayRequestStatus parse(int value) {
        for(PayRequestStatus status:PayRequestStatus.values()) {
            if(status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
