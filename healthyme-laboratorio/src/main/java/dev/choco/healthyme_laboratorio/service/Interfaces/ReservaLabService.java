package dev.choco.healthyme_laboratorio.service.Interfaces;

import dev.choco.healthyme_laboratorio.dto.ReservaLabDTO;
import dev.choco.healthyme_laboratorio.entity.ReservaLab;
import jakarta.validation.Valid;

import java.util.List;

public interface ReservaLabService {
    List<ReservaLabDTO> listar();
    ReservaLabDTO guardar(ReservaLabDTO dto);
    ReservaLabDTO buscarPorId(Integer id);
    ReservaLabDTO actualizar(Integer id,@Valid ReservaLabDTO dto);
    Long countReservas();
    void eliminar(Integer id);

}
