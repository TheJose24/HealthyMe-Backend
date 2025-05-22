package dev.juliancamacho.healthyme_personal.service.impl;

import dev.juliancamacho.healthyme_personal.dto.EspecialidadDto;
import dev.juliancamacho.healthyme_personal.entity.Especialidad;
import dev.juliancamacho.healthyme_personal.exception.NotFoundException;
import dev.juliancamacho.healthyme_personal.repository.EspecialidadRepository;
import dev.juliancamacho.healthyme_personal.service.interfaces.EspecialidadService;
import dev.juliancamacho.healthyme_personal.mapper.EspecialidadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final EspecialidadMapper especialidadMapper;

    // CREATE
    @Override
    public EspecialidadDto createEspecialidad(EspecialidadDto especialidadDto) {
        Especialidad especialidad = especialidadMapper.especialidadDtoToEspecialidad(especialidadDto);
        Especialidad savedEspecialidad = especialidadRepository.save(especialidad);
        return especialidadMapper.especialidadToEspecialidadDto(savedEspecialidad);
    }

    // SELECT BY ID
    @Override
    public EspecialidadDto getEspecialidadById(Integer id) {
        Especialidad especialidad = especialidadRepository
                .findById(id).orElseThrow(() -> new NotFoundException("Especialidad", id));
        return especialidadMapper.especialidadToEspecialidadDto(especialidad);
    }

    // SELECT ALL
    @Override
    public List<EspecialidadDto> getAllEspecialidades() {
        List<Especialidad> especialidades = especialidadRepository.findAll();

        return especialidades.stream().map(
                especialidadMapper::especialidadToEspecialidadDto).collect(Collectors.toList()
        );
    }

    // UPDATE
    @Override
    public EspecialidadDto updateEspecialidad(Integer id, EspecialidadDto especialidadDto) {
        Especialidad especialidad = especialidadRepository
                .findById(id).orElseThrow(() -> new NotFoundException("Especialidad", id));

        especialidad.setNombreEspecialidad(especialidadDto.getNombreEspecialidad());
        especialidad.setImgEspecialidad(especialidadDto.getImgEspecialidad());

        Especialidad savedEspecialidad = especialidadRepository.save(especialidad);

        return especialidadMapper.especialidadToEspecialidadDto(savedEspecialidad);
    }

    // DELETE BY ID
    @Override
    public void deleteEspecialidadById(Integer id) {
        especialidadRepository.deleteById(id);
    }

}
