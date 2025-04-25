package dev.diegoqm.healthyme_infraestructura.service.interfaces;

import dev.diegoqm.healthyme_infraestructura.dto.SedeDTO;
import java.util.List;

public interface SedeService {

    SedeDTO createSede(SedeDTO sedeDTO);
    SedeDTO getSedeById(int id);
    List<SedeDTO> getAllSedes();
    SedeDTO updateSede(int id, SedeDTO sedeDTO);
    void deleteSedeById(int id);
}
