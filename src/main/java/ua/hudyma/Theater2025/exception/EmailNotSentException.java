package ua.hudyma.Theater2025.exception;

import jakarta.mail.MessagingException;

public class EmailNotSentException extends IllegalStateException {
    public EmailNotSentException(String s) {
        super(s);
    }
}
