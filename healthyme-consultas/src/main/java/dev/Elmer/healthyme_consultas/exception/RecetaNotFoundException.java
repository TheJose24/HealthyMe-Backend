package dev.Elmer.healthyme_consultas.exception;

import org.springframework.http.HttpStatus;

public class RecetaNotFoundException extends ConsultasException {
    public RecetaNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public RecetaNotFoundException(Integer id) {
        super("Receta no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
    }
}