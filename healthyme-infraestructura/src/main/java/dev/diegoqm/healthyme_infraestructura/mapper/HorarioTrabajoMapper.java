package dev.diegoqm.healthyme_infraestructura.mapper;

import dev.diegoqm.healthyme_infraestructura.dto.HorarioTrabajoDTO;
import dev.diegoqm.healthyme_infraestructura.entity.HorarioTrabajo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HorarioTrabajoMapper {

    HorarioTrabajoDTO toDTO(HorarioTrabajo horarioTrabajo);
    HorarioTrabajo toEntity(HorarioTrabajoDTO horarioTrabajoDTO);

    
}

