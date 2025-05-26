package dev.Elmer.healthyme_consultas.mapper;

import dev.Elmer.healthyme_consultas.dto.RecetaDto;
import dev.Elmer.healthyme_consultas.entity.Receta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { MedicamentoMapper.class })
public interface RecetaMapper {

    @Mapping(source = "consulta.idConsulta", target = "idConsulta")
    RecetaDto toDto(Receta receta);

    @Mapping(target = "consulta", ignore = true)
    Receta toEntity(RecetaDto dto);
}