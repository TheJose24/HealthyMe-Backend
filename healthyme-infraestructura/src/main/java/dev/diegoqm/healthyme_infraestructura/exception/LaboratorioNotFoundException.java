package dev.diegoqm.healthyme_infraestructura.exception;

import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.HttpStatusProvider;

public class LaboratorioNotFoundException extends RuntimeException implements HttpStatusProvider{
    private final HttpStatus httpStatus;

    public LaboratorioNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public LaboratorioNotFoundException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public LaboratorioNotFoundException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
