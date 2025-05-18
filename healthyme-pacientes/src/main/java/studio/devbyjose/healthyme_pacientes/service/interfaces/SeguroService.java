package studio.devbyjose.healthyme_pacientes.service.interfaces;

import studio.devbyjose.healthyme_pacientes.dto.SeguroDTO;

import java.util.List;

/**
 * Servicio para gestionar los seguros médicos
 */
public interface SeguroService {
    
    /**
     * Obtiene todos los seguros médicos registrados
     * 
     * @return Lista de SeguroDTO
     */
    List<SeguroDTO> findAll();
    
    /**
     * Busca un seguro médico por su ID
     * 
     * @param id ID del seguro a buscar
     * @return SeguroDTO encontrado
     * @throws RuntimeException si no se encuentra el seguro
     */
    SeguroDTO findById(Long id);
    
    /**
     * Crea un nuevo seguro médico
     * 
     * @param seguroDTO Datos del seguro a crear
     * @return SeguroDTO creado
     */
    SeguroDTO create(SeguroDTO seguroDTO);
    
    /**
     * Actualiza un seguro médico existente
     * 
     * @param id ID del seguro a actualizar
     * @param seguroDTO Datos actualizados del seguro
     * @return SeguroDTO actualizado
     * @throws RuntimeException si no se encuentra el seguro
     */
    SeguroDTO update(Long id, SeguroDTO seguroDTO);
    
    /**
     * Elimina un seguro médico
     * 
     * @param id ID del seguro a eliminar
     * @throws RuntimeException si no se encuentra el seguro
     */
    void delete(Long id);
}