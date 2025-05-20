package dev.juliancamacho.healthyme_personal.service.impl;

import dev.juliancamacho.healthyme_personal.dto.MedicoDto;
import dev.juliancamacho.healthyme_personal.entity.Medico;
import dev.juliancamacho.healthyme_personal.exception.NotFoundException;
import dev.juliancamacho.healthyme_personal.mapper.MedicoMapper;
import dev.juliancamacho.healthyme_personal.repository.MedicoRepository;
import dev.juliancamacho.healthyme_personal.service.interfaces.MedicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.feign.UsuarioClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final UsuarioClient usuarioClient;

    // CREATE
    @Override
    public MedicoDto createMedico(MedicoDto medicoDto) {
        // Validar que el usuario existe
        UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(medicoDto.getIdUsuario());
        if(usuarioDTO == null || usuarioDTO.getIdUsuario() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        Medico medico = medicoMapper.medicoDtoToMedico(medicoDto);
        medico = medicoRepository.save(medico);
        return medicoMapper.medicoToMedicoDto(medico);
    }

    // SELECT BY ID
    @Override
    public MedicoDto getMedicoById(Integer id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Medico", id));

        // Obtener información del usuario
        UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(medico.getIdUsuario());

        MedicoDto medicoDto = medicoMapper.medicoToMedicoDto(medico);

        medicoDto.setNombreUsuario(usuarioDTO.getNombreUsuario());
        medicoDto.setContratos(usuarioDTO.getContratos());
        medicoDto.setImagenPerfil(usuarioDTO.getImagenPerfil());
        medicoDto.setRol(usuarioDTO.getRol());
        medicoDto.setEstado(usuarioDTO.getEstado());

        return medicoDto;
    }

    // SELECT ALL
    @Override
    public List<MedicoDto> getAllMedico() {
        return medicoRepository.findAll()
                .stream().map(medico -> {
                    MedicoDto medicoDto = medicoMapper.medicoToMedicoDto(medico);
                    UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(medico.getIdUsuario());
                    medicoDto.setNombreUsuario(usuarioDTO.getNombreUsuario());
                    medicoDto.setContratos(usuarioDTO.getContratos());
                    medicoDto.setImagenPerfil(usuarioDTO.getImagenPerfil());
                    medicoDto.setRol(usuarioDTO.getRol());
                    medicoDto.setEstado(usuarioDTO.getEstado());
                    return medicoDto;
                }).collect(Collectors.toList());
    }

    // UPDATE
    @Override
    public MedicoDto updateMedico(Integer id, MedicoDto medicoDto) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Medico", id));

        // Validar usuario
        UsuarioDTO usuarioDTO = usuarioClient.obtenerUsuario(medicoDto.getIdUsuario());
        if(usuarioDTO == null || usuarioDTO.getIdUsuario() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        medico.setIdUsuario(usuarioDTO.getIdUsuario());
        medico.setHorarios(medicoDto.getIdHorarios());

        Medico savedMedico = medicoRepository.save(medico);

        return medicoMapper.medicoToMedicoDto(savedMedico);
    }

    // DELETE BY ID
    @Override
    public void deleteMedicoById(Integer id) {
        medicoRepository.deleteById(id);
    }
}
