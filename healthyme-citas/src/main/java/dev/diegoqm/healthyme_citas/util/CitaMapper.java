package dev.diegoqm.healthyme_citas.util;

import dev.diegoqm.healthyme_citas.dto.CitaDTO;
import dev.diegoqm.healthyme_citas.entity.Cita;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    CitaDTO toDTO(Cita cita);
    Cita toEntity(CitaDTO dto);

}
