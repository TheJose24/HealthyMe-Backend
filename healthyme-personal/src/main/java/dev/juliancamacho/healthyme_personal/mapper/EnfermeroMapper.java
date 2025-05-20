package dev.juliancamacho.healthyme_personal.mapper;

import dev.juliancamacho.healthyme_personal.dto.EnfermeroDto;
import dev.juliancamacho.healthyme_personal.entity.Enfermero;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnfermeroMapper {
    // Entity -> DTO
    EnfermeroDto enfermeroToEnfermeroDto(Enfermero enfermero);

    // DTO -> Entity
    Enfermero enfermeroDtoToEnfermero(EnfermeroDto enfermeroDto);

    // List<Entity> -> List<DTO>
    List<EnfermeroDto> enfermerosToEnfermeroDtos(List<Enfermero> enfermeros);
}