package ua.hudyma.Theater2025.constants;

public enum TicketStatus {

    PAID ("Сплачено"),
    DECLINED ("Відхилено"),
    CANCELLED ("Скасовано"),
    UTILISED ("Використано"),
    PENDING ("Неоплачено");

    public final String str;

    TicketStatus(String str) {
        this.str = str;
    }
}
