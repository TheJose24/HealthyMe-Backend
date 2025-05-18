package studio.devbyjose.healthyme_pacientes.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import studio.devbyjose.healthyme_pacientes.dto.PacienteDTO;
import studio.devbyjose.healthyme_pacientes.entity.Paciente;
import studio.devbyjose.healthyme_pacientes.entity.Seguro;
import studio.devbyjose.healthyme_pacientes.exception.SeguroNotFoundException;
import studio.devbyjose.healthyme_pacientes.repository.SeguroRepository;

@Mapper(componentModel = "spring", uses = {SeguroMapper.class})
public abstract class PacienteMapper {

    @Autowired
    private SeguroRepository seguroRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seguro", source = "seguro.id", qualifiedByName = "idToSeguro")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "ultimaModificacion", ignore = true)
    public abstract Paciente toEntity(PacienteDTO dto);

    @Mapping(target = "seguro", source = "seguro")
    public abstract PacienteDTO toDTO(Paciente entity);

    @Named("idToSeguro")
    protected Seguro idToSeguro(Long idSeguro) {
        if (idSeguro == null) {
            return null;
        }
        return seguroRepository.findById(idSeguro)
                .orElseThrow(() -> new SeguroNotFoundException(
                        "No se encontró seguro con el id: " + idSeguro,
                        HttpStatus.NOT_FOUND));
    }

    @AfterMapping
    protected void mapSeguroId(PacienteDTO source, @MappingTarget Paciente target) {
        if (source.getSeguro() == null) {
            target.setSeguro(null);
        }
    }
}