package dev.diegoqm.healthyme_infraestructura.service.interfaces;

import dev.diegoqm.healthyme_infraestructura.dto.ConsultorioDTO;
import java.util.List;

public interface ConsultorioService {

    ConsultorioDTO createConsultorio(ConsultorioDTO dto);
    ConsultorioDTO getConsultorioById(int id);
    List<ConsultorioDTO> getAllConsultorios();
    ConsultorioDTO updateConsultorio(int id, ConsultorioDTO dto);
    void deleteConsultorioById(int id);
}