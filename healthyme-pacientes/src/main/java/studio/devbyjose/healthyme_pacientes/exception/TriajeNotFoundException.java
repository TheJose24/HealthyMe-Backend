package studio.devbyjose.healthyme_pacientes.exception;

import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.HttpStatusProvider;

public class TriajeNotFoundException extends RuntimeException implements HttpStatusProvider {

  private final HttpStatus httpStatus;

  public TriajeNotFoundException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public TriajeNotFoundException(String message, Throwable cause, HttpStatus httpStatus) {
    super(message, cause);
    this.httpStatus = httpStatus;
  }

  public TriajeNotFoundException(Throwable cause, HttpStatus httpStatus) {
    super(cause);
    this.httpStatus = httpStatus;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return this.httpStatus;
  }
}
