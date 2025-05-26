package dev.Elmer.healthyme_consultas.service.implementacion;

import dev.Elmer.healthyme_consultas.dto.RecetaDto;
import dev.Elmer.healthyme_consultas.dto.MedicamentoDto;
import dev.Elmer.healthyme_consultas.entity.Consulta;
import dev.Elmer.healthyme_consultas.entity.Receta;
import dev.Elmer.healthyme_consultas.entity.Medicamento;
import dev.Elmer.healthyme_consultas.exception.*;
import dev.Elmer.healthyme_consultas.repository.ConsultaRepository;
import dev.Elmer.healthyme_consultas.repository.RecetaRepository;
import dev.Elmer.healthyme_consultas.service.interfaces.RecetaService;
import dev.Elmer.healthyme_consultas.mapper.RecetaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final ConsultaRepository consultaRepository;
    private final RecetaMapper recetaMapper;

    @Override
    public RecetaDto guardar(RecetaDto dto) {
        if (dto == null || dto.getIdConsulta() == null) {
            throw new InvalidDataException("Los datos de la receta son inválidos o incompletos.");
        }

        Consulta consulta = consultaRepository.findById(dto.getIdConsulta())
                .orElseThrow(() -> new ConsultaNotFoundException(dto.getIdConsulta()));

        Receta receta = recetaMapper.toEntity(dto);
        receta.setConsulta(consulta);

        Receta saved = recetaRepository.save(receta);
        return recetaMapper.toDto(saved);
    }

    @Override
    public List<RecetaDto> listar() {
        return recetaRepository.findAll().stream()
                .map(recetaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RecetaDto buscarPorId(Integer id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RecetaNotFoundException(id));
        return recetaMapper.toDto(receta);
    }

    @Override
    public RecetaDto actualizar(Integer id, RecetaDto dto) {
        if (dto == null) {
            throw new InvalidDataException("Los datos de la receta no pueden ser nulos.");
        }

        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RecetaNotFoundException(id));

        List<Medicamento> medicamentos = dto.getMedicamentos().stream()
                .map(m -> new Medicamento(m.getNombre(), m.getDosis(), m.getIndicaciones()))
                .collect(Collectors.toList());

        receta.setMedicamentos(medicamentos);
        receta.setFechaEmision(dto.getFechaEmision());

        if (!receta.getConsulta().getIdConsulta().equals(dto.getIdConsulta())) {
            Consulta nuevaConsulta = consultaRepository.findById(dto.getIdConsulta())
                    .orElseThrow(() -> new ConsultaNotFoundException(dto.getIdConsulta()));
            receta.setConsulta(nuevaConsulta);
        }

        Receta updated = recetaRepository.save(receta);
        return recetaMapper.toDto(updated);
    }

    @Override
    public void eliminar(Integer id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RecetaNotFoundException(id));
        recetaRepository.delete(receta);
    }
}