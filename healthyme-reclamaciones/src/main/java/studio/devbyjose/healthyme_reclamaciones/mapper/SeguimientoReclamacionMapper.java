package studio.devbyjose.healthyme_reclamaciones.mapper;

import org.mapstruct.*;
import studio.devbyjose.healthyme_reclamaciones.dto.SeguimientoReclamacionDTO;
import studio.devbyjose.healthyme_reclamaciones.entity.SeguimientoReclamacion;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeguimientoReclamacionMapper {
    
    @Mapping(target = "reclamacionId", source = "reclamacion.id")
    @Mapping(target = "numeroReclamacion", source = "reclamacion.numeroReclamacion")
    SeguimientoReclamacionDTO toDTO(SeguimientoReclamacion seguimiento);
    
    List<SeguimientoReclamacionDTO> toDTOList(List<SeguimientoReclamacion> seguimientos);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCambio", ignore = true)
    @Mapping(target = "reclamacion", ignore = true)
    SeguimientoReclamacion toEntity(SeguimientoReclamacionDTO dto);
}