package studio.devbyjose.security_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.security_service.enums.EstadoUsuario;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer idUsuario;
    private String nombreUsuario;
    private EstadoUsuario estado;
    private String imagenPerfil;
    private String rol;
    private PersonaDTO persona;
    private List<ContratoDTO> contratos;
}