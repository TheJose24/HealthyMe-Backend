package studio.devbyjose.healthyme_payment.service.interfaces;

import studio.devbyjose.healthyme_commons.client.dto.FacturaDTO;
import studio.devbyjose.healthyme_payment.dto.CreateFacturaDTO;

public interface FacturaService {
    
    /**
     * Genera una nueva factura
     * @param createFacturaDTO datos de la factura
     * @return la factura generada
     */
    FacturaDTO createFactura(CreateFacturaDTO createFacturaDTO);
    
    /**
     * Obtiene una factura por su ID
     * @param idFactura identificador de la factura
     * @return la factura encontrada
     */
    FacturaDTO getFacturaById(Integer idFactura);
    
    /**
     * Obtiene una factura por su número
     * @param numeroFactura número de la factura
     * @return la factura encontrada
     */
    FacturaDTO getFacturaByNumero(String numeroFactura);
    
    /**
     * Obtiene una factura por el ID del pago asociado
     * @param idPago identificador del pago
     * @return la factura encontrada
     */
    FacturaDTO getFacturaByPago(Integer idPago);
    
    /**
     * Envía la factura por correo electrónico al cliente
     * @param idFactura identificador de la factura
     * @return true si se envió correctamente
     */
    boolean sendFacturaByEmail(Integer idFactura);
}