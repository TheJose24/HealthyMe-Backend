package dev.juliancamacho.healthyme_personal.service.impl;

import dev.juliancamacho.healthyme_personal.dto.TecnicoDto;
import dev.juliancamacho.healthyme_personal.entity.Tecnico;
import dev.juliancamacho.healthyme_personal.exception.NotFoundException;
import dev.juliancamacho.healthyme_personal.mapper.TecnicoMapper;
import dev.juliancamacho.healthyme_personal.repository.TecnicoRepository;
import dev.juliancamacho.healthyme_personal.service.interfaces.TecnicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.feign.UsuarioClient;

import java.util.List;
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
        UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(tecnicoDto.getIdUsuario());
        if(usuarioDTO == null || usuarioDTO.getIdUsuario() == null) {
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
        UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(tecnico.getIdUsuario());

        TecnicoDto tecnicoDto = tecnicoMapper.tecnicoToTecnicoDto(tecnico);

        tecnicoDto.setNombreUsuario(usuarioDTO.getNombreUsuario());
        tecnicoDto.setContratos(usuarioDTO.getContratos());
        tecnicoDto.setImagenPerfil(usuarioDTO.getImagenPerfil());
        tecnicoDto.setRol(usuarioDTO.getRol());
        tecnicoDto.setEstado(usuarioDTO.getEstado());

        return tecnicoDto;
    }

    // SELECT ALL
    @Override
    public List<TecnicoDto> getAllTecnico() {
        return tecnicoRepository.findAll()
                .stream().map(tecnico -> {
                    TecnicoDto tecnicoDto = tecnicoMapper.tecnicoToTecnicoDto(tecnico);
                    UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(tecnico.getIdUsuario());
                    tecnicoDto.setNombreUsuario(usuarioDTO.getNombreUsuario());
                    tecnicoDto.setContratos(usuarioDTO.getContratos());
                    tecnicoDto.setImagenPerfil(usuarioDTO.getImagenPerfil());
                    tecnicoDto.setRol(usuarioDTO.getRol());
                    tecnicoDto.setEstado(usuarioDTO.getEstado());
                    return tecnicoDto;
                }).collect(Collectors.toList());
    }

    // UPDATE
    @Override
    public TecnicoDto updateTecnico(Integer id, TecnicoDto tecnicoDto) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tecnico", id));

        // Validar usuario
        UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(tecnicoDto.getIdUsuario());
        if(usuarioDTO == null || usuarioDTO.getIdUsuario() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        tecnico.setIdUsuario(usuarioDTO.getIdUsuario());
        tecnico.setHorarios(tecnicoDto.getIdHorarios());

        Tecnico savedTecnico = tecnicoRepository.save(tecnico);

        return tecnicoMapper.tecnicoToTecnicoDto(savedTecnico);
    }

    // DELETE BY ID
    @Override
    public void deleteTecnicoById(Integer id) {
        tecnicoRepository.deleteById(id);
    }
}
