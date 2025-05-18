package studio.devbyjose.healthyme_pacientes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import studio.devbyjose.healthyme_pacientes.dto.SeguroDTO;
import studio.devbyjose.healthyme_pacientes.entity.Seguro;

@Mapper(componentModel = "spring")
public interface SeguroMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "ultimaModificacion", ignore = true)
    Seguro toEntity(SeguroDTO dto);

    SeguroDTO toDTO(Seguro entity);
}