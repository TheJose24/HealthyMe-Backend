package studio.devbyjose.healthyme_pacientes.service.interfaces;

import studio.devbyjose.healthyme_pacientes.dto.TriajeDTO;

import java.util.List;

/**
 * Servicio para gestionar los triajes médicos
 */
public interface TriajeService {
    
    /**
     * Obtiene todos los triajes registrados
     * 
     * @return Lista de TriajeDTO
     */
    List<TriajeDTO> findAll();
    
    /**
     * Busca un triaje por su ID
     * 
     * @param id ID del triaje a buscar
     * @return TriajeDTO encontrado
     * @throws RuntimeException si no se encuentra el triaje
     */
    TriajeDTO findById(Long id);
    
    /**
     * Obtiene todos los triajes de un paciente ordenados por fecha y hora (más recientes primero)
     * 
     * @param idPaciente ID del paciente
     * @return Lista de TriajeDTO del paciente
     */
    List<TriajeDTO> findByPaciente(Long idPaciente);
    
    /**
     * Crea un nuevo triaje
     * 
     * @param triajeDTO Datos del triaje a crear
     * @return TriajeDTO creado
     * @throws RuntimeException si no se encuentra el paciente asociado
     */
    TriajeDTO create(TriajeDTO triajeDTO);
    
    /**
     * Actualiza un triaje existente
     * 
     * @param id ID del triaje a actualizar
     * @param triajeDTO Datos actualizados del triaje
     * @return TriajeDTO actualizado
     * @throws RuntimeException si no se encuentra el triaje o el paciente asociado
     */
    TriajeDTO update(Long id, TriajeDTO triajeDTO);
    
    /**
     * Elimina un triaje
     * 
     * @param id ID del triaje a eliminar
     * @throws RuntimeException si no se encuentra el triaje
     */
    void delete(Long id);
}