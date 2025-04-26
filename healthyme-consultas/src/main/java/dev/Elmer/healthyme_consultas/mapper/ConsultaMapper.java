package dev.Elmer.healthyme_consultas.mapper;

import dev.Elmer.healthyme_consultas.dto.ConsultaDto;
import dev.Elmer.healthyme_consultas.entity.Consulta;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsultaMapper {
    ConsultaDto toDto(Consulta consulta);

    Consulta toEntity(ConsultaDto dto);
}