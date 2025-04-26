package dev.diegoqm.healthyme_infraestructura.mapper;

import dev.diegoqm.healthyme_infraestructura.dto.LaboratorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Laboratorio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LaboratorioMapper {

    LaboratorioDTO toDTO(Laboratorio laboratorio);
    Laboratorio toEntity(LaboratorioDTO dto);

}
