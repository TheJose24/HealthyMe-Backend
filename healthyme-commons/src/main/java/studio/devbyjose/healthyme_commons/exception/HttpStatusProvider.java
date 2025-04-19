package studio.devbyjose.healthyme_commons.exception;

import org.springframework.http.HttpStatus;

public interface HttpStatusProvider {
    HttpStatus getHttpStatus();
}
