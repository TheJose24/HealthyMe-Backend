package dev.juliancamacho.healthyme_personal.mapper;

import dev.juliancamacho.healthyme_personal.dto.TecnicoDto;
import dev.juliancamacho.healthyme_personal.entity.HorarioTrabajo;
import dev.juliancamacho.healthyme_personal.entity.Tecnico;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TecnicoMapper {
    // Entity -> DTO
    TecnicoDto tecnicoToTecnicoDto(Tecnico tecnico);

    // DTO -> Entity
    Tecnico tecnicoDtoToTecnico(TecnicoDto tecnicoDTO);

    // List<Entity> -> List<DTO>
    List<TecnicoDto> tecnicosToTecnicoDtos(List<Tecnico> tecnicos);

    default List<Integer> mapHorarios(Set<HorarioTrabajo> horarios) {
        if (horarios == null) return null;
        return horarios.stream().map(HorarioTrabajo::getIdHorario).collect(Collectors.toList());
    }
}