package studio.devbyjose.healthyme_pacientes.service.interfaces;

import studio.devbyjose.healthyme_pacientes.dto.HistorialMedicoDTO;

import java.util.List;

/**
 * Servicio para gestionar los historiales médicos
 */
public interface HistorialMedicoService {
    
    /**
     * Obtiene todos los historiales médicos registrados
     * 
     * @return Lista de HistorialMedicoDTO
     */
    List<HistorialMedicoDTO> findAll();
    
    /**
     * Busca un historial médico por su ID
     * 
     * @param id ID del historial médico a buscar
     * @return HistorialMedicoDTO encontrado
     * @throws RuntimeException si no se encuentra el historial
     */
    HistorialMedicoDTO findById(Long id);
    
    /**
     * Obtiene todos los historiales médicos de un paciente
     * 
     * @param idPaciente ID del paciente
     * @return Lista de HistorialMedicoDTO del paciente
     */
    List<HistorialMedicoDTO> findByPaciente(Long idPaciente);
    
    /**
     * Crea un nuevo historial médico
     * 
     * @param historialDTO Datos del historial médico a crear
     * @return HistorialMedicoDTO creado
     * @throws RuntimeException si no se encuentra el paciente o triaje asociado
     */
    HistorialMedicoDTO create(HistorialMedicoDTO historialDTO);
    
    /**
     * Actualiza un historial médico existente
     * 
     * @param id ID del historial médico a actualizar
     * @param historialDTO Datos actualizados del historial médico
     * @return HistorialMedicoDTO actualizado
     * @throws RuntimeException si no se encuentra el historial médico
     */
    HistorialMedicoDTO update(Long id, HistorialMedicoDTO historialDTO);
    
    /**
     * Elimina un historial médico
     * 
     * @param id ID del historial médico a eliminar
     * @throws RuntimeException si no se encuentra el historial médico
     */
    void delete(Long id);
}