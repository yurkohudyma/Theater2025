package ua.hudyma.Theater2025.constants.liqpay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LiqpayCardType {
    MASTER_CARD("mc"),
    VISA("visa"),
    MAESTRO("maestro"),
    HIPER("hiper");

    private final String value;

    LiqpayCardType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LiqpayCardType fromValue(String value) {
        for (LiqpayCardType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown card type: " + value);
    }
}
