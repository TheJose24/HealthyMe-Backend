package studio.devbyjose.healthyme_payment.exception;

import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.ResourceNotFoundException;

public class MetodoPagoNotFoundException extends ResourceNotFoundException {

    public MetodoPagoNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public MetodoPagoNotFoundException(Integer id) {
        super("Método de pago no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
    }
}