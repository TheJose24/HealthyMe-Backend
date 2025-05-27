package studio.devbyjose.healthyme_reclamaciones.enums;

import lombok.Getter;

@Getter
public enum TipoMotivo {
    QUEJA("Queja", "Expresión de insatisfacción sobre el servicio"),
    RECLAMO("Reclamo", "Solicitud de solución a un problema específico"),
    SUGERENCIA("Sugerencia", "Propuesta de mejora para el servicio"),
    FELICITACION("Felicitación", "Reconocimiento positivo del servicio");

    private final String nombre;
    private final String descripcion;

    TipoMotivo(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

}
