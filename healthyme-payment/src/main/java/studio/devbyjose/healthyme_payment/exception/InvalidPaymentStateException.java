package studio.devbyjose.healthyme_payment.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaymentStateException extends PaymentException {

    public InvalidPaymentStateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public InvalidPaymentStateException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}