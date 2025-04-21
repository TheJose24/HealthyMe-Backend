package dev.juliancamacho.healthyme_personal.util;

import dev.juliancamacho.healthyme_personal.dto.UnidadDto;
import dev.juliancamacho.healthyme_personal.entity.Unidad;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UnidadMapper {

    // Mapeo de Entidad -> DTO
    UnidadDto unidadToUnidadDto(Unidad unidad);

    // Mapeo de DTO -> Entidad
    Unidad unidadDtoToUnidad(UnidadDto unidadDto);
}
