package ua.hudyma.Theater2025.constants.liqpay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LiqPaySystemPaymentType fromValue(String value) {
        for (LiqPaySystemPaymentType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown LiqPay paytype: " + value);
    }
}
