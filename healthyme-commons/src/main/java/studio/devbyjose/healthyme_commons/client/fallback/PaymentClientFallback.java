package studio.devbyjose.healthyme_commons.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.CreatePagoDTO;
import studio.devbyjose.healthyme_commons.client.dto.FacturaDTO;
import studio.devbyjose.healthyme_commons.client.dto.PagoDTO;
import studio.devbyjose.healthyme_commons.client.feign.PaymentClient;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class PaymentClientFallback implements PaymentClient {
    
    @Override
    public ResponseEntity<PagoDTO> crearPago(CreatePagoDTO createPagoDTO) {
        log.error("Error al crear pago: {}", createPagoDTO);
        return ResponseEntity.internalServerError().build();
    }
    
    @Override
    public ResponseEntity<PagoDTO> getPagoById(Integer id) {
        log.error("Error al obtener pago con ID: {}", id);
        return ResponseEntity.notFound().build();
    }
    
    @Override
    public ResponseEntity<List<PagoDTO>> getPagosByPaciente(Integer idPaciente) {
        log.error("Error al obtener pagos del paciente con ID: {}", idPaciente);
        return ResponseEntity.ok(Collections.emptyList());
    }
    
    @Override
    public ResponseEntity<PagoDTO> procesarPago(Integer id) {
        log.error("Error al procesar pago con ID: {}", id);
        return ResponseEntity.internalServerError().build();
    }
    
    @Override
    public ResponseEntity<PagoDTO> actualizarEstadoPago(Integer id, String estado) {
        log.error("Error al actualizar estado del pago con ID: {} a {}", id, estado);
        return ResponseEntity.internalServerError().build();
    }
    
    @Override
    public ResponseEntity<PagoDTO> getPagoByReferencia(String entidad, Integer idReferencia) {
        log.error("Error al obtener pago por referencia: {} - {}", entidad, idReferencia);
        return ResponseEntity.notFound().build();
    }
    
    @Override
    public ResponseEntity<FacturaDTO> getFacturaByPago(Integer idPago) {
        log.error("Error al obtener factura para pago con ID: {}", idPago);
        return ResponseEntity.notFound().build();
    }
    
    @Override
    public ResponseEntity<String> enviarFacturaPorEmail(Integer id) {
        log.error("Error al enviar factura por email, ID: {}", id);
        return ResponseEntity.internalServerError().body("No se pudo enviar la factura por correo electrónico");
    }
}