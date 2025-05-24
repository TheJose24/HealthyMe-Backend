package dev.Elmer.healthyme_consultas.exception;

import org.springframework.http.HttpStatus;

public class InvalidDataException extends ConsultasException {
    public InvalidDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidDataException() {
        super("Los datos proporcionados no son válidos.", HttpStatus.BAD_REQUEST);
    }

}
