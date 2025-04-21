package dev.choco.healthyme_laboratorio.service.implementacion;

import dev.choco.healthyme_laboratorio.dto.ReservaLabDTO;
import dev.choco.healthyme_laboratorio.entity.ReservaLab;
import dev.choco.healthyme_laboratorio.repository.ReservaLabRepository;
import dev.choco.healthyme_laboratorio.service.Interfaces.ReservaLabService;
import dev.choco.healthyme_laboratorio.util.ReservaLabMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaLabServiceImpl implements ReservaLabService {
    private final ReservaLabRepository repository;
    private final ReservaLabMapper mapper;

    @Override
    public List<ReservaLabDTO> listar() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReservaLabDTO guardar(ReservaLabDTO dto) {
        ReservaLab entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public ReservaLabDTO buscarPorId(Integer id) {
        ReservaLab reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        return mapper.toDTO(reserva);
    }

    @Override
    public ReservaLabDTO actualizar(Integer id, ReservaLabDTO dto) {
        ReservaLab reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setFecha(dto.getFecha());
        reserva.setHora(dto.getHora());
        reserva.setEstado(ReservaLab.EstadoReserva.valueOf(dto.getEstado()));
        reserva.setIdPaciente(dto.getIdPaciente());
        reserva.setIdTecnico(dto.getIdTecnico());
        reserva.setIdLaboratorio(dto.getIdLaboratorio());

        return mapper.toDTO(repository.save(reserva));
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }
}
