package dev.juliancamacho.healthyme_personal.service.impl;

import dev.juliancamacho.healthyme_personal.dto.TecnicoDto;
import dev.juliancamacho.healthyme_personal.entity.Tecnico;
import dev.juliancamacho.healthyme_personal.exception.NotFoundException;
import dev.juliancamacho.healthyme_personal.mapper.TecnicoMapper;
import dev.juliancamacho.healthyme_personal.repository.TecnicoRepository;
import dev.juliancamacho.healthyme_personal.service.interfaces.TecnicoService;
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
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final TecnicoMapper tecnicoMapper;
    private final UsuarioClient usuarioClient;

    // CREATE
    @Override
    public TecnicoDto createTecnico(TecnicoDto tecnicoDto) {
        // Validar que el usuario existe
        ResponseEntity<UsuarioDTO> usuarioDTO = usuarioClient.obtenerUsuario(tecnicoDto.getIdUsuario());
        if(usuarioDTO == null || Objects.requireNonNull(usuarioDTO.getBody()).getIdUsuario() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        Tecnico tecnico = tecnicoMapper.tecnicoDtoToTecnico(tecnicoDto);
        tecnico = tecnicoRepository.save(tecnico);
        return tecnicoMapper.tecnicoToTecnicoDto(tecnico);
    }

    // SELECT BY ID
    @Override
    public TecnicoDto getTecnicoById(Integer id) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tecnico", id));

        // Obtener información del usuario
        ResponseEntity<UsuarioDTO> usuarioDTO = usuarioClient.obtenerUsuario(tecnico.getIdUsuario());

        TecnicoDto tecnicoDto = tecnicoMapper.tecnicoToTecnicoDto(tecnico);

        tecnicoDto.setNombreUsuario(Objects.requireNonNull(usuarioDTO.getBody()).getNombreUsuario());
        tecnicoDto.setContratos(usuarioDTO.getBody().getContratos());
        tecnicoDto.setImagenPerfil(usuarioDTO.getBody().getImagenPerfil());
        tecnicoDto.setRol(usuarioDTO.getBody().getRol());
        tecnicoDto.setEstado(usuarioDTO.getBody().getEstado());

        return tecnicoDto;
    }

    // SELECT ALL
    @Override
    public List<TecnicoDto> getAllTecnico() {
        return tecnicoRepository.findAll()
                .stream().map(tecnico -> {
                    TecnicoDto tecnicoDto = tecnicoMapper.tecnicoToTecnicoDto(tecnico);
                    ResponseEntity<UsuarioDTO> usuarioDTO = usuarioClient.obtenerUsuario(tecnico.getIdUsuario());
                    tecnicoDto.setNombreUsuario(Objects.requireNonNull(usuarioDTO.getBody()).getNombreUsuario());
                    tecnicoDto.setContratos(usuarioDTO.getBody().getContratos());
                    tecnicoDto.setImagenPerfil(usuarioDTO.getBody().getImagenPerfil());
                    tecnicoDto.setRol(usuarioDTO.getBody().getRol());
                    tecnicoDto.setEstado(usuarioDTO.getBody().getEstado());
                    return tecnicoDto;
                }).collect(Collectors.toList());
    }

    // UPDATE
    @Override
    public TecnicoDto updateTecnico(Integer id, TecnicoDto tecnicoDto) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tecnico", id));

        // Validar usuario
        ResponseEntity<UsuarioDTO> usuarioDTO = usuarioClient.obtenerUsuario(tecnicoDto.getIdUsuario());
        if(usuarioDTO == null || Objects.requireNonNull(usuarioDTO.getBody()).getIdUsuario() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        tecnico.setIdUsuario(usuarioDTO.getBody().getIdUsuario());
        tecnico.setHorariosTecnico(tecnicoDto.getIdHorarios());

        Tecnico savedTecnico = tecnicoRepository.save(tecnico);

        return tecnicoMapper.tecnicoToTecnicoDto(savedTecnico);
    }

    // DELETE BY ID
    @Override
    public void deleteTecnicoById(Integer id) {
        tecnicoRepository.deleteById(id);
    }
}
