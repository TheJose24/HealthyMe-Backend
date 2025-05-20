package studio.devbyjose.healthyme_commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_commons.enums.EstadoUsuario;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Integer idUsuario;
    private String nombreUsuario;
    private EstadoUsuario estado;
    private String imagenPerfil;
    private String rol;
    private PersonaDTO persona;
    private List<ContratoDTO> contratos;
}