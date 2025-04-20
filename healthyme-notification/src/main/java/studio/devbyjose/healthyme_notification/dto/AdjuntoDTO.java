package studio.devbyjose.healthyme_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdjuntoDTO {
    private Integer idAdjunto;
    private String nombre;
    private String tipoContenido;
    private String urlAlmacenamiento;
    private Integer idNotificacion;
    private byte[] contenido; // Para pasar el contenido del archivo directamente
    private String storageFilename;
}