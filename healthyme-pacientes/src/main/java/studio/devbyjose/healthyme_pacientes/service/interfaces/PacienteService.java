package studio.devbyjose.healthyme_pacientes.service.interfaces;

import studio.devbyjose.healthyme_pacientes.dto.PacienteDTO;

import java.util.List;

/**
 * Servicio para gestionar los pacientes
 */
public interface PacienteService {
    
    /**
     * Obtiene todos los pacientes registrados
     * 
     * @return Lista de PacienteDTO
     */
    List<PacienteDTO> findAll();
    
    /**
     * Busca un paciente por su ID
     * 
     * @param id ID del paciente a buscar
     * @return PacienteDTO encontrado
     * @throws RuntimeException si no se encuentra el paciente
     */
    PacienteDTO findById(Long id);
    
    /**
     * Busca un paciente por su ID de usuario
     * 
     * @param idUsuario ID de usuario asociado
     * @return PacienteDTO encontrado
     * @throws RuntimeException si no se encuentra el paciente
     */
    PacienteDTO findByIdUsuario(Long idUsuario);
    
    /**
     * Crea un nuevo paciente
     * 
     * @param pacienteDTO Datos del paciente a crear
     * @return PacienteDTO creado
     * @throws RuntimeException si ya existe un paciente con ese ID de usuario
     */
    PacienteDTO create(PacienteDTO pacienteDTO);
    
    /**
     * Actualiza un paciente existente
     * 
     * @param id ID del paciente a actualizar
     * @param pacienteDTO Datos actualizados del paciente
     * @return PacienteDTO actualizado
     * @throws RuntimeException si no se encuentra el paciente
     */
    PacienteDTO update(Long id, PacienteDTO pacienteDTO);
    
    /**
     * Elimina un paciente
     * 
     * @param id ID del paciente a eliminar
     * @throws RuntimeException si no se encuentra el paciente
     */
    void delete(Long id);
}