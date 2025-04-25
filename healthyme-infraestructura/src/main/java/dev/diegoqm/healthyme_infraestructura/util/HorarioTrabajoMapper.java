package dev.diegoqm.healthyme_infraestructura.util;

import dev.diegoqm.healthyme_infraestructura.dto.HorarioTrabajoDTO;
import dev.diegoqm.healthyme_infraestructura.entity.HorarioTrabajo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HorarioTrabajoMapper {

    HorarioTrabajoMapper mapper = Mappers.getMapper(HorarioTrabajoMapper.class);

    HorarioTrabajoDTO toDTO(HorarioTrabajo horarioTrabajo);
    HorarioTrabajo toEntity(HorarioTrabajoDTO horarioTrabajoDTO);

    
}

