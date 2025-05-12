package ua.hudyma.Theater2025.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RefundFailureException.class)
    public ResponseEntity<String> handleRefundFailure(RefundFailureException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body("Refund request has been rejected");
    }
}

