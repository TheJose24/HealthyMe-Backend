package studio.devbyjose.healthyme_pacientes.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_pacientes.dto.TriajeDTO;
import studio.devbyjose.healthyme_pacientes.entity.Paciente;
import studio.devbyjose.healthyme_pacientes.entity.Triaje;
import studio.devbyjose.healthyme_pacientes.exception.PacienteNotFoundException;
import studio.devbyjose.healthyme_pacientes.repository.PacienteRepository;

@Mapper(componentModel = "spring", uses = {PacienteMapper.class})
public abstract class TriajeMapper {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", source = "idPaciente", qualifiedByName = "idToPaciente")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "ultimaModificacion", ignore = true)
    public abstract Triaje toEntity(TriajeDTO dto);

    @Mapping(target = "idPaciente", source = "paciente.id")
    public abstract TriajeDTO toDTO(Triaje entity);

    @Named("idToPaciente")
    protected Paciente idToPaciente(Long idPaciente) {
        if (idPaciente == null) {
            return null;
        }
        return pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new PacienteNotFoundException(
                        "No se encontró paciente con el id: " + idPaciente,
                        HttpStatus.NOT_FOUND));
    }
}