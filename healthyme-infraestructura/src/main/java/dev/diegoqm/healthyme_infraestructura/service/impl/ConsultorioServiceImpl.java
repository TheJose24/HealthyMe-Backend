package dev.diegoqm.healthyme_infraestructura.service.impl;

import dev.diegoqm.healthyme_infraestructura.dto.ConsultorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Consultorio;
import dev.diegoqm.healthyme_infraestructura.repository.ConsultorioRepository;
import dev.diegoqm.healthyme_infraestructura.repository.SedeRepository;
import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.ConsultorioService;
import dev.diegoqm.healthyme_infraestructura.util.ConsultorioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultorioServiceImpl implements ConsultorioService {

    @Autowired
    private ConsultorioRepository repository;

    @Autowired
    private SedeRepository repositorySed;

    @Autowired
    private ConsultorioMapper mapper;

    @Override
    public ConsultorioDTO createConsultorio(ConsultorioDTO dto) {
        Consultorio entity = mapper.toEntity(dto);
        Consultorio saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public ConsultorioDTO getConsultorioById(int id) {
        Consultorio cons = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultorio no encontrado"));
        return mapper.toDTO(cons);
    }

    @Override
    public List<ConsultorioDTO> getAllConsultorios() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ConsultorioDTO updateConsultorio(int id, ConsultorioDTO dto) {
        Consultorio consul = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultorio no encontrado"));
        
        consul.setNombre(dto.getNombre());
        consul.setPiso(dto.getPiso());
        consul.setNumeroHabitacion(dto.getNumeroHabitacion());

        Sede sede = repositorySed.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        consul.setSede(sede);

        Consultorio updated = repository.save(consul);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteConsultorioById(int id) {
        repository.deleteById(id);
    }
}
