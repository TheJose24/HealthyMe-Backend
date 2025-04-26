package dev.diegoqm.healthyme_infraestructura.service.impl;

import dev.diegoqm.healthyme_infraestructura.dto.SedeDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import dev.diegoqm.healthyme_infraestructura.entity.HorarioTrabajo;
import dev.diegoqm.healthyme_infraestructura.repository.HorarioTrabajoRepository;
import dev.diegoqm.healthyme_infraestructura.repository.SedeRepository;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.SedeService;
import dev.diegoqm.healthyme_infraestructura.mapper.SedeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SedeServiceImpl implements SedeService {

    @Autowired
    private SedeRepository repository;

    @Autowired
    private HorarioTrabajoRepository repositoryHor;

    @Autowired
    private SedeMapper mapper;

    @Override
    public SedeDTO createSede(SedeDTO dto) {
        Sede entity = mapper.toEntity(dto);
        Sede saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public SedeDTO getSedeById(int id) {
        Sede sede = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
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
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        
        sede.setNombre(dto.getNombre());
        sede.setDireccion(dto.getDireccion());
        sede.setTelefono(dto.getTelefono());
        sede.setEmail(dto.getEmail());

        HorarioTrabajo horario = repositoryHor.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario de trabajo no encontrado"));
        sede.setHorarioTrabajo(horario);

        Sede updated = repository.save(sede);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteSedeById(int id) {
        repository.deleteById(id);
    }
}
