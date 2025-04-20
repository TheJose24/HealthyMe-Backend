package studio.devbyjose.healthyme_notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import studio.devbyjose.healthyme_notification.dto.AdjuntoDTO;
import studio.devbyjose.healthyme_notification.entity.Adjunto;

@Mapper(componentModel = "spring", uses = {NotificacionMapper.class})
public interface AdjuntoMapper {

    @Mapping(source = "notificacion.idNotificacion", target = "idNotificacion")
    @Mapping(target = "contenido", ignore = true)
    @Mapping(target = "storageFilename", ignore = true)
    AdjuntoDTO toDTO(Adjunto adjunto);

    @Mapping(source = "idNotificacion", target = "notificacion.idNotificacion")
    Adjunto toEntity(AdjuntoDTO adjuntoDTO);
}