package ua.hudyma.Theater2025.exception;

public class UserEmailExistsException extends IllegalArgumentException {
    public UserEmailExistsException(String s) {
        super(s);
    }
}
