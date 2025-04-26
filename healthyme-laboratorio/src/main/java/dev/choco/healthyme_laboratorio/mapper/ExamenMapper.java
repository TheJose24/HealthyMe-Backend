package dev.choco.healthyme_laboratorio.mapper;

import dev.choco.healthyme_laboratorio.dto.ExamenDTO;
import dev.choco.healthyme_laboratorio.entity.Examen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExamenMapper {
    @Mapping(target = "idReservaLab", source = "reservaLab.idReservaLab")
    ExamenDTO toDTO(Examen entity);

    @Mapping(target = "reservaLab", ignore = true)
    Examen toEntity(ExamenDTO dto);
}