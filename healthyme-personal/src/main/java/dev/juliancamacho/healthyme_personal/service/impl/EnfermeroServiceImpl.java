package dev.juliancamacho.healthyme_personal.service.impl;

import dev.juliancamacho.healthyme_personal.dto.EnfermeroDto;
import dev.juliancamacho.healthyme_personal.entity.Enfermero;
import dev.juliancamacho.healthyme_personal.exception.NotFoundException;
import dev.juliancamacho.healthyme_personal.mapper.EnfermeroMapper;
import dev.juliancamacho.healthyme_personal.repository.EnfermeroRepository;
import dev.juliancamacho.healthyme_personal.service.interfaces.EnfermeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.feign.UsuarioClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnfermeroServiceImpl implements EnfermeroService {

    private final EnfermeroRepository enfermeroRepository;
    private final EnfermeroMapper enfermeroMapper;
    private final UsuarioClient usuarioClient;

    // CREATE
    @Override
    public EnfermeroDto createEnfermero(EnfermeroDto enfermeroDto) {
        // Validar que el usuario existe
        ResponseEntity<UsuarioDTO> usuarioDTO = usuarioClient.obtenerUsuario(enfermeroDto.getIdUsuario());
        if(usuarioDTO == null || Objects.requireNonNull(usuarioDTO.getBody()).getIdUsuario() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        Enfermero enfermero = enfermeroMapper.enfermeroDtoToEnfermero(enfermeroDto);
        enfermero = enfermeroRepository.save(enfermero);
        return enfermeroMapper.enfermeroToEnfermeroDto(enfermero);
    }

    // SELECT BY ID
    @Override
    public EnfermeroDto getEnfermeroById(Integer id) {
        Enfermero enfermero = enfermeroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Enfermero", id));

        // Obtener información del usuario
        ResponseEntity<UsuarioDTO> usuarioDTO = usuarioClient.obtenerUsuario(enfermero.getIdUsuario());

        EnfermeroDto enfermeroDto = enfermeroMapper.enfermeroToEnfermeroDto(enfermero);

        enfermeroDto.setNombreUsuario(Objects.requireNonNull(usuarioDTO.getBody()).getNombreUsuario());
        enfermeroDto.setContratos(usuarioDTO.getBody().getContratos());
        enfermeroDto.setImagenPerfil(usuarioDTO.getBody().getImagenPerfil());
        enfermeroDto.setRol(usuarioDTO.getBody().getRol());
        enfermeroDto.setEstado(usuarioDTO.getBody().getEstado());

        return enfermeroDto;
    }

    // SELECT ALL
    @Override
    public List<EnfermeroDto> getAllEnfermero() {
        return enfermeroRepository.findAll()
                .stream().map(enfermero -> {
                    EnfermeroDto enfermeroDto = enfermeroMapper.enfermeroToEnfermeroDto(enfermero);
                    ResponseEntity<UsuarioDTO> usuarioDTO = usuarioClient.obtenerUsuario(enfermero.getIdUsuario());
                    enfermeroDto.setNombreUsuario(Objects.requireNonNull(usuarioDTO.getBody()).getNombreUsuario());
                    enfermeroDto.setContratos(usuarioDTO.getBody().getContratos());
                    enfermeroDto.setImagenPerfil(usuarioDTO.getBody().getImagenPerfil());
                    enfermeroDto.setRol(usuarioDTO.getBody().getRol());
                    enfermeroDto.setEstado(usuarioDTO.getBody().getEstado());
                    return enfermeroDto;
                }).collect(Collectors.toList());
    }

    // UPDATE
    @Override
    public EnfermeroDto updateEnfermero(Integer id, EnfermeroDto enfermeroDto) {
        Enfermero enfermero = enfermeroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Enfermero", id));

        // Validar usuario
        ResponseEntity<UsuarioDTO> usuarioDTO = usuarioClient.obtenerUsuario(enfermeroDto.getIdUsuario());
        if(usuarioDTO == null || Objects.requireNonNull(usuarioDTO.getBody()).getIdUsuario() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        enfermero.setIdUsuario(usuarioDTO.getBody().getIdUsuario());
        enfermero.setHorario(enfermeroDto.getHorario());

        Enfermero savedEnfermero = enfermeroRepository.save(enfermero);

        return enfermeroMapper.enfermeroToEnfermeroDto(savedEnfermero);
    }

    // DELETE BY ID
    @Override
    public void deleteEnfermeroById(Integer id) {
        enfermeroRepository.deleteById(id);
    }
}
