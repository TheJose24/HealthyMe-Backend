package dev.juliancamacho.healthyme_personal.util;

import dev.juliancamacho.healthyme_personal.dto.HorarioTrabajoDto;
import dev.juliancamacho.healthyme_personal.entity.HorarioTrabajo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HorarioTrabajoMapper {

    // Mapeo de Entidad -> DTO
    HorarioTrabajoDto horarioTrabajoToHorarioTrabajoDto(HorarioTrabajo horarioTrabajo);

    // Mapeo de DTO -> Entidad
    HorarioTrabajo horarioTrabajoDtoToHorarioTrabajo(HorarioTrabajoDto horarioTrabajoDTO);
}
