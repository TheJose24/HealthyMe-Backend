package dev.diegoqm.healthyme_infraestructura.util;

import dev.diegoqm.healthyme_infraestructura.dto.LaboratorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Laboratorio;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LaboratorioMapper {
    LaboratorioMapper mapper = Mappers.getMapper(LaboratorioMapper.class);

    LaboratorioDTO toDTO(Laboratorio laboratorio);
    Laboratorio toEntity(LaboratorioDTO dto);

}
