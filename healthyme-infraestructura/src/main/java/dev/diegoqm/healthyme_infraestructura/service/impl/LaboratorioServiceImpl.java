package dev.diegoqm.healthyme_infraestructura.service.impl;

import dev.diegoqm.healthyme_infraestructura.dto.LaboratorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Laboratorio;
import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import dev.diegoqm.healthyme_infraestructura.repository.LaboratorioRepository;
import dev.diegoqm.healthyme_infraestructura.repository.SedeRepository;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.LaboratorioService;
import dev.diegoqm.healthyme_infraestructura.mapper.LaboratorioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaboratorioServiceImpl implements LaboratorioService {

    @Autowired
    private LaboratorioRepository repository;

    @Autowired
    private SedeRepository repositorySed;

    @Autowired
    private LaboratorioMapper mapper;

    @Override
    public LaboratorioDTO createLaboratorio(LaboratorioDTO dto) {
        Laboratorio entity = mapper.toEntity(dto);
        Laboratorio saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public LaboratorioDTO getLaboratorioById(int id) {
        Laboratorio lab = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
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
                .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
        
        lab.setNombre(dto.getNombre());
        lab.setPiso(dto.getPiso());
        lab.setNumeroHabitacion(dto.getNumeroHabitacion());
        Sede sede = repositorySed.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        lab.setSede(sede);
        Laboratorio updated = repository.save(lab);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteLaboratorioById(int id) {
        repository.deleteById(id);
    }
}