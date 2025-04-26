package dev.diegoqm.healthyme_citas.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import dev.diegoqm.healthyme_citas.dto.CitaDTO;
import dev.diegoqm.healthyme_citas.entity.Cita;
import dev.diegoqm.healthyme_citas.repository.CitaRepository;
import dev.diegoqm.healthyme_citas.service.interfaces.CitaService;
import dev.diegoqm.healthyme_citas.mapper.CitaMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private CitaMapper citaMapper;

    @Override
    public CitaDTO createCita(CitaDTO citaDto) {
        Cita cita = citaMapper.toEntity(citaDto);
        Cita saved = citaRepository.save(cita);
        return citaMapper.toDTO(saved);
    }

    @Override
    public CitaDTO getCitaById(int id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita con id " + id + " no encontrada"));
        return citaMapper.toDTO(cita);
    }

    @Override
    public List<CitaDTO> getAllCitas() {
        return citaRepository.findAll()
                .stream()
                .map(citaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CitaDTO updateCita(int id, @Valid CitaDTO citaDto) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita con id " + id + " no encontrada"));

        cita.setFecha(citaDto.getFecha());
        cita.setHora(citaDto.getHora());
        cita.setEstado(citaDto.getEstado());
        cita.setIdPaciente(citaDto.getIdPaciente());
        cita.setIdMedico(citaDto.getIdMedico());
        cita.setIdConsultorio(citaDto.getIdConsultorio());
        Cita updated = citaRepository.save(cita);
        return citaMapper.toDTO(updated);
    }

    @Override
    public void deleteCitaById(int id) {
        if (!citaRepository.existsById(id)) {
            throw new RuntimeException("Cita con id " + id + " no encontrada");
        }
        citaRepository.deleteById(id);
    }
}