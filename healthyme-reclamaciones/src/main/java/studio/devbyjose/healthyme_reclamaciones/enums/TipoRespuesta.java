package studio.devbyjose.healthyme_reclamaciones.enums;

import lombok.Getter;

@Getter
public enum TipoRespuesta {
    INICIAL("Respuesta Inicial"),
    INTERMEDIA("Respuesta Intermedia"),
    SEGUIMIENTO("Seguimiento"),
    FINAL("Respuesta Final"),
    SOLICITUD_INFORMACION("Solicitud de Información"),
    INFORMACION_ADICIONAL("Información Adicional"),
    ACLARACION("Aclaración"),
    DISCULPAS("Disculpas"),
    DERIVACION("Derivación");

    private final String descripcion;

    TipoRespuesta(String descripcion) {
        this.descripcion = descripcion;
    }
}
