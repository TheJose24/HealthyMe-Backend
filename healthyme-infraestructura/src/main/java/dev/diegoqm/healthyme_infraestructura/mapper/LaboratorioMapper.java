package dev.diegoqm.healthyme_infraestructura.mapper;

import dev.diegoqm.healthyme_infraestructura.dto.LaboratorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Laboratorio;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LaboratorioMapper {
    @Mapping(source = "sede.id", target = "idSede")
    LaboratorioDTO toDTO(Laboratorio laboratorio);
    @Mapping(target = "sede", ignore = true)
    Laboratorio toEntity(LaboratorioDTO dto);
}
