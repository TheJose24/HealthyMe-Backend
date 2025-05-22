package dev.juliancamacho.healthyme_personal.mapper;

import dev.juliancamacho.healthyme_personal.dto.MedicoDto;
import dev.juliancamacho.healthyme_personal.entity.HorarioTrabajo;
import dev.juliancamacho.healthyme_personal.entity.Medico;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MedicoMapper {

    // Entity -> DTO
    MedicoDto medicoToMedicoDto(Medico medico);

    // DTO -> Entity
    Medico medicoDtoToMedico(MedicoDto medicoDto);

    // List<Entity> -> List<DTO>
    List<MedicoDto> medicosToMedicoDtos(List<Medico> medicos);

    default List<Integer> mapHorarios(Set<HorarioTrabajo> horarios) {
        if (horarios == null) return null;
        return horarios.stream().map(HorarioTrabajo::getIdHorario).collect(Collectors.toList());
    }
}
