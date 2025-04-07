package ua.hudyma.Theater2025.constants;

public enum Genre {

    ACTION ("Бойовик"),
    DRAMA ("Драма"),
    COMEDY ("Комедія"),
    HORROR ("Жахи"),
    ANIME ("Анімаційне кіно"),
    EROTIC ("Еротика"),
    HISTORIC ("Історичне"),
    BIOPIC ("Біографічне");

    public final String str;


    Genre(String str) {
        this.str = str;
    }
}
