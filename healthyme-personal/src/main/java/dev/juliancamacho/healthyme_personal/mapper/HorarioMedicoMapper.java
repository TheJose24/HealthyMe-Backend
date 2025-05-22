package dev.juliancamacho.healthyme_personal.mapper;

import dev.juliancamacho.healthyme_personal.dto.HorarioMedicoDto;
import dev.juliancamacho.healthyme_personal.entity.HorarioMedico;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HorarioMedicoMapper {
    // Entity -> DTO
    HorarioMedicoDto horarioMedicoToHorarioMedicoDto(HorarioMedico horarioMedico);

    // DTO -> Entity
    HorarioMedico horarioMedicoDtoToHorarioMedico(HorarioMedicoDto horarioMedicoDto);

    // List<Entity> -> List<DTO>
    List<HorarioMedicoDto> horariosMedicosToHorarioMedicoDtos(List<HorarioMedico> horariosMedicos);
}