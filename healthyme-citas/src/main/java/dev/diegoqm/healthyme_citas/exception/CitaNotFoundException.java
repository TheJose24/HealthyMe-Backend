package dev.diegoqm.healthyme_citas.exception;

import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.HttpStatusProvider;

public class CitaNotFoundException extends RuntimeException implements HttpStatusProvider{
  private final HttpStatus httpStatus;

  public CitaNotFoundException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public CitaNotFoundException(String message, Throwable cause, HttpStatus httpStatus) {
    super(message, cause);
    this.httpStatus = httpStatus;
  }

  public CitaNotFoundException(Throwable cause, HttpStatus httpStatus) {
    super(cause);
    this.httpStatus = httpStatus;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return this.httpStatus;
  }
}
