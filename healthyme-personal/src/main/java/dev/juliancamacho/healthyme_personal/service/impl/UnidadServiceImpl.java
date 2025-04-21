package dev.juliancamacho.healthyme_personal.service.impl;

import dev.juliancamacho.healthyme_personal.dto.UnidadDto;
import dev.juliancamacho.healthyme_personal.entity.Unidad;
import dev.juliancamacho.healthyme_personal.repository.UnidadRepository;
import dev.juliancamacho.healthyme_personal.service.interfaces.UnidadService;
import dev.juliancamacho.healthyme_personal.util.UnidadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnidadServiceImpl implements UnidadService {

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private UnidadMapper unidadMapper;

    // CREATE
    public UnidadDto createUnidad(UnidadDto unidadDto) {
        Unidad unidad = unidadMapper.unidadDtoToUnidad(unidadDto);
        Unidad savedUnidad = unidadRepository.save(unidad);
        return unidadMapper.unidadToUnidadDto(savedUnidad);
    }

    // SELECT BY ID
    @Override
    public UnidadDto getUnidadById(int id) {
        Unidad unidad = unidadRepository.findById(id).orElseThrow(() -> new RuntimeException("No existe una unidad con ese ID"));
        return unidadMapper.unidadToUnidadDto(unidad);
    }

    // SELECT ALL
    @Override
    public List<UnidadDto> getAllUnidades() {
        List<Unidad> unidades = unidadRepository.findAll();

        return unidades.stream().map(
                unidadMapper::unidadToUnidadDto).collect(Collectors.toList()
        );
    }

    // UPDATE
    @Override
    public UnidadDto updateUnidad(int id, UnidadDto unidadDto) {
        Unidad unidad = unidadRepository.findById(id).orElseThrow(() -> new RuntimeException("No existe una unidad con ese ID"));

        unidad.setNombreUnidad(unidadDto.getNombreUnidad());
        unidad.setImgUnidad(unidadDto.getImgUnidad());

        Unidad savedUnidad = unidadRepository.save(unidad);

        return unidadMapper.unidadToUnidadDto(savedUnidad);
    }

    // DELETE BY ID
    @Override
    public void deleteUnidadById(int id) {
        unidadRepository.deleteById(id);
    }
}
