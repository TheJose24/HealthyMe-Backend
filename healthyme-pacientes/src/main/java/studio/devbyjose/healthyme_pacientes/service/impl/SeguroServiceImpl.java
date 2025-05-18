package studio.devbyjose.healthyme_pacientes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studio.devbyjose.healthyme_pacientes.dto.SeguroDTO;
import studio.devbyjose.healthyme_pacientes.entity.Seguro;
import studio.devbyjose.healthyme_pacientes.exception.SeguroNotFoundException;
import studio.devbyjose.healthyme_pacientes.mapper.SeguroMapper;
import studio.devbyjose.healthyme_pacientes.repository.SeguroRepository;
import studio.devbyjose.healthyme_pacientes.service.interfaces.SeguroService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeguroServiceImpl implements SeguroService {

    private final SeguroRepository seguroRepository;
    private final SeguroMapper seguroMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SeguroDTO> findAll() {
        log.info("Obteniendo todos los seguros médicos");
        return seguroRepository.findAll().stream()
                .map(seguroMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SeguroDTO findById(Long id) {
        log.info("Buscando seguro médico con id: {}", id);
        return seguroRepository.findById(id)
                .map(seguroMapper::toDTO)
                .orElseThrow(() -> new SeguroNotFoundException("No se encontró seguro médico con el id: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public SeguroDTO create(SeguroDTO seguroDTO) {
        log.info("Creando nuevo seguro médico: {}", seguroDTO.getNombre());
        Seguro seguro = seguroMapper.toEntity(seguroDTO);
        Seguro savedSeguro = seguroRepository.save(seguro);
        log.info("Seguro médico creado con id: {}", savedSeguro.getId());
        return seguroMapper.toDTO(savedSeguro);
    }

    @Override
    @Transactional
    public SeguroDTO update(Long id, SeguroDTO seguroDTO) {
        log.info("Actualizando seguro médico con id: {}", id);
        if (!seguroRepository.existsById(id)) {
            throw new SeguroNotFoundException("No se encontró seguro médico con el id: " + id, HttpStatus.NOT_FOUND);
        }
        Seguro seguro = seguroMapper.toEntity(seguroDTO);
        seguro.setId(id);
        Seguro updatedSeguro = seguroRepository.save(seguro);
        log.info("Seguro médico actualizado con éxito");
        return seguroMapper.toDTO(updatedSeguro);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando seguro médico con id: {}", id);
        if (!seguroRepository.existsById(id)) {
            throw new SeguroNotFoundException("No se encontró seguro médico con el id: " + id, HttpStatus.NOT_FOUND);
        }
        seguroRepository.deleteById(id);
        log.info("Seguro médico eliminado con éxito");
    }
}