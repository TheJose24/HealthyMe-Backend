package dev.Elmer.healthyme_consultas.exception;

import org.springframework.http.HttpStatus;

public class ConsultaNotFoundException extends ConsultasException {
    public ConsultaNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ConsultaNotFoundException(Integer id) {
        super("Consulta no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
    }
}


