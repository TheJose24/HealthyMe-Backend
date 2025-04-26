package dev.juliancamacho.healthyme_personal.mapper;

import dev.juliancamacho.healthyme_personal.dto.HorarioTrabajoDto;
import dev.juliancamacho.healthyme_personal.entity.HorarioTrabajo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HorarioTrabajoMapper {

    // Mapeo de Entidad -> DTO
    HorarioTrabajoDto horarioTrabajoToHorarioTrabajoDto(HorarioTrabajo horarioTrabajo);

    // Mapeo de DTO -> Entidad
    HorarioTrabajo horarioTrabajoDtoToHorarioTrabajo(HorarioTrabajoDto horarioTrabajoDTO);
}
