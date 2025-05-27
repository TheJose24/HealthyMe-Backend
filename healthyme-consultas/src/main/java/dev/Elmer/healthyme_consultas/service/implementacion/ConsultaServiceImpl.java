package dev.Elmer.healthyme_consultas.service.implementacion;

import dev.Elmer.healthyme_consultas.dto.ConsultaDto;
import dev.Elmer.healthyme_consultas.entity.Consulta;
import dev.Elmer.healthyme_consultas.exception.*;
import dev.Elmer.healthyme_consultas.repository.ConsultaRepository;
import dev.Elmer.healthyme_consultas.service.interfaces.ConsultaService;
import dev.Elmer.healthyme_consultas.mapper.ConsultaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultaServiceImpl implements ConsultaService {

    private final ConsultaRepository repository;
    private final ConsultaMapper mapper;

    @Override
    public List<ConsultaDto> listar() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ConsultaDto guardar(ConsultaDto dto) {
        if (dto == null || dto.getIdPaciente() == null || dto.getIdMedico() == null) {
            throw new InvalidDataException("Los datos de la consulta son inválidos o incompletos.");
        }

        Optional<Consulta> existente = repository.findAll().stream()
                .filter(c -> c.getFecha().equals(dto.getFecha()) &&
                        c.getIdPaciente().equals(dto.getIdPaciente()) &&
                        c.getIdMedico().equals(dto.getIdMedico()))
                .findFirst();

        if (existente.isPresent()) {
            throw new ConsultaConflictException("Ya existe una consulta registrada para este paciente, médico y fecha.");
        }

        Consulta entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public ConsultaDto buscarPorId(Integer id) {
        Consulta consulta = repository.findById(id)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta no encontrada con id " + id));
        return mapper.toDto(consulta);
    }

    @Override
    public ConsultaDto actualizar(Integer id, ConsultaDto dto) {
        if (dto == null) {
            throw new InvalidDataException("Los datos proporcionados para la actualización no pueden ser nulos.");
        }

        Consulta consulta = repository.findById(id)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta no encontrada con id " + id));

        consulta.setSintomas(dto.getSintomas());
        consulta.setDiagnostico(dto.getDiagnostico());
        consulta.setFecha(dto.getFecha());
        consulta.setIdCita(dto.getIdCita());
        consulta.setIdPaciente(dto.getIdPaciente());
        consulta.setIdMedico(dto.getIdMedico());

        return mapper.toDto(repository.save(consulta));
    }

    @Override
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ConsultaNotFoundException(id);
        }
        repository.deleteById(id);
    }
}