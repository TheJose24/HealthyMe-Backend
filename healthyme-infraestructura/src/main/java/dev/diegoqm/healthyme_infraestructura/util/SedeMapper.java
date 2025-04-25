package dev.diegoqm.healthyme_infraestructura.util;

import dev.diegoqm.healthyme_infraestructura.dto.SedeDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SedeMapper {

    SedeMapper mapper = Mappers.getMapper(SedeMapper.class);

    SedeDTO toDTO(Sede sede);
    Sede toEntity(SedeDTO dto);

}