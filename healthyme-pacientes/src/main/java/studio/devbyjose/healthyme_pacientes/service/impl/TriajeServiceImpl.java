package studio.devbyjose.healthyme_pacientes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studio.devbyjose.healthyme_pacientes.dto.TriajeDTO;
import studio.devbyjose.healthyme_pacientes.entity.Paciente;
import studio.devbyjose.healthyme_pacientes.entity.Triaje;
import studio.devbyjose.healthyme_pacientes.exception.PacienteNotFoundException;
import studio.devbyjose.healthyme_pacientes.exception.TriajeNotFoundException;
import studio.devbyjose.healthyme_pacientes.mapper.TriajeMapper;
import studio.devbyjose.healthyme_pacientes.repository.PacienteRepository;
import studio.devbyjose.healthyme_pacientes.repository.TriajeRepository;
import studio.devbyjose.healthyme_pacientes.service.interfaces.TriajeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TriajeServiceImpl implements TriajeService {

    private final TriajeRepository triajeRepository;
    private final PacienteRepository pacienteRepository;
    private final TriajeMapper triajeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TriajeDTO> findAll() {
        log.info("Obteniendo todos los triajes");
        return triajeRepository.findAll().stream()
                .map(triajeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TriajeDTO findById(Long id) {
        log.info("Buscando triaje con id: {}", id);
        return triajeRepository.findById(id)
                .map(triajeMapper::toDTO)
                .orElseThrow(() -> new TriajeNotFoundException("No se encontró triaje con el id: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TriajeDTO> findByPaciente(Long idPaciente) {
        log.info("Buscando triajes para el paciente con id: {}", idPaciente);
        // El mapper ahora validará la existencia del paciente
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new PacienteNotFoundException("No se encontró paciente con el id: " + idPaciente, HttpStatus.NOT_FOUND));

        return triajeRepository.findByPacienteOrderByFechaDescHoraDesc(paciente).stream()
                .map(triajeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TriajeDTO create(TriajeDTO triajeDTO) {
        log.info("Creando nuevo triaje para el paciente con id: {}", triajeDTO.getIdPaciente());

        // El mapper se encargará de encontrar el paciente por su ID
        Triaje triaje = triajeMapper.toEntity(triajeDTO);
        Triaje savedTriaje = triajeRepository.save(triaje);
        log.info("Triaje creado con id: {}", savedTriaje.getId());
        return triajeMapper.toDTO(savedTriaje);
    }

    @Override
    @Transactional
    public TriajeDTO update(Long id, TriajeDTO triajeDTO) {
        log.info("Actualizando triaje con id: {}", id);

        if (!triajeRepository.existsById(id)) {
            throw new TriajeNotFoundException("No se encontró triaje con el id: " + id, HttpStatus.NOT_FOUND);
        }

        // El mapper se encargará de validar las relaciones
        Triaje triaje = triajeMapper.toEntity(triajeDTO);
        triaje.setId(id);

        Triaje updatedTriaje = triajeRepository.save(triaje);
        log.info("Triaje actualizado con éxito");
        return triajeMapper.toDTO(updatedTriaje);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando triaje con id: {}", id);

        if (!triajeRepository.existsById(id)) {
            throw new TriajeNotFoundException("No se encontró triaje con el id: " + id, HttpStatus.NOT_FOUND);
        }

        triajeRepository.deleteById(id);
        log.info("Triaje eliminado con éxito");
    }
}