package studio.devbyjose.healthyme_payment.service.interfaces;

import studio.devbyjose.healthyme_payment.dto.CreateTransaccionDTO;
import studio.devbyjose.healthyme_payment.dto.TransaccionDTO;

import java.util.List;

public interface TransaccionService {
    
    /**
     * Registra una nueva transacción
     * @param createTransaccionDTO datos de la transacción
     * @return la transacción registrada
     */
    TransaccionDTO createTransaccion(CreateTransaccionDTO createTransaccionDTO);
    
    /**
     * Obtiene una transacción por su ID
     * @param idTransaccion identificador de la transacción
     * @return la transacción encontrada
     */
    TransaccionDTO getTransaccionById(Integer idTransaccion);
    
    /**
     * Obtiene transacciones por pago
     * @param idPago identificador del pago
     * @return lista de transacciones asociadas al pago
     */
    List<TransaccionDTO> getTransaccionesByPago(Integer idPago);
    
    /**
     * Obtiene una transacción por su referencia externa
     * @param referenciaExterna referencia externa de la transacción
     * @return la transacción encontrada
     */
    TransaccionDTO getTransaccionByReferenciaExterna(String referenciaExterna);
}