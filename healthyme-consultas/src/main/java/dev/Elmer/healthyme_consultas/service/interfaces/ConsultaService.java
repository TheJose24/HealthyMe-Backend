package dev.Elmer.healthyme_consultas.service.interfaces;

import dev.Elmer.healthyme_consultas.dto.ConsultaDto;
import java.util.List;

public interface ConsultaService {
    List<ConsultaDto> listar();

    ConsultaDto guardar(ConsultaDto dto);

    ConsultaDto buscarPorId(Integer id);

    ConsultaDto actualizar(Integer id, ConsultaDto dto);

    List<ConsultaDto> listarPorPaciente(Integer idPaciente);

    void eliminar(Integer id);
}