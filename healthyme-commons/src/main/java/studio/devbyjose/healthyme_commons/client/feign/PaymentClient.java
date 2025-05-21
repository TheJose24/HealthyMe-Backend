package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_commons.client.dto.CreatePagoDTO;
import studio.devbyjose.healthyme_commons.client.dto.FacturaDTO;
import studio.devbyjose.healthyme_commons.client.dto.PagoDTO;
import studio.devbyjose.healthyme_commons.client.fallback.PaymentClientFallback;

import java.util.List;

@FeignClient(name = "healthyme-payment",
             path = "/payment/api",
             fallback = PaymentClientFallback.class)
public interface PaymentClient {
    
    @PostMapping("/pagos")
    ResponseEntity<PagoDTO> crearPago(@RequestBody CreatePagoDTO createPagoDTO);
    
    @GetMapping("/pagos/{id}")
    ResponseEntity<PagoDTO> getPagoById(@PathVariable("id") Integer id);
    
    @GetMapping("/pagos/paciente/{idPaciente}")
    ResponseEntity<List<PagoDTO>> getPagosByPaciente(@PathVariable("idPaciente") Integer idPaciente);
    
    @PostMapping("/pagos/{id}/procesar")
    ResponseEntity<PagoDTO> procesarPago(@PathVariable("id") Integer id);
    
    @PatchMapping("/pagos/{id}/estado")
    ResponseEntity<PagoDTO> actualizarEstadoPago(
            @PathVariable("id") Integer id,
            @RequestParam("estado") String estado);
    
    @GetMapping("/pagos/referencia")
    ResponseEntity<PagoDTO> getPagoByReferencia(
            @RequestParam("entidad") String entidad,
            @RequestParam("idReferencia") Integer idReferencia);
    
    @GetMapping("/facturas/pago/{idPago}")
    ResponseEntity<FacturaDTO> getFacturaByPago(@PathVariable("idPago") Integer idPago);
    
    @PostMapping("/facturas/{id}/enviar-email")
    ResponseEntity<String> enviarFacturaPorEmail(@PathVariable("id") Integer id);
}