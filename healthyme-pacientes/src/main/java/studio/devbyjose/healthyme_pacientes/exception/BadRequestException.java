package studio.devbyjose.healthyme_pacientes.exception;

import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.HttpStatusProvider;

public class BadRequestException extends RuntimeException implements HttpStatusProvider {

    private final HttpStatus httpStatus;

    public BadRequestException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}