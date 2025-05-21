package studio.devbyjose.healthyme_payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.HttpStatusProvider;

@Getter
public class PaymentException extends RuntimeException implements HttpStatusProvider {

    private final HttpStatus httpStatus;

    public PaymentException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public PaymentException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}