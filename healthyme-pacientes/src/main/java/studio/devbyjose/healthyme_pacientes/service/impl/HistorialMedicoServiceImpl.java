package studio.devbyjose.healthyme_pacientes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studio.devbyjose.healthyme_pacientes.dto.HistorialMedicoDTO;
import studio.devbyjose.healthyme_pacientes.entity.HistorialMedico;
import studio.devbyjose.healthyme_pacientes.entity.Paciente;
import studio.devbyjose.healthyme_pacientes.exception.HistorialMedicoNotFoundException;
import studio.devbyjose.healthyme_pacientes.exception.PacienteNotFoundException;
import studio.devbyjose.healthyme_pacientes.mapper.HistorialMedicoMapper;
import studio.devbyjose.healthyme_pacientes.repository.HistorialMedicoRepository;
import studio.devbyjose.healthyme_pacientes.repository.PacienteRepository;
import studio.devbyjose.healthyme_pacientes.service.interfaces.HistorialMedicoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistorialMedicoServiceImpl implements HistorialMedicoService {

    private final HistorialMedicoRepository historialRepository;
    private final PacienteRepository pacienteRepository;
    private final HistorialMedicoMapper historialMedicoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<HistorialMedicoDTO> findAll() {
        log.info("Obteniendo todos los historiales médicos");
        return historialRepository.findAll().stream()
                .map(historialMedicoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialMedicoDTO findById(Long id) {
        log.info("Buscando historial médico con id: {}", id);
        return historialRepository.findById(id)
                .map(historialMedicoMapper::toDTO)
                .orElseThrow(() -> new HistorialMedicoNotFoundException("No se encontró historial médico con el id: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialMedicoDTO> findByPaciente(Long idPaciente) {
        log.info("Buscando historiales médicos para el paciente con id: {}", idPaciente);
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new PacienteNotFoundException("No se encontró paciente con el id: " + idPaciente, HttpStatus.NOT_FOUND));

        return historialRepository.findByPaciente(paciente).stream()
                .map(historialMedicoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HistorialMedicoDTO create(HistorialMedicoDTO historialDTO) {
        log.info("Creando nuevo historial médico para paciente con id: {}", historialDTO.getIdPaciente());

        // El mapper se encarga de validar y encontrar las entidades relacionadas
        HistorialMedico historial = historialMedicoMapper.toEntity(historialDTO);
        HistorialMedico savedHistorial = historialRepository.save(historial);

        log.info("Historial médico creado con id: {}", savedHistorial.getId());
        return historialMedicoMapper.toDTO(savedHistorial);
    }

    @Override
    @Transactional
    public HistorialMedicoDTO update(Long id, HistorialMedicoDTO historialDTO) {
        log.info("Actualizando historial médico con id: {}", id);

        if (!historialRepository.existsById(id)) {
            throw new HistorialMedicoNotFoundException("No se encontró historial médico con el id: " + id, HttpStatus.NOT_FOUND);
        }

        // El mapper convierte y valida las relaciones
        HistorialMedico historial = historialMedicoMapper.toEntity(historialDTO);
        historial.setId(id);

        HistorialMedico updatedHistorial = historialRepository.save(historial);
        log.info("Historial médico actualizado con éxito");
        return historialMedicoMapper.toDTO(updatedHistorial);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando historial médico con id: {}", id);

        if (!historialRepository.existsById(id)) {
            throw new HistorialMedicoNotFoundException("No se encontró historial médico con el id: " + id, HttpStatus.NOT_FOUND);
        }

        historialRepository.deleteById(id);
        log.info("Historial médico eliminado con éxito");
    }
}