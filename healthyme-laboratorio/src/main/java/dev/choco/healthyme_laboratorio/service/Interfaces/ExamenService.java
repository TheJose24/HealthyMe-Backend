package dev.choco.healthyme_laboratorio.service.Interfaces;

import dev.choco.healthyme_laboratorio.dto.ExamenDTO;

import java.util.List;

public interface ExamenService {
    List<ExamenDTO> listar();
    ExamenDTO guardar(ExamenDTO dto);
    ExamenDTO buscarPorId(Integer id);
    ExamenDTO actualizar(Integer id, ExamenDTO dto);
    void eliminar(Integer id);
}
