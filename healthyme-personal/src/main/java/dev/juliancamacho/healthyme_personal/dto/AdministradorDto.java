package dev.juliancamacho.healthyme_personal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import studio.devbyjose.healthyme_commons.client.dto.ContratoDTO;
import studio.devbyjose.healthyme_commons.client.dto.PersonaDTO;
import studio.devbyjose.healthyme_commons.enums.EstadoUsuario;

import java.util.List;

@Data
public class AdministradorDto {

    private Integer idAdministrador;

    private String cargo;

    @NotNull(message = "El ID usuario no puede ser nulo")
    @NotNull(message = "El ID usuario debe ser unico")
    private Integer idUsuario;

    // Datos del usuario obtenidos via Feign
    private String nombreUsuario;
    private EstadoUsuario estado;
    private String imagenPerfil;
    private String rol;
    private PersonaDTO persona;
    private List<ContratoDTO> contratos;
}
