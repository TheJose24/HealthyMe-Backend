package dev.Elmer.healthyme_consultas.service.implementacion;

import dev.Elmer.healthyme_consultas.dto.RecetaDto;
import dev.Elmer.healthyme_consultas.entity.Consulta;
import dev.Elmer.healthyme_consultas.entity.Receta;
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
        Receta receta = recetaMapper.toEntity(dto);
        Consulta consulta = consultaRepository.findById(dto.getIdConsulta())
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada con ID: " + dto.getIdConsulta()));
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
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));
        return recetaMapper.toDto(receta);
    }

    @Override
    public RecetaDto actualizar(Integer id, RecetaDto dto) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));

        receta.setMedicamento(dto.getMedicamento());
        receta.setDosis(dto.getDosis());
        receta.setInstrucciones(dto.getInstrucciones());
        receta.setFechaEmision(dto.getFechaEmision());

        // Actualiza la relación con Consulta si cambia
        if (!receta.getConsulta().getIdConsulta().equals(dto.getIdConsulta())) {
            Consulta consulta = consultaRepository.findById(dto.getIdConsulta())
                    .orElseThrow(() -> new RuntimeException("Consulta no encontrada con ID: " + dto.getIdConsulta()));
            receta.setConsulta(consulta);
        }

        Receta updated = recetaRepository.save(receta);
        return recetaMapper.toDto(updated);
    }

    @Override
    public void eliminar(Integer id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));
        recetaRepository.delete(receta);
    }
}