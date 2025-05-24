package dev.choco.healthyme_laboratorio.service.implementacion;

import dev.choco.healthyme_laboratorio.dto.ExamenDTO;
import dev.choco.healthyme_laboratorio.entity.Examen;
import dev.choco.healthyme_laboratorio.entity.ReservaLab;
import dev.choco.healthyme_laboratorio.exception.ResourceNotFoundException;
import dev.choco.healthyme_laboratorio.repository.ExamenRepository;
import dev.choco.healthyme_laboratorio.repository.ReservaLabRepository;
import dev.choco.healthyme_laboratorio.service.Interfaces.ExamenService;
import dev.choco.healthyme_laboratorio.mapper.ExamenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamenServiceImpl implements ExamenService {
    private final ExamenRepository repository;
    private final ReservaLabRepository reservaLabRepository;
    private final ExamenMapper mapper;

    @Override
    public List<ExamenDTO> listar() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExamenDTO guardar(ExamenDTO dto) {
        ReservaLab reserva = reservaLabRepository.findById(dto.getIdReservaLab())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + dto.getIdReservaLab()));

        Examen examen = mapper.toEntity(dto);
        examen.setReservaLab(reserva);

        return mapper.toDTO(repository.save(examen));
    }

    @Override
    public ExamenDTO buscarPorId(Integer id) {
        Examen examen = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));
        return mapper.toDTO(examen);
    }

    @Override
    public ExamenDTO actualizar(Integer id, ExamenDTO dto) {
        Examen examen = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));

        ReservaLab reserva = reservaLabRepository.findById(dto.getIdReservaLab())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + dto.getIdReservaLab()));

        examen.setNombreExamen(dto.getNombreExamen());
        examen.setResultados(dto.getResultados());
        examen.setObservaciones(dto.getObservaciones());
        examen.setFechaRealizacion(dto.getFechaRealizacion());
        examen.setIdLaboratorio(dto.getIdLaboratorio());
        examen.setIdPaciente(dto.getIdPaciente());
        examen.setIdTecnico(dto.getIdTecnico());
        examen.setReservaLab(reserva);

        return mapper.toDTO(repository.save(examen));
    }

    @Override
    public void eliminar(Integer id) {
        Examen examen = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));
        repository.delete(examen);
    }
}
