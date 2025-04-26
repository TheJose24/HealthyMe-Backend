package dev.juliancamacho.healthyme_personal.mapper;

import dev.juliancamacho.healthyme_personal.dto.UnidadDto;
import dev.juliancamacho.healthyme_personal.entity.Unidad;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnidadMapper {

    // Mapeo de Entidad -> DTO
    UnidadDto unidadToUnidadDto(Unidad unidad);

    // Mapeo de DTO -> Entidad
    Unidad unidadDtoToUnidad(UnidadDto unidadDto);
}
