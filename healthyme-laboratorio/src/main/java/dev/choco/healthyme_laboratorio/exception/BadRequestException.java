package dev.choco.healthyme_laboratorio.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String mensaje) {
        super(mensaje);
    }
}
