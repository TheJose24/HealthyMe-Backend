package dev.juliancamacho.healthyme_personal.service.impl;

import dev.juliancamacho.healthyme_personal.dto.UnidadDto;
import dev.juliancamacho.healthyme_personal.entity.Unidad;
import dev.juliancamacho.healthyme_personal.exception.NotFoundException;
import dev.juliancamacho.healthyme_personal.repository.UnidadRepository;
import dev.juliancamacho.healthyme_personal.service.interfaces.UnidadService;
import dev.juliancamacho.healthyme_personal.mapper.UnidadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnidadServiceImpl implements UnidadService {

    private final UnidadRepository unidadRepository;
    private final UnidadMapper unidadMapper;

    // CREATE
    @Override
    public UnidadDto createUnidad(UnidadDto unidadDto) {
        Unidad unidad = unidadMapper.unidadDtoToUnidad(unidadDto);
        Unidad savedUnidad = unidadRepository.save(unidad);
        return unidadMapper.unidadToUnidadDto(savedUnidad);
    }

    // SELECT BY ID
    @Override
    public UnidadDto getUnidadById(Integer id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidad", id));
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
    public UnidadDto updateUnidad(Integer id, UnidadDto unidadDto) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidad", id));

        unidad.setNombreUnidad(unidadDto.getNombreUnidad());
        unidad.setImgUnidad(unidadDto.getImgUnidad());

        Unidad savedUnidad = unidadRepository.save(unidad);

        return unidadMapper.unidadToUnidadDto(savedUnidad);
    }

    // DELETE BY ID
    @Override
    public void deleteUnidadById(Integer id) {
        unidadRepository.deleteById(id);
    }
}
