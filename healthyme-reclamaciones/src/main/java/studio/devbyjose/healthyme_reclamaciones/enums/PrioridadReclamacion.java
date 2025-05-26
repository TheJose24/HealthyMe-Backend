package studio.devbyjose.healthyme_reclamaciones.enums;

import lombok.Getter;

@Getter
public enum PrioridadReclamacion {
    BAJA("Baja", 30),
    MEDIA("Media", 15),
    ALTA("Alta", 7),
    CRITICA("Crítica", 3);

    private final String descripcion;
    private final int diasRespuesta;

    PrioridadReclamacion(String descripcion, int diasRespuesta) {
        this.descripcion = descripcion;
        this.diasRespuesta = diasRespuesta;
    }

}
