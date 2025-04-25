package dev.diegoqm.healthyme_infraestructura.service.interfaces;

import dev.diegoqm.healthyme_infraestructura.dto.HorarioTrabajoDTO;
import java.util.List;

public interface HorarioTrabajoService {

    HorarioTrabajoDTO createHorarioTrabajo(HorarioTrabajoDTO horarioTrabajoDTO);
    HorarioTrabajoDTO getHorarioTrabajoById(int id);
    List<HorarioTrabajoDTO> getAllHorarioTrabajo();
    HorarioTrabajoDTO updateHorarioTrabajo(int id, HorarioTrabajoDTO horarioTrabajoDTO);
    void deleteHorarioTrabajoById(int id);
}