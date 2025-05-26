package dev.diegoqm.healthyme_infraestructura.mapper;

import dev.diegoqm.healthyme_infraestructura.dto.ConsultorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Consultorio;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ConsultorioMapper {
    @Mapping(source = "sede.id", target = "idSede")
    ConsultorioDTO toDTO(Consultorio consultorio);
    @Mapping(target = "sede", ignore = true)
    Consultorio toEntity(ConsultorioDTO dto);
}