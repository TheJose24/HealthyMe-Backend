package studio.devbyjose.healthyme_notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import studio.devbyjose.healthyme_notification.dto.NotificacionDTO;
import studio.devbyjose.healthyme_notification.entity.Notificacion;

@Mapper(componentModel = "spring", uses = {PlantillaMapper.class})
public interface NotificacionMapper {

    @Mapping(source = "plantilla.idPlantilla", target = "idPlantilla")
    @Mapping(target = "adjuntos", ignore = true)
    NotificacionDTO toDTO(Notificacion notificacion);

    @Mapping(source = "idPlantilla", target = "plantilla.idPlantilla")
    Notificacion toEntity(NotificacionDTO notificacionDTO);

}