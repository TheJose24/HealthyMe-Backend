package dev.Elmer.healthyme_consultas.mapper;

import dev.Elmer.healthyme_consultas.dto.MedicamentoDto;
import dev.Elmer.healthyme_consultas.entity.Medicamento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicamentoMapper {
    MedicamentoDto toDto(Medicamento entity);
    Medicamento toEntity(MedicamentoDto dto);
}