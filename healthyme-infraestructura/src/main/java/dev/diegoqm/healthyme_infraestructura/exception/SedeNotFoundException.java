package dev.diegoqm.healthyme_infraestructura.exception;

import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_commons.exception.HttpStatusProvider;

public class SedeNotFoundException extends RuntimeException implements HttpStatusProvider{
   private final HttpStatus httpStatus;

   public SedeNotFoundException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
   }

   public SedeNotFoundException(String message, Throwable cause, HttpStatus httpStatus) {
    super(message, cause);
    this.httpStatus = httpStatus;
   }

   public SedeNotFoundException(Throwable cause, HttpStatus httpStatus) {
    super(cause);
    this.httpStatus = httpStatus;
   }

  @Override
  public HttpStatus getHttpStatus() {
    return this.httpStatus;
  }
}
