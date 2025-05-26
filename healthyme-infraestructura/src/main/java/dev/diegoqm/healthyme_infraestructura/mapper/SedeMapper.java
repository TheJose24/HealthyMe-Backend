package dev.diegoqm.healthyme_infraestructura.mapper;

import dev.diegoqm.healthyme_infraestructura.dto.SedeDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SedeMapper {
    @Mapping(source = "horarioTrabajo.id", target = "idHorario")
    SedeDTO toDTO(Sede sede);

    @Mapping(target = "horarioTrabajo", ignore = true)
    Sede toEntity(SedeDTO dto);
}