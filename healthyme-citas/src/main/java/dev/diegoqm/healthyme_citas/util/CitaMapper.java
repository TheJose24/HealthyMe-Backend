package dev.diegoqm.healthyme_citas.util;


import dev.diegoqm.healthyme_citas.dto.CitaDTO;
import dev.diegoqm.healthyme_citas.entity.Cita;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CitaMapper {
    CitaMapper mapper = Mappers.getMapper(CitaMapper.class);

    CitaDTO toDTO(Cita cita);
    Cita toEntity(CitaDTO dto);

}
