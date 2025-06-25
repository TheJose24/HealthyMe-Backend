package studio.devbyjose.healthyme_payment.service.interfaces;

import studio.devbyjose.healthyme_commons.client.dto.CreatePagoDTO;
import studio.devbyjose.healthyme_commons.client.dto.PagoDTO;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.payment.EstadoPago;
import studio.devbyjose.healthyme_payment.dto.HistorialPagoDTO;
import studio.devbyjose.healthyme_payment.dto.PagoResponseDTO;

import java.util.List;

public interface PagoService {
    
    /**
     * Crea un nuevo pago en estado PENDIENTE
     * @param createPagoDTO datos del pago
     * @return el pago creado
     */
    PagoDTO createPago(CreatePagoDTO createPagoDTO);
    
    /**
     * Procesa un pago y lo marca como COMPLETADO si es exitoso
     * @param idPago identificador del pago
     * @return el pago procesado con información completa
     */
    PagoResponseDTO processPago(Integer idPago);
    
    /**
     * Obtiene un pago por su ID con información completa
     * @param idPago identificador del pago
     * @return el pago encontrado con información completa (incluye factura y transacciones)
     */
    PagoResponseDTO getPagoById(Integer idPago);
    
    /**
     * Obtiene pagos por paciente
     * @param idPaciente identificador del paciente
     * @return lista de pagos del paciente
     */
    List<PagoDTO> getPagosByPaciente(Integer idPaciente);
    
    /**
     * Obtiene pagos por estado
     * @param estado estado del pago
     * @return lista de pagos con el estado indicado
     */
    List<PagoDTO> getPagosByEstado(EstadoPago estado);
    
    /**
     * Actualiza el estado de un pago
     * @param idPago identificador del pago
     * @param estado nuevo estado
     * @return el pago actualizado
     */
    PagoDTO updateEstadoPago(Integer idPago, EstadoPago estado);
    
    /**
     * Obtiene un pago por entidad y referencia
     * @param entidadReferencia tipo de entidad (CITA, EXAMEN, CONSULTA)
     * @param idReferencia identificador de la entidad
     * @return el pago encontrado
     */
    PagoDTO getPagoByReferencia(EntidadOrigen entidadReferencia, Integer idReferencia);

    List<HistorialPagoDTO> getHistorialPagos();
}