package dev.diegoqm.healthyme_infraestructura.util;

import dev.diegoqm.healthyme_infraestructura.dto.ConsultorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Consultorio;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ConsultorioMapper {

    ConsultorioMapper mapper = Mappers.getMapper(ConsultorioMapper.class);

    ConsultorioDTO toDTO(Consultorio consultorio);
    Consultorio toEntity(ConsultorioDTO dto);

}