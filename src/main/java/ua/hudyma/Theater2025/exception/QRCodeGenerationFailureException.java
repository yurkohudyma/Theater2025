package ua.hudyma.Theater2025.exception;

public class QRCodeGenerationFailureException extends RuntimeException {
    public QRCodeGenerationFailureException(String s) {
        super(s);
    }
}
