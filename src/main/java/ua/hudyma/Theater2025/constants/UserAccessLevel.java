package ua.hudyma.Theater2025.constants;

public enum UserAccessLevel {
    USER ("Користувач"),
    ADMIN ("Адмін"),
    MANAGER ("Менеджер"),
    BLOCKED ("Заблоковано");

    public final String str;

    UserAccessLevel(String str) {
        this.str = str;
    }
}
