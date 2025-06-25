package studio.devbyjose.healthyme_payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studio.devbyjose.healthyme_commons.client.dto.FacturaDTO;
import studio.devbyjose.healthyme_commons.client.dto.PacienteDTO;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.feign.PacienteClient;
import studio.devbyjose.healthyme_commons.client.feign.UsuarioClient;
import studio.devbyjose.healthyme_payment.dto.CreateFacturaDTO;
import studio.devbyjose.healthyme_payment.entity.Factura;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.exception.FacturaNotFoundException;
import studio.devbyjose.healthyme_payment.exception.PaymentException;
import studio.devbyjose.healthyme_payment.exception.PaymentNotFoundException;
import studio.devbyjose.healthyme_payment.mapper.FacturaMapper;
import studio.devbyjose.healthyme_payment.repository.FacturaRepository;
import studio.devbyjose.healthyme_payment.repository.PagoRepository;
import studio.devbyjose.healthyme_payment.service.interfaces.FacturaService;
import studio.devbyjose.healthyme_payment.service.interfaces.NotificationService;

import java.util.Optional;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;
    private final FacturaMapper facturaMapper;
    private final NotificationService notificationService;
    private final PacienteClient pacienteClient;
    private final UsuarioClient usuarioClient;

    @Override
    @Transactional
    public FacturaDTO createFactura(CreateFacturaDTO createFacturaDTO) {
        log.info("Creando factura para pago con ID: {}", createFacturaDTO.getIdPago());
        
        // Verificar si el pago existe
        Pago pago = pagoRepository.findById(createFacturaDTO.getIdPago())
                .orElseThrow(() -> new PaymentNotFoundException(createFacturaDTO.getIdPago()));
        
        // Verificar si ya existe una factura para este pago
        Optional<Factura> facturaExistente = facturaRepository.findByPago(pago);
        if (facturaExistente.isPresent()) {
            log.warn("Ya existe una factura para el pago con ID: {}", createFacturaDTO.getIdPago());
            
            // Si ya existe, intentar enviar por email nuevamente
            try {
                sendFacturaByEmail(facturaExistente.get().getId());
            } catch (Exception e) {
                log.warn("Error al reenviar factura existente: {}", e.getMessage());
            }
            
            return facturaMapper.toDto(facturaExistente.get());
        }
        
        // Crear la factura
        Factura factura = facturaMapper.toEntity(createFacturaDTO);
        factura.setPago(pago);
        
        Factura savedFactura = facturaRepository.save(factura);
        log.info("Factura creada exitosamente con número: {}", savedFactura.getNumeroFactura());
        
        // Enviar automáticamente por email después de crear la factura
        try {
            boolean enviado = sendFacturaByEmail(savedFactura.getId());
            if (enviado) {
                log.info("Factura {} enviada exitosamente por email", savedFactura.getNumeroFactura());
            } else {
                log.warn("No se pudo enviar la factura {} por email", savedFactura.getNumeroFactura());
            }
        } catch (Exception e) {
            log.error("Error al enviar factura por email: {}", e.getMessage(), e);
            // No lanzamos excepción para no afectar la creación de la factura
        }
        
        return facturaMapper.toDto(savedFactura);
    }

    @Override
    public FacturaDTO getFacturaById(Integer idFactura) {
        log.info("Obteniendo factura con ID: {}", idFactura);
        
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new FacturaNotFoundException(idFactura));
                
        return facturaMapper.toDto(factura);
    }

    @Override
    public FacturaDTO getFacturaByNumero(String numeroFactura) {
        log.info("Buscando factura con número: {}", numeroFactura);
        
        Factura factura = facturaRepository.findByNumeroFactura(numeroFactura)
                .orElseThrow(() -> new FacturaNotFoundException("Factura no encontrada con número: " + numeroFactura));
                
        return facturaMapper.toDto(factura);
    }

    @Override
    public FacturaDTO getFacturaByPago(Integer idPago) {
        log.info("Buscando factura para pago con ID: {}", idPago);
        
        // Verificar si el pago existe
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PaymentNotFoundException(idPago));
        
        // Buscar la factura asociada al pago
        Factura factura = facturaRepository.findByPago(pago)
                .orElseThrow(() -> new FacturaNotFoundException("Factura no encontrada para el pago con ID: " + idPago));
                
        return facturaMapper.toDto(factura);
    }

    @Transactional
    public boolean sendFacturaByEmail(Integer idFactura) {
        log.info("Enviando factura por correo electrónico, ID: {}", idFactura);

        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new FacturaNotFoundException(idFactura));

        try {
            // Obtener el email del paciente
            String email = obtenerEmailPaciente(factura.getPago().getIdPaciente());

            // Enviar la factura usando el servicio
            return notificationService.enviarFacturaPorEmail(factura, email);
        } catch (Exception e) {
            log.error("Error al enviar la factura por correo: {}", e.getMessage(), e);
            throw new PaymentException("Error al enviar la factura por correo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String obtenerEmailPaciente(Long idPaciente) {
        log.info("Obteniendo email para paciente ID: {}", idPaciente);

        try {
            // 1. Obtener el paciente por su ID
            ResponseEntity<PacienteDTO> pacienteResponse = pacienteClient.findPacienteById(String.valueOf(idPaciente));

            if (!pacienteResponse.getStatusCode().is2xxSuccessful() || pacienteResponse.getBody() == null) {
                throw new PaymentException("No se pudo obtener datos del paciente con ID: " + idPaciente,
                        HttpStatus.NOT_FOUND);
            }

            Long idUsuario = pacienteResponse.getBody().getIdUsuario();
            log.info("Obtenido ID de usuario: {} para paciente ID: {}", idUsuario, idPaciente);

            // 2. Obtener el email del usuario
            ResponseEntity<UsuarioDTO> emailResponse = usuarioClient.obtenerUsuario(idUsuario.intValue());

            if (!emailResponse.getStatusCode().is2xxSuccessful() || emailResponse.getBody() == null) {
                throw new PaymentException("No se pudo obtener email del usuario con ID: " + idUsuario,
                        HttpStatus.NOT_FOUND);
            }

            String email = emailResponse.getBody().getPersona().getEmail();
            log.info("Email obtenido para paciente ID {}: {}", idPaciente, email);

            return email;
        } catch (Exception e) {
            log.error("Error al obtener email del paciente: {}", e.getMessage(), e);
            throw new PaymentException("Error al obtener email del paciente: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}