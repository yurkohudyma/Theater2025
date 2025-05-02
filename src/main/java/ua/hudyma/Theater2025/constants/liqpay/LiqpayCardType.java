package ua.hudyma.Theater2025.constants.liqpay;

public enum LiqpayCardType {
    MASTER_CARD("mc"),
    VISA("visa"),
    MAESTRO("maestro"),
    HIPER("hiper");

    private final String value;

    LiqpayCardType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LiqpayCardType fromValue(String value) {
        for (LiqpayCardType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown card type: " + value);
    }
}
