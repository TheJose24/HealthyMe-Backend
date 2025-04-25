package dev.diegoqm.healthyme_infraestructura.service.impl;

import dev.diegoqm.healthyme_infraestructura.dto.HorarioTrabajoDTO;
import dev.diegoqm.healthyme_infraestructura.entity.HorarioTrabajo;
import dev.diegoqm.healthyme_infraestructura.repository.HorarioTrabajoRepository;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.HorarioTrabajoService;
import dev.diegoqm.healthyme_infraestructura.util.HorarioTrabajoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioTrabajoServiceImpl implements HorarioTrabajoService {

    @Autowired
    private HorarioTrabajoRepository repository;

    @Autowired
    private HorarioTrabajoMapper mapper;

    @Override
    public HorarioTrabajoDTO createHorarioTrabajo(HorarioTrabajoDTO dto) {
        HorarioTrabajo entity = mapper.toEntity(dto);
        HorarioTrabajo saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public HorarioTrabajoDTO getHorarioTrabajoById(int id) {
        HorarioTrabajo horario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        return mapper.toDTO(horario);
    }

    @Override
    public List<HorarioTrabajoDTO> getAllHorarioTrabajo() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HorarioTrabajoDTO updateHorarioTrabajo(int id, HorarioTrabajoDTO dto) {
        HorarioTrabajo horarioTrabajo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

                horarioTrabajo.setDiaSemana(dto.getDiaSemana());
                horarioTrabajo.setHoraInicio(dto.getHoraInicio());
                horarioTrabajo.setHoraFin(dto.getHoraFin());
        
        HorarioTrabajo updated = repository.save(horarioTrabajo);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteHorarioTrabajoById(int id) {
        repository.deleteById(id);
    }
}
