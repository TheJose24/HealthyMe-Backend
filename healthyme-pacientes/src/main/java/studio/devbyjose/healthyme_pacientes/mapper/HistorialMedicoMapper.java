package studio.devbyjose.healthyme_pacientes.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_pacientes.dto.HistorialMedicoDTO;
import studio.devbyjose.healthyme_pacientes.entity.HistorialMedico;
import studio.devbyjose.healthyme_pacientes.entity.Paciente;
import studio.devbyjose.healthyme_pacientes.entity.Triaje;
import studio.devbyjose.healthyme_pacientes.exception.PacienteNotFoundException;
import studio.devbyjose.healthyme_pacientes.exception.TriajeNotFoundException;
import studio.devbyjose.healthyme_pacientes.repository.PacienteRepository;
import studio.devbyjose.healthyme_pacientes.repository.TriajeRepository;

@Mapper(componentModel = "spring")
public abstract class HistorialMedicoMapper {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private TriajeRepository triajeRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", source = "idPaciente", qualifiedByName = "idToPaciente")
    @Mapping(target = "triaje", source = "idTriaje", qualifiedByName = "idToTriaje")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "ultimaModificacion", ignore = true)
    public abstract HistorialMedico toEntity(HistorialMedicoDTO dto);

    @Mapping(target = "idPaciente", source = "paciente.id")
    @Mapping(target = "idTriaje", source = "triaje.id")
    public abstract HistorialMedicoDTO toDTO(HistorialMedico entity);

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

    @Named("idToTriaje")
    protected Triaje idToTriaje(Long idTriaje) {
        if (idTriaje == null) {
            return null;
        }
        return triajeRepository.findById(idTriaje)
                .orElseThrow(() -> new TriajeNotFoundException(
                        "No se encontró triaje con el id: " + idTriaje,
                        HttpStatus.NOT_FOUND));
    }
}