package dev.juliancamacho.healthyme_personal.dto;

import dev.juliancamacho.healthyme_personal.entity.HorarioTrabajo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import studio.devbyjose.healthyme_commons.client.dto.ContratoDTO;
import studio.devbyjose.healthyme_commons.client.dto.PersonaDTO;
import studio.devbyjose.healthyme_commons.enums.EstadoUsuario;

import java.util.List;

@Data
public class EnfermeroDto {

    private Integer idEnfermero;

    @NotNull(message = "El ID usuario no puede ser nulo")
    private Integer idUsuario;

    @NotNull(message = "El ID horario no puede ser nulo")
    private HorarioTrabajo horario;

    // Datos del usuario obtenidos via Feign
    private String nombreUsuario;
    private EstadoUsuario estado;
    private String imagenPerfil;
    private String rol;
    private PersonaDTO persona;
    private List<ContratoDTO> contratos;
}
