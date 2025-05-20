package dev.juliancamacho.healthyme_personal.mapper;

import dev.juliancamacho.healthyme_personal.dto.HorarioTecnicoDto;
import dev.juliancamacho.healthyme_personal.entity.HorarioTecnico;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HorarioTecnicoMapper {
    // Entity -> DTO
    HorarioTecnicoDto horarioTecnicoToHorarioTecnicoDto(HorarioTecnico horarioTecnico);

    // DTO -> Entity
    HorarioTecnico horarioTecnicoDtoToHorarioTecnico(HorarioTecnicoDto horarioTecnicoDto);

    // List<Entity> -> List<DTO>
    List<HorarioTecnicoDto> horariosTecnicosToHorarioTecnicoDtos(List<HorarioTecnico> horariosTecnicos);
}
