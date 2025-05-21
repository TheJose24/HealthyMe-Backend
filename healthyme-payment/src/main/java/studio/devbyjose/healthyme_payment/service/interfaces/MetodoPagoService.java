package studio.devbyjose.healthyme_payment.service.interfaces;

import studio.devbyjose.healthyme_commons.client.dto.MetodoPagoDTO;
import studio.devbyjose.healthyme_payment.dto.CreateMetodoPagoDTO;

import java.util.List;

public interface MetodoPagoService {
    
    /**
     * Crea un nuevo método de pago
     * @param createMetodoPagoDTO datos del método de pago
     * @return el método de pago creado
     */
    MetodoPagoDTO createMetodoPago(CreateMetodoPagoDTO createMetodoPagoDTO);
    
    /**
     * Obtiene un método de pago por su ID
     * @param id identificador del método de pago
     * @return el método de pago encontrado
     */
    MetodoPagoDTO getMetodoPagoById(Integer id);
    
    /**
     * Obtiene todos los métodos de pago activos
     * @return lista de métodos de pago activos
     */
    List<MetodoPagoDTO> getAllMetodoPagosActivos();
    
    /**
     * Obtiene todos los métodos de pago
     * @return lista de todos los métodos de pago
     */
    List<MetodoPagoDTO> getAllMetodoPagos();
    
    /**
     * Actualiza el estado de un método de pago
     * @param id identificador del método de pago
     * @param estado nuevo estado
     * @return el método de pago actualizado
     */
    MetodoPagoDTO updateEstadoMetodoPago(Integer id, Boolean estado);
}