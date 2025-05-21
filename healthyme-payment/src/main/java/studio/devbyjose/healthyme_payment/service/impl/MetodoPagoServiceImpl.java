package studio.devbyjose.healthyme_payment.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_commons.client.dto.MetodoPagoDTO;
import studio.devbyjose.healthyme_payment.dto.CreateMetodoPagoDTO;
import studio.devbyjose.healthyme_payment.entity.MetodoPago;
import studio.devbyjose.healthyme_payment.exception.MetodoPagoNotFoundException;
import studio.devbyjose.healthyme_payment.mapper.MetodoPagoMapper;
import studio.devbyjose.healthyme_payment.repository.MetodoPagoRepository;
import studio.devbyjose.healthyme_payment.service.interfaces.MetodoPagoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;
    private final MetodoPagoMapper metodoPagoMapper;

    @Override
    public MetodoPagoDTO createMetodoPago(CreateMetodoPagoDTO createMetodoPagoDTO) {
        MetodoPago metodoPago = metodoPagoMapper.toEntity(createMetodoPagoDTO);
        MetodoPago savedMetodoPago = metodoPagoRepository.save(metodoPago);
        return metodoPagoMapper.toDto(savedMetodoPago);
    }

    @Override
    public MetodoPagoDTO getMetodoPagoById(Integer id) {
        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new MetodoPagoNotFoundException(id));
        return metodoPagoMapper.toDto(metodoPago);
    }

    @Override
    public List<MetodoPagoDTO> getAllMetodoPagosActivos() {
        List<MetodoPago> metodoPagos = metodoPagoRepository.findByEstadoTrue();
        return metodoPagoMapper.toDtoList(metodoPagos);
    }

    @Override
    public List<MetodoPagoDTO> getAllMetodoPagos() {
        List<MetodoPago> metodoPagos = metodoPagoRepository.findAll();
        return metodoPagoMapper.toDtoList(metodoPagos);
    }

    @Override
    public MetodoPagoDTO updateEstadoMetodoPago(Integer id, Boolean estado) {
        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new MetodoPagoNotFoundException(id));

        metodoPago.setEstado(estado);
        MetodoPago updatedMetodoPago = metodoPagoRepository.save(metodoPago);
        return metodoPagoMapper.toDto(updatedMetodoPago);
    }
}