package ua.hudyma.Theater2025.constants.liqpay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LiqPayAction {
    PAY("pay"),
    HOLD("hold"),
    SUBSCRIBE("subscribe"),
    PAY_DONATE("paydonate"),
    PAY_SPLIT("paysplit"),
    PAY_CASH("paycash"),
    INVOICE("invoice"),
    AUTH("auth"),
    REGULAR("regular"),
    HOLD_COMPLETE("hold_complete"),
    REVERSE("reverse"),
    REFUND("refund"),
    CHECK("check");

    private final String value;

    LiqPayAction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LiqPayAction fromValue(String value) {
        for (LiqPayAction action : values()) {
            if (action.value.equalsIgnoreCase(value)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Unknown LiqPay action: " + value);
    }
}
