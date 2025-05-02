package ua.hudyma.Theater2025.constants.liqpay;

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

    public String getValue() {
        return value;
    }

    public static LiqPayAction fromValue(String value) {
        for (LiqPayAction action : values()) {
            if (action.value.equalsIgnoreCase(value)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Unknown LiqPay action: " + value);
    }
}
