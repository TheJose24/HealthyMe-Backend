package studio.devbyjose.healthyme_commons.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EspecialidadContadaDTO {
    private String especialidad;
    private Long cantidad;

    public EspecialidadContadaDTO(String especialidad, Long cantidad) {
        this.setEspecialidad(especialidad);
        this.setCantidad(cantidad);
    }
}
