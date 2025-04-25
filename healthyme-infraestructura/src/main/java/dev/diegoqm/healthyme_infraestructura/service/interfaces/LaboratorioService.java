package dev.diegoqm.healthyme_infraestructura.service.interfaces;

import dev.diegoqm.healthyme_infraestructura.dto.LaboratorioDTO;
import java.util.List;

public interface LaboratorioService {

    LaboratorioDTO createLaboratorio(LaboratorioDTO dto);
    LaboratorioDTO getLaboratorioById(int id);
    List<LaboratorioDTO> getAllLaboratorios();
    LaboratorioDTO updateLaboratorio(int id, LaboratorioDTO dto);
    void deleteLaboratorioById(int id);
}
