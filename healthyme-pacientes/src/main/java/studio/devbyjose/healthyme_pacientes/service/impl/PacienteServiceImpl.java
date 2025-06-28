package studio.devbyjose.healthyme_pacientes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studio.devbyjose.healthyme_pacientes.dto.PacienteDTO;
import studio.devbyjose.healthyme_pacientes.dto.PacientesPorMesDTO;
import studio.devbyjose.healthyme_pacientes.entity.Paciente;
import studio.devbyjose.healthyme_pacientes.exception.BadRequestException;
import studio.devbyjose.healthyme_pacientes.exception.PacienteNotFoundException;
import studio.devbyjose.healthyme_pacientes.mapper.PacienteMapper;
import studio.devbyjose.healthyme_pacientes.repository.PacienteRepository;
import studio.devbyjose.healthyme_pacientes.service.interfaces.PacienteService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PacienteServiceImpl implements PacienteService {
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PacienteDTO> findAll() {
        log.info("Obteniendo todos los pacientes");
        return pacienteRepository.findAll().stream()
                .map(pacienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteDTO findById(Long id) {
        log.info("Buscando paciente con id: {}", id);
        return pacienteRepository.findById(id)
                .map(pacienteMapper::toDTO)
                .orElseThrow(() -> new PacienteNotFoundException("No se encontró paciente con el id: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteDTO findByIdUsuario(Long idUsuario) {
        log.info("Buscando paciente con idUsuario: {}", idUsuario);
        return pacienteRepository.findByIdUsuario(idUsuario)
                .map(pacienteMapper::toDTO)
                .orElseThrow(() -> new PacienteNotFoundException("No se encontró paciente con el idUsuario: " + idUsuario, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public PacienteDTO create(PacienteDTO pacienteDTO) {
        log.info("Creando nuevo paciente con idUsuario: {}", pacienteDTO.getIdUsuario());
        if (pacienteRepository.existsByIdUsuario(pacienteDTO.getIdUsuario())) {
            throw new BadRequestException("Ya existe un paciente con el ID de usuario: " + pacienteDTO.getIdUsuario());
        }
        Paciente paciente = pacienteMapper.toEntity(pacienteDTO);
        Paciente savedPaciente = pacienteRepository.save(paciente);
        log.info("Paciente creado con id: {}", savedPaciente.getId());
        return pacienteMapper.toDTO(savedPaciente);
    }

    @Override
    @Transactional
    public PacienteDTO update(Long id, PacienteDTO pacienteDTO) {
        log.info("Actualizando paciente con id: {}", id);
        if (!pacienteRepository.existsById(id)) {
            throw new PacienteNotFoundException("No se encontró paciente con el id: " + id, HttpStatus.NOT_FOUND);
        }

        // Verificar si al cambiar el ID de usuario no colisiona con otro paciente existente
        pacienteRepository.findByIdUsuario(pacienteDTO.getIdUsuario())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> {
                    throw new BadRequestException("Ya existe otro paciente con el ID de usuario: " + pacienteDTO.getIdUsuario());
                });

        Paciente paciente = pacienteMapper.toEntity(pacienteDTO);
        paciente.setId(id);
        Paciente updatedPaciente = pacienteRepository.save(paciente);
        log.info("Paciente actualizado con éxito");
        return pacienteMapper.toDTO(updatedPaciente);
    }

    @Override
    public Long countPacientes() {
        return pacienteRepository.count();
    }

    @Override
    public List<PacientesPorMesDTO> getPacientesPorMes() {
        List<Object[]> resultados = pacienteRepository.countPacientesPorMes();
        String[] nombresMeses = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        return resultados.stream()
                .map(fila -> {
                    Integer mes = ((Number) fila[0]).intValue();
                    Long total = ((Number) fila[1]).longValue();
                    return new PacientesPorMesDTO(nombresMeses[mes - 1], total);
                })
                .sorted(Comparator.comparing(dto -> dto.getMes()))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando paciente con id: {}", id);
        if (!pacienteRepository.existsById(id)) {
            throw new PacienteNotFoundException("No se encontró paciente con el id: " + id, HttpStatus.NOT_FOUND);
        }
        pacienteRepository.deleteById(id);
        log.info("Paciente eliminado con éxito");
    }
}