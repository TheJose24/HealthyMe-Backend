package dev.Elmer.healthyme_consultas.exception;

import org.springframework.http.HttpStatus;

public class ConsultaConflictException extends ConsultasException {
    public ConsultaConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConsultaConflictException(Integer idCita) {
        super("Ya existe una consulta registrada con el ID de cita: " + idCita, HttpStatus.CONFLICT);
    }
}
