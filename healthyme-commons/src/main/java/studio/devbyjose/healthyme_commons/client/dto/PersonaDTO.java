package studio.devbyjose.healthyme_commons.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDTO {
    private String dni;
    private String nombre;
    private String apellido;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    private String sexo;
    private String direccion;
    private String telefono;
    private String email;
    private Integer edad;
}