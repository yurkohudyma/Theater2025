package ua.hudyma.Theater2025.constants.liqpay;

public enum LiqPayStatus {
    SUCCESS("success"),
    SANDBOX("sandbox"),
    FAILURE("failure"),
    ERROR("error"),
    SUBSCRIBED("subscribed"),
    UNSUBSCRIBED("unsubscribed"),
    WAIT_ACCEPT("wait_accept"),
    WAIT_SECURE("wait_secure"),
    WAIT_CARD("wait_card"),
    WAIT_COMPENSATION("wait_compensation"),
    PROCESSING("processing"),
    REVERSED("reversed"),
    REFUNDED("refunded"),
    EXPIRED("expired");

    private final String value;

    LiqPayStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LiqPayStatus fromValue(String value) {
        for (LiqPayStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown LiqPay status: " + value);
    }
}
