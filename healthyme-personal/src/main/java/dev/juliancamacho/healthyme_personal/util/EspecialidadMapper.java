package dev.juliancamacho.healthyme_personal.util;

import dev.juliancamacho.healthyme_personal.dto.EspecialidadDto;
import dev.juliancamacho.healthyme_personal.entity.Especialidad;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EspecialidadMapper {

    // Mapeo de Entidad -> DTO
    EspecialidadDto especialidadToEspecialidadDto(Especialidad especialidad);

    // Mapeo de DTO -> Entidad
    Especialidad especialidadDtoToEspecialidad(EspecialidadDto especialidadDTO);
}
