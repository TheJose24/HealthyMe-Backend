package studio.devbyjose.healthyme_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_notification.enums.EntidadOrigen;
import studio.devbyjose.healthyme_notification.enums.EstadoNotificacion;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionDTO {
    private Integer idNotificacion;
    private String destinatario;
    private LocalDateTime fechaEnvio;
    private EstadoNotificacion estado;
    private Integer idPlantilla;
    private String datosContexto;
    private EntidadOrigen entidadOrigen;
    private Integer idOrigen;
    private List<AdjuntoDTO> adjuntos;
}