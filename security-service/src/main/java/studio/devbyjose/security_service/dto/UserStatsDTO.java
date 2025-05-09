package studio.devbyjose.security_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    private long totalUsuarios;
    private long usuariosActivos;
    private long usuariosSuspendidos;
    private long usuariosEliminados;
    private Map<String, Long> usuariosPorRol;
}