package dev.juliancamacho.healthyme_personal.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String resourceName, Object id) {
        super(resourceName + " con ID " + id + " no encontrado");
    }
}
