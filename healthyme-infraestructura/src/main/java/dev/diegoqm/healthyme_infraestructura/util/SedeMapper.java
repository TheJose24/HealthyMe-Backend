package dev.diegoqm.healthyme_infraestructura.util;

import dev.diegoqm.healthyme_infraestructura.dto.SedeDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SedeMapper {

    SedeDTO toDTO(Sede sede);
    Sede toEntity(SedeDTO dto);

}