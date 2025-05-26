package dev.diegoqm.healthyme_infraestructura.service.impl;

import dev.diegoqm.healthyme_infraestructura.dto.ConsultorioDTO;
import dev.diegoqm.healthyme_infraestructura.entity.Consultorio;
import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import dev.diegoqm.healthyme_infraestructura.exception.ConsultorioNotFoundException;
import dev.diegoqm.healthyme_infraestructura.exception.SedeNotFoundException;
import dev.diegoqm.healthyme_infraestructura.mapper.ConsultorioMapper;
import dev.diegoqm.healthyme_infraestructura.repository.ConsultorioRepository;
import dev.diegoqm.healthyme_infraestructura.repository.SedeRepository;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.ConsultorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultorioServiceImpl implements ConsultorioService {

    private final ConsultorioRepository repository;
    private final SedeRepository repositorySed;
    private final ConsultorioMapper mapper;

    @Override
    public ConsultorioDTO createConsultorio(ConsultorioDTO dto) {
        Sede sede = repositorySed.findById(dto.getIdSede())
                .orElseThrow(() -> new SedeNotFoundException("Sede no encontrada con id: " + dto.getIdSede(), HttpStatus.NOT_FOUND));
        Consultorio entity = mapper.toEntity(dto);
        entity.setSede(sede);
        Consultorio saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public ConsultorioDTO getConsultorioById(int id) {
        Consultorio cons = repository.findById(id)
                .orElseThrow(() -> new ConsultorioNotFoundException("Consultorio no encontrado con id: " + id, HttpStatus.NOT_FOUND));
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
                .orElseThrow(() -> new ConsultorioNotFoundException("Consultorio no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        consul.setNombre(dto.getNombre());
        consul.setPiso(dto.getPiso());
        consul.setNumeroHabitacion(dto.getNumeroHabitacion());

        Sede sede = repositorySed.findById(id)
                .orElseThrow(() -> new SedeNotFoundException("Sede no encontrada con id: " + id, HttpStatus.NOT_FOUND));
        consul.setSede(sede);

        Consultorio updated = repository.save(consul);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteConsultorioById(int id) {
        if (!repository.existsById(id)) {
            throw new ConsultorioNotFoundException("Consultorio no encontrado con id: " + id, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}