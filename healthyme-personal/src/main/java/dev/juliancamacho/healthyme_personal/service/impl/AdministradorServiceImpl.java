package dev.juliancamacho.healthyme_personal.service.impl;

import dev.juliancamacho.healthyme_personal.dto.AdministradorDto;
import dev.juliancamacho.healthyme_personal.entity.Administrador;
import dev.juliancamacho.healthyme_personal.exception.NotFoundException;
import dev.juliancamacho.healthyme_personal.mapper.AdministradorMapper;
import dev.juliancamacho.healthyme_personal.repository.AdministradorRepository;
import dev.juliancamacho.healthyme_personal.service.interfaces.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.feign.UsuarioClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdministradorServiceImpl implements AdministradorService
{

    private final AdministradorRepository administradorRepository;
    private final AdministradorMapper administradorMapper;
    private final UsuarioClient usuarioClient;

    // CREATE
    @Override
    public AdministradorDto createAdministrador(AdministradorDto administradorDto) {
        // Validar que el usuario existe
        UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(administradorDto.getIdUsuario());
        if(usuarioDTO == null || usuarioDTO.getIdUsuario() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        Administrador administrador = administradorMapper.administradorDtoToAdministrador(administradorDto);
        administrador = administradorRepository.save(administrador);
        return administradorMapper.administradorToAdministradorDto(administrador);
    }

    // SELECT BY ID
    @Override
    public AdministradorDto getAdministradorById(Integer id) {
        Administrador administrador = administradorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Administrador", id));

        // Obtener información del usuario
        UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(administrador.getIdUsuario());

        AdministradorDto administradorDto = administradorMapper.administradorToAdministradorDto(administrador);

        administradorDto.setNombreUsuario(usuarioDTO.getNombreUsuario());
        administradorDto.setContratos(usuarioDTO.getContratos());
        administradorDto.setImagenPerfil(usuarioDTO.getImagenPerfil());
        administradorDto.setRol(usuarioDTO.getRol());
        administradorDto.setEstado(usuarioDTO.getEstado());

        return administradorDto;
    }

    // SELECT ALL
    @Override
    public List<AdministradorDto> getAllAdministrador() {
        return administradorRepository.findAll()
                .stream().map(administrador -> {
                    AdministradorDto administradorDto = administradorMapper.administradorToAdministradorDto(administrador);
                    UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(administrador.getIdUsuario());
                    administradorDto.setNombreUsuario(usuarioDTO.getNombreUsuario());
                    administradorDto.setContratos(usuarioDTO.getContratos());
                    administradorDto.setImagenPerfil(usuarioDTO.getImagenPerfil());
                    administradorDto.setRol(usuarioDTO.getRol());
                    administradorDto.setEstado(usuarioDTO.getEstado());
                    return administradorDto;
                }).collect(Collectors.toList());
    }

    // UPDATE
    @Override
    public AdministradorDto updateAdministrador(Integer id, AdministradorDto administradorDto) {
        Administrador administrador = administradorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Administrador", id));

        // Validar usuario
        UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(administradorDto.getIdUsuario());
        if(usuarioDTO == null || usuarioDTO.getIdUsuario() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        administrador.setIdUsuario(usuarioDTO.getIdUsuario());
        administrador.setCargo(administradorDto.getCargo());

        Administrador savedAdministrador = administradorRepository.save(administrador);

        return administradorMapper.administradorToAdministradorDto(savedAdministrador);
    }

    // DELETE BY ID
    @Override
    public void deleteAdministradorById(Integer id) {
        administradorRepository.deleteById(id);
    }
}
