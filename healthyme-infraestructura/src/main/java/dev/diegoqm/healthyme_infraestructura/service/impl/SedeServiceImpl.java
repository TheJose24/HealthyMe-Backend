package dev.diegoqm.healthyme_infraestructura.service.impl;

import dev.diegoqm.healthyme_infraestructura.dto.SedeDTO;
import dev.diegoqm.healthyme_infraestructura.entity.HorarioTrabajo;
import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import dev.diegoqm.healthyme_infraestructura.exception.HorarioTrabajoNotFoundException;
import dev.diegoqm.healthyme_infraestructura.exception.SedeNotFoundException;
import dev.diegoqm.healthyme_infraestructura.mapper.SedeMapper;
import dev.diegoqm.healthyme_infraestructura.repository.HorarioTrabajoRepository;
import dev.diegoqm.healthyme_infraestructura.repository.SedeRepository;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.SedeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SedeServiceImpl implements SedeService {

    private final SedeRepository repository;
    private final HorarioTrabajoRepository repositoryHor;
    private final SedeMapper mapper;

    @Override
    public SedeDTO createSede(SedeDTO dto) {
    HorarioTrabajo horario = repositoryHor.findById(dto.getIdHorario())
            .orElseThrow(() -> new HorarioTrabajoNotFoundException(
                    "Horario de trabajo no encontrado con id: " + dto.getIdHorario(), HttpStatus.NOT_FOUND));
        Sede entity = mapper.toEntity(dto);
        entity.setHorarioTrabajo(horario);
        Sede saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public SedeDTO getSedeById(int id) {
        Sede sede = repository.findById(id)
                .orElseThrow(() -> new SedeNotFoundException("Sede no encontrada con id: " + id, HttpStatus.NOT_FOUND));
        return mapper.toDTO(sede);
    }

    @Override
    public List<SedeDTO> getAllSedes() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SedeDTO updateSede(int id, SedeDTO dto) {
        Sede sede = repository.findById(id)
                .orElseThrow(() -> new SedeNotFoundException("Sede no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        sede.setNombre(dto.getNombre());
        sede.setDireccion(dto.getDireccion());
        sede.setTelefono(dto.getTelefono());
        sede.setEmail(dto.getEmail());

        HorarioTrabajo horario = repositoryHor.findById(id)
                .orElseThrow(() -> new HorarioTrabajoNotFoundException("Horario de trabajo no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND));
        sede.setHorarioTrabajo(horario);

        Sede updated = repository.save(sede);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteSedeById(int id) {
        if (!repository.existsById(id)) {
            throw new SedeNotFoundException("Sede no encontrada con id: " + id, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
