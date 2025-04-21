package dev.choco.healthyme_laboratorio.service.Interfaces;

import dev.choco.healthyme_laboratorio.dto.ReservaLabDTO;

import java.util.List;

public interface ReservaLabService {
    List<ReservaLabDTO> listar();
    ReservaLabDTO guardar(ReservaLabDTO dto);
    ReservaLabDTO buscarPorId(Integer id);
    ReservaLabDTO actualizar(Integer id, ReservaLabDTO dto);
    void eliminar(Integer id);
}
