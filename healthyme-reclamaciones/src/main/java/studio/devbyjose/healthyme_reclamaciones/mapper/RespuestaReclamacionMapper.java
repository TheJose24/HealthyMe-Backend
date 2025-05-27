package studio.devbyjose.healthyme_reclamaciones.mapper;

import org.mapstruct.*;
import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.entity.RespuestaReclamacion;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RespuestaReclamacionMapper {
    
    @Mapping(target = "reclamacionId", source = "reclamacion.id")
    @Mapping(target = "numeroReclamacion", source = "reclamacion.numeroReclamacion")
    RespuestaReclamacionDTO toDTO(RespuestaReclamacion respuesta);
    
    List<RespuestaReclamacionDTO> toDTOList(List<RespuestaReclamacion> respuestas);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRespuesta", ignore = true)
    @Mapping(target = "notificadoCliente", ignore = true)
    @Mapping(target = "fechaNotificacion", ignore = true)
    @Mapping(target = "reclamacion", ignore = true)
    RespuestaReclamacion toEntity(CreateRespuestaDTO createDTO);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reclamacion", ignore = true)
    @Mapping(target = "fechaRespuesta", ignore = true)
    void updateEntity(CreateRespuestaDTO updateDTO, @MappingTarget RespuestaReclamacion respuesta);
}
