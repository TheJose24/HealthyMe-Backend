package studio.devbyjose.healthyme_notification.mapper;

import org.mapstruct.Mapper;
import studio.devbyjose.healthyme_notification.dto.PlantillaDTO;
import studio.devbyjose.healthyme_notification.entity.Plantilla;

@Mapper(componentModel = "spring")
public interface PlantillaMapper {
    PlantillaDTO toDTO(Plantilla plantilla);
    Plantilla toEntity(PlantillaDTO plantillaDTO);
}