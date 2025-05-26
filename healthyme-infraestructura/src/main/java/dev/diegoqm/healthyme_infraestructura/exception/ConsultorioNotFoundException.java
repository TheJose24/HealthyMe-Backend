package dev.diegoqm.healthyme_infraestructura.exception;

import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.HttpStatusProvider;

public class ConsultorioNotFoundException extends RuntimeException implements HttpStatusProvider {
    private final HttpStatus httpStatus;

    public ConsultorioNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ConsultorioNotFoundException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public ConsultorioNotFoundException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
