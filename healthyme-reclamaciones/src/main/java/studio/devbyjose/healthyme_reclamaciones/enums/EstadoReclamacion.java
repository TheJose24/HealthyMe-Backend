package studio.devbyjose.healthyme_reclamaciones.enums;

import lombok.Getter;

@Getter
public enum EstadoReclamacion {
    RECIBIDO("Recibido"),
    EN_PROCESO("En Proceso"), 
    PENDIENTE_INFORMACION("Pendiente de Información"),
    RESUELTO("Resuelto"),
    CERRADO("Cerrado"),
    ANULADO("Anulado");

    private final String descripcion;

    EstadoReclamacion(String descripcion) {
        this.descripcion = descripcion;
    }

}
