package studio.devbyjose.healthyme_commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicoDTO {
    private Integer idMedico;
    private String nombre;
    private String apellido;
    private String especialidad;
    private String email;
}