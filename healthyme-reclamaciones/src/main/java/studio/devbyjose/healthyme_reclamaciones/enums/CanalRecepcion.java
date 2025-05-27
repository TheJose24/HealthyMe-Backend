package studio.devbyjose.healthyme_reclamaciones.enums;

import lombok.Getter;

@Getter
public enum CanalRecepcion {
    PRESENCIAL("Presencial"),
    WEB("Página Web"),
    TELEFONO("Teléfono"),
    EMAIL("Correo Electrónico");

    private final String descripcion;

    CanalRecepcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
