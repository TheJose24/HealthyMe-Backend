package dev.diegoqm.healthyme_infraestructura.mapper;

import dev.diegoqm.healthyme_infraestructura.dto.ConsultorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Consultorio;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ConsultorioMapper {
    @Mapping(source = "sede.id", target = "idSede")
    @Mapping(source = "sede.nombre", target = "nombreSede")
    ConsultorioDTO toDTO(Consultorio consultorio);
    @Mapping(target = "sede", ignore = true)
    @Mapping(target = "sede.nombre", ignore = true)
    Consultorio toEntity(ConsultorioDTO dto);
}