package dev.choco.healthyme_laboratorio.mapper;

import dev.choco.healthyme_laboratorio.dto.ReservaLabDTO;
import dev.choco.healthyme_laboratorio.entity.ReservaLab;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservaLabMapper {
    ReservaLabDTO toDTO(ReservaLab entity);
    ReservaLab toEntity(ReservaLabDTO dto);
}
