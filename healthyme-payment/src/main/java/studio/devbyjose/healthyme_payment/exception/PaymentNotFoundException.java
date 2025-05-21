package studio.devbyjose.healthyme_payment.exception;

import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.ResourceNotFoundException;

public class PaymentNotFoundException extends ResourceNotFoundException {

    public PaymentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public PaymentNotFoundException(Integer id) {
        super("Pago no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
    }
}