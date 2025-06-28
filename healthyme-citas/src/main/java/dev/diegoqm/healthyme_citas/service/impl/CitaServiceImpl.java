package dev.diegoqm.healthyme_citas.service.impl;

import dev.diegoqm.healthyme_citas.dto.CitaDTO;
import dev.diegoqm.healthyme_citas.dto.CitasHoyDTO;
import dev.diegoqm.healthyme_citas.dto.EspecialidadContadaDTO;
import dev.diegoqm.healthyme_citas.entity.Cita;
import dev.diegoqm.healthyme_citas.exception.CitaNotFoundException;
import dev.diegoqm.healthyme_citas.mapper.CitaMapper;
import dev.diegoqm.healthyme_citas.repository.CitaRepository;
import dev.diegoqm.healthyme_citas.service.interfaces.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_commons.client.dto.MedicoDTO;
import studio.devbyjose.healthyme_commons.client.dto.PacienteDTO;
import studio.devbyjose.healthyme_commons.client.feign.MedicoClient;
import studio.devbyjose.healthyme_commons.client.feign.PacienteClient;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;

    @Override
    public CitaDTO createCita(CitaDTO citaDto) {
        // Validar si el paciente existe
        ResponseEntity<PacienteDTO> pacienteResponse = pacienteClient.findPacienteById(citaDto.getIdPaciente());
        if (!pacienteResponse.getStatusCode().is2xxSuccessful()) {
            throw new CitaNotFoundException("Paciente con id " + citaDto.getIdPaciente() + " no encontrado", HttpStatus.NOT_FOUND);
        }
        Cita cita = citaMapper.toEntity(citaDto);
        Cita saved = citaRepository.save(cita);
        return citaMapper.toDTO(saved);
    }

    @Override
    public CitaDTO getCitaById(String id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new CitaNotFoundException("Cita con id " + id + " no encontrada", HttpStatus.NOT_FOUND));
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
    public CitaDTO updateCita(String id, @Valid CitaDTO citaDto) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new CitaNotFoundException("Cita con id " + id + " no encontrada", HttpStatus.NOT_FOUND));

        // Validar si el paciente existe
        ResponseEntity<PacienteDTO> pacienteDTO= pacienteClient.findPacienteById(citaDto.getIdPaciente());
        if (!pacienteDTO.getStatusCode().is2xxSuccessful() || pacienteDTO.getBody() == null) {
            throw new CitaNotFoundException("Paciente con id " + citaDto.getIdPaciente() + " no encontrado", HttpStatus.NOT_FOUND);
        }
        cita.setFecha(citaDto.getFecha());
        cita.setHora(citaDto.getHora());
        cita.setEstado(citaDto.getEstado());
        cita.setIdPaciente(pacienteDTO.getBody().getId());
        cita.setIdMedico(citaDto.getIdMedico());
        cita.setIdConsultorio(citaDto.getIdConsultorio());
        Cita updated = citaRepository.save(cita);
        return citaMapper.toDTO(updated);
    }

    @Override
    public Long countCitas() {
        return citaRepository.count();
    }

    @Override
    public List<CitasHoyDTO> getCitasDeHoy() {
        LocalDate hoy = LocalDate.now();
        List<Cita> citasHoy = citaRepository.findByFecha(hoy);

        List<CitasHoyDTO> resultado = new ArrayList<>();

        for (Cita cita : citasHoy) {
            CitasHoyDTO dto = new CitasHoyDTO();
            dto.setId(cita.getId());

            // Formatear hora
            String horaFormateada = cita.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
            dto.setTime(horaFormateada);

            // Estado
            dto.setStatus(cita.getEstado().name().toLowerCase());

            // Obtener datos del médico
            try {
                Integer idMedico = Integer.valueOf(cita.getIdMedico());
                MedicoDTO medicoDTO = medicoClient.obtenerMedico(idMedico);
                dto.setDoctor(medicoDTO.getNombre() + " " + medicoDTO.getApellido());
                dto.setArea(medicoDTO.getEspecialidad());
            } catch (Exception e) {
                dto.setDoctor("Desconocido");
                dto.setArea("Desconocida");
            }

            // Obtener nombre del paciente (opcional)
            try {
                ResponseEntity<PacienteDTO> pacienteResponse = pacienteClient.findPacienteById(cita.getIdPaciente());
                if (pacienteResponse.getStatusCode().is2xxSuccessful() && pacienteResponse.getBody() != null) {
                    PacienteDTO paciente = pacienteResponse.getBody();
                    dto.setPatient("Paciente " + paciente.getId()); // Puedes cambiar esto si tienes acceso a nombre/apellido
                } else {
                    dto.setPatient("Desconocido");
                }
            } catch (Exception e) {
                dto.setPatient("Desconocido");
            }

            resultado.add(dto);
        }

        return resultado;
    }

    @Override
    public List<EspecialidadContadaDTO> getEspecialidadesMasSolicitadas() {
        List<Cita> citas = citaRepository.findAll();
        Map<String, Long> conteoPorEspecialidad = new HashMap<>();

        for (Cita cita : citas) {
            try {
                Integer idMedico = Integer.valueOf(cita.getIdMedico());
                MedicoDTO medico = medicoClient.obtenerMedico(idMedico);
                String especialidad = medico.getEspecialidad();

                conteoPorEspecialidad.put(especialidad,
                        conteoPorEspecialidad.getOrDefault(especialidad, 0L) + 1);
            } catch (Exception e) {
                // Puedes registrar el error si el médico no se encuentra
            }
        }

        return conteoPorEspecialidad.entrySet().stream()
                .map(entry -> new EspecialidadContadaDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getCantidad(), a.getCantidad())) // orden descendente
                .collect(Collectors.toList());
    }



    @Override
    public void deleteCitaById(String id) {
        if (!citaRepository.existsById(id)) {
            throw new CitaNotFoundException("Cita con id " + id + " no encontrada", HttpStatus.NOT_FOUND);
        }
        citaRepository.deleteById(id);
    }
}
