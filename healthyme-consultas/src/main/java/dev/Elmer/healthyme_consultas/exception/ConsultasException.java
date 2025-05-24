package dev.Elmer.healthyme_consultas.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.HttpStatusProvider;

public class ConsultasException extends RuntimeException implements HttpStatusProvider {

    private final HttpStatus httpStatus;

    public ConsultasException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ConsultasException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
