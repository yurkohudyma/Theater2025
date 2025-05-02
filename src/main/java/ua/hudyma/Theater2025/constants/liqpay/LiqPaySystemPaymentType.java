package ua.hudyma.Theater2025.constants.liqpay;

public enum LiqPaySystemPaymentType {
    PRIVAT24("privat24"),
    LIQPAY("liqpay"),
    CARD("card"),
    CASH("cash"),
    WEBMONEY("webmoney"),
    VISA("visa"),
    MASTERCARD("mastercard"),
    PAYPAL("paypal"),
    APPLEPAY("applepay"),
    GOOGLEPAY("googlepay");

    private final String value;

    LiqPaySystemPaymentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LiqPaySystemPaymentType fromValue(String value) {
        for (LiqPaySystemPaymentType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown LiqPay paytype: " + value);
    }
}
