package dev.diegoqm.healthyme_infraestructura.util;

import dev.diegoqm.healthyme_infraestructura.dto.ConsultorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Consultorio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsultorioMapper {

    ConsultorioDTO toDTO(Consultorio consultorio);
    Consultorio toEntity(ConsultorioDTO dto);

}