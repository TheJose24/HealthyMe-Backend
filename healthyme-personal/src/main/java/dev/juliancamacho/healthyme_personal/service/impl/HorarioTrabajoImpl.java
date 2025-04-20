package dev.juliancamacho.healthyme_personal.service.impl;

import dev.juliancamacho.healthyme_personal.dto.HorarioTrabajoDto;
import dev.juliancamacho.healthyme_personal.entity.HorarioTrabajo;
import dev.juliancamacho.healthyme_personal.repository.HorarioTrabajoRepository;
import dev.juliancamacho.healthyme_personal.service.interfaces.HorarioTrabajoService;
import dev.juliancamacho.healthyme_personal.util.HorarioTrabajoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HorarioTrabajoImpl implements HorarioTrabajoService {

    @Autowired
    private HorarioTrabajoRepository horarioTrabajoRepository;

    // CREATE
    @Override
    public HorarioTrabajoDto createHorarioTrabajo(HorarioTrabajoDto horarioTrabajoDto) {
        HorarioTrabajo horarioTrabajo = HorarioTrabajoMapper.mapper.horarioTrabajoDtoToHorarioTrabajo(horarioTrabajoDto);
        HorarioTrabajo savedHorarioTrabajo = horarioTrabajoRepository.save(horarioTrabajo);
        return HorarioTrabajoMapper.mapper.horarioTrabajoToHorarioTrabajoDto(savedHorarioTrabajo);
    }

    // SELECT BY ID
    @Override
    public HorarioTrabajoDto getHorarioTrabajoById(int id) {
        HorarioTrabajo horarioTrabajo = horarioTrabajoRepository.findById(id).orElseThrow(() -> new RuntimeException("No existe una horarioTrabajo con e ID"));
        return HorarioTrabajoMapper.mapper.horarioTrabajoToHorarioTrabajoDto(horarioTrabajo);
    }

    // SELECT ALL
    @Override
    public List<HorarioTrabajoDto> getAllHorarioTrabajo() {
        List<HorarioTrabajo> horarioTrabajo = horarioTrabajoRepository.findAll();

        return horarioTrabajo.stream().map(
                HorarioTrabajoMapper.mapper::horarioTrabajoToHorarioTrabajoDto).collect(Collectors.toList()
        );
    }

    // UPDATE
    @Override
    public HorarioTrabajoDto updateHorarioTrabajo(int id, HorarioTrabajoDto horarioTrabajoDto) {
        HorarioTrabajo horarioTrabajo = horarioTrabajoRepository.findById(id).orElseThrow(() -> new RuntimeException("No existe una horarioTrabajo con e ID"));

        horarioTrabajo.setDiaSemana(horarioTrabajoDto.getDiaSemana());
        horarioTrabajo.setHoraInicio(horarioTrabajoDto.getHoraInicio());
        horarioTrabajo.setHoraFin(horarioTrabajoDto.getHoraFin());

        HorarioTrabajo savedHorarioTrabajo = horarioTrabajoRepository.save(horarioTrabajo);

        return HorarioTrabajoMapper.mapper.horarioTrabajoToHorarioTrabajoDto(savedHorarioTrabajo);
    }

    // DELETE BY ID
    @Override
    public void deleteHorarioTrabajoById(int id) {
        horarioTrabajoRepository.deleteById(id);
    }

}
