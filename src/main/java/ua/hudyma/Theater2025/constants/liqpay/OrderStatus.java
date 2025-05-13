package ua.hudyma.Theater2025.constants.liqpay;

public enum OrderStatus {
    PAID ("Сплачено"),
    PENDING ("Неоплачено");

    public final String str;

    OrderStatus(String str) {
        this.str = str;
    }
}