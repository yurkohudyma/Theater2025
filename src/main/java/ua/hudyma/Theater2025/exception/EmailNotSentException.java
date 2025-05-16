package ua.hudyma.Theater2025.exception;

public class EmailNotSentException extends IllegalStateException {
    public EmailNotSentException(String s) {
        super(s);
    }
}
