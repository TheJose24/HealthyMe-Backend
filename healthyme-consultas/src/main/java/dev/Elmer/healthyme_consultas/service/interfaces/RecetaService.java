package dev.Elmer.healthyme_consultas.service.interfaces;

import dev.Elmer.healthyme_consultas.dto.RecetaDto;
import java.util.List;

public interface RecetaService {
    List<RecetaDto> listar();

    RecetaDto guardar(RecetaDto dto);

    RecetaDto buscarPorId(Integer id);

    RecetaDto actualizar(Integer id, RecetaDto dto);

    void eliminar(Integer id);
}