package dev.diegoqm.healthyme_infraestructura.service.impl;

import dev.diegoqm.healthyme_infraestructura.dto.LaboratorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Laboratorio;
import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import dev.diegoqm.healthyme_infraestructura.exception.LaboratorioNotFoundException;
import dev.diegoqm.healthyme_infraestructura.exception.SedeNotFoundException;
import dev.diegoqm.healthyme_infraestructura.mapper.LaboratorioMapper;
import dev.diegoqm.healthyme_infraestructura.repository.LaboratorioRepository;
import dev.diegoqm.healthyme_infraestructura.repository.SedeRepository;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.LaboratorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaboratorioServiceImpl implements LaboratorioService {

    private final LaboratorioRepository repository;
    private final SedeRepository repositorySed;
    private final LaboratorioMapper mapper;

    @Override
    public LaboratorioDTO createLaboratorio(LaboratorioDTO dto) {
        Sede sede = repositorySed.findById(dto.getIdSede())
                .orElseThrow(() -> new SedeNotFoundException("Sede no encontrada con id: " + dto.getIdSede(), HttpStatus.NOT_FOUND));
        Laboratorio entity = mapper.toEntity(dto);
        entity.setSede(sede);
        Laboratorio saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public LaboratorioDTO getLaboratorioById(int id) {
        Laboratorio lab = repository.findById(id)
                .orElseThrow(() -> new LaboratorioNotFoundException("Laboratorio no encontrado con id: " + id, HttpStatus.NOT_FOUND));
        return mapper.toDTO(lab);
    }

    @Override
    public List<LaboratorioDTO> getAllLaboratorios() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LaboratorioDTO updateLaboratorio(int id, LaboratorioDTO dto) {
        Laboratorio lab = repository.findById(id)
                .orElseThrow(() -> new LaboratorioNotFoundException("Laboratorio no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        lab.setNombre(dto.getNombre());
        lab.setPiso(dto.getPiso());
        lab.setNumeroHabitacion(dto.getNumeroHabitacion());

        Sede sede = repositorySed.findById(id)
                .orElseThrow(() -> new SedeNotFoundException("Sede no encontrada con id: " + id, HttpStatus.NOT_FOUND));
        lab.setSede(sede);

        Laboratorio updated = repository.save(lab);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteLaboratorioById(int id) {
        if (!repository.existsById(id)) {
            throw new LaboratorioNotFoundException("Laboratorio no encontrado con id: " + id, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
