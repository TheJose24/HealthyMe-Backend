package studio.devbyjose.healthyme_payment.exception;

import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.ResourceNotFoundException;

public class FacturaNotFoundException extends ResourceNotFoundException {

    public FacturaNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public FacturaNotFoundException(Integer id) {
        super("Factura no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
    }

    public FacturaNotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND);
    }
}