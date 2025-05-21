package studio.devbyjose.healthyme_payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studio.devbyjose.healthyme_payment.dto.CreateTransaccionDTO;
import studio.devbyjose.healthyme_payment.dto.TransaccionDTO;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.entity.Transaccion;
import studio.devbyjose.healthyme_payment.exception.PaymentException;
import studio.devbyjose.healthyme_payment.exception.PaymentNotFoundException;
import studio.devbyjose.healthyme_payment.mapper.TransaccionMapper;
import studio.devbyjose.healthyme_payment.repository.PagoRepository;
import studio.devbyjose.healthyme_payment.repository.TransaccionRepository;
import studio.devbyjose.healthyme_payment.service.interfaces.TransaccionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final PagoRepository pagoRepository;
    private final TransaccionMapper transaccionMapper;

    @Override
    @Transactional
    public TransaccionDTO createTransaccion(CreateTransaccionDTO createTransaccionDTO) {
        log.info("Creando transacción para pago con ID: {}", createTransaccionDTO.getIdPago());
        
        // Verificar si el pago existe
        Pago pago = pagoRepository.findById(createTransaccionDTO.getIdPago())
                .orElseThrow(() -> new PaymentNotFoundException(createTransaccionDTO.getIdPago()));
        
        try {
            // Crear la transacción
            Transaccion transaccion = new Transaccion();
            transaccion.setPago(pago);
            transaccion.setFechaTransaccion(LocalDateTime.now());
            transaccion.setReferenciaExterna(createTransaccionDTO.getReferenciaExterna());
            transaccion.setDatosTransaccion(createTransaccionDTO.getDatosTransaccion());
            
            Transaccion savedTransaccion = transaccionRepository.save(transaccion);
            log.info("Transacción creada exitosamente con ID: {}", savedTransaccion.getId());
            
            return transaccionMapper.toDto(savedTransaccion);
        } catch (Exception e) {
            log.error("Error al crear la transacción: {}", e.getMessage(), e);
            throw new PaymentException("Error al registrar la transacción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public TransaccionDTO getTransaccionById(Integer idTransaccion) {
        log.info("Obteniendo transacción con ID: {}", idTransaccion);
        
        Transaccion transaccion = transaccionRepository.findById(idTransaccion)
                .orElseThrow(() -> new NoSuchElementException("Transacción no encontrada con ID: " + idTransaccion));
                
        return transaccionMapper.toDto(transaccion);
    }

    @Override
    public List<TransaccionDTO> getTransaccionesByPago(Integer idPago) {
        log.info("Obteniendo transacciones para pago con ID: {}", idPago);
        
        // Verificar si el pago existe
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PaymentNotFoundException(idPago));
        
        // Obtener las transacciones
        List<Transaccion> transacciones = transaccionRepository.findByPago(pago);
        return transaccionMapper.toDtoList(transacciones);
    }

    @Override
    public TransaccionDTO getTransaccionByReferenciaExterna(String referenciaExterna) {
        log.info("Buscando transacción con referencia externa: {}", referenciaExterna);
        
        Transaccion transaccion = transaccionRepository.findByReferenciaExterna(referenciaExterna)
                .orElseThrow(() -> new NoSuchElementException("Transacción no encontrada con referencia externa: " + referenciaExterna));
                
        return transaccionMapper.toDto(transaccion);
    }
}