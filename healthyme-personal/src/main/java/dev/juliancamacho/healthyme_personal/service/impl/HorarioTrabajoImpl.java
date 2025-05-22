package dev.juliancamacho.healthyme_personal.service.impl;

import dev.juliancamacho.healthyme_personal.dto.HorarioTrabajoDto;
import dev.juliancamacho.healthyme_personal.entity.HorarioTrabajo;
import dev.juliancamacho.healthyme_personal.exception.NotFoundException;
import dev.juliancamacho.healthyme_personal.repository.HorarioTrabajoRepository;
import dev.juliancamacho.healthyme_personal.service.interfaces.HorarioTrabajoService;
import dev.juliancamacho.healthyme_personal.mapper.HorarioTrabajoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioTrabajoImpl implements HorarioTrabajoService {

    private final HorarioTrabajoRepository horarioTrabajoRepository;
    private final HorarioTrabajoMapper horarioTrabajoMapper;

    // CREATE
    @Override
    public HorarioTrabajoDto createHorarioTrabajo(HorarioTrabajoDto horarioTrabajoDto) {
        HorarioTrabajo horarioTrabajo = horarioTrabajoMapper.horarioTrabajoDtoToHorarioTrabajo(horarioTrabajoDto);
        HorarioTrabajo savedHorarioTrabajo = horarioTrabajoRepository.save(horarioTrabajo);
        return horarioTrabajoMapper.horarioTrabajoToHorarioTrabajoDto(savedHorarioTrabajo);
    }

    // SELECT BY ID
    @Override
    public HorarioTrabajoDto getHorarioTrabajoById(Integer id) {
        HorarioTrabajo horarioTrabajo = horarioTrabajoRepository
                .findById(id).orElseThrow(() -> new NotFoundException("Horario de Trabajo", id));
        return horarioTrabajoMapper.horarioTrabajoToHorarioTrabajoDto(horarioTrabajo);
    }

    // SELECT ALL
    @Override
    public List<HorarioTrabajoDto> getAllHorarioTrabajo() {
        List<HorarioTrabajo> horarioTrabajo = horarioTrabajoRepository.findAll();

        return horarioTrabajo.stream().map(
                horarioTrabajoMapper::horarioTrabajoToHorarioTrabajoDto).collect(Collectors.toList()
        );
    }

    // UPDATE
    @Override
    public HorarioTrabajoDto updateHorarioTrabajo(Integer id, HorarioTrabajoDto horarioTrabajoDto) {
        HorarioTrabajo horarioTrabajo = horarioTrabajoRepository
                .findById(id).orElseThrow(() -> new NotFoundException("Horario de Trabajo", id));

        horarioTrabajo.setDiaSemana(horarioTrabajoDto.getDiaSemana());
        horarioTrabajo.setHoraInicio(horarioTrabajoDto.getHoraInicio());
        horarioTrabajo.setHoraFin(horarioTrabajoDto.getHoraFin());

        HorarioTrabajo savedHorarioTrabajo = horarioTrabajoRepository.save(horarioTrabajo);

        return horarioTrabajoMapper.horarioTrabajoToHorarioTrabajoDto(savedHorarioTrabajo);
    }

    // DELETE BY ID
    @Override
    public void deleteHorarioTrabajoById(Integer id) {
        horarioTrabajoRepository.deleteById(id);
    }

}
