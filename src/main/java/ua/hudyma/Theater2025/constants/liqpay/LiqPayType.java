package ua.hudyma.Theater2025.constants.liqpay;

public enum LiqPayType {
    BUY("buy"),
    DONATE("donate"),
    INVOICE("invoice"),
    AUTH("auth"),
    VERIFY_3DS("3ds_verify"),
    PAY_OUT("pay_out"),
    TRANSFER("transfer"),
    SUBSCRIBE("subscribe");

    private final String value;

    LiqPayType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LiqPayType fromValue(String value) {
        for (LiqPayType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown LiqPay type: " + value);
    }
}

