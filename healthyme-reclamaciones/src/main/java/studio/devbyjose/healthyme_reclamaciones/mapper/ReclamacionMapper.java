package studio.devbyjose.healthyme_reclamaciones.mapper;

import org.mapstruct.*;
import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.entity.Reclamacion;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring", 
        uses = {RespuestaReclamacionMapper.class,
                SeguimientoReclamacionMapper.class, AdjuntoReclamacionMapper.class})
public interface ReclamacionMapper {

    @Mapping(target = "esVencida", expression = "java(calcularEsVencida(reclamacion))")
    @Mapping(target = "diasParaVencer", expression = "java(calcularDiasParaVencer(reclamacion))")
    @Mapping(target = "totalRespuestas", expression = "java(calcularTotalRespuestas(reclamacion))")
    @Mapping(target = "totalAdjuntos", expression = "java(calcularTotalAdjuntos(reclamacion))")
    ReclamacionDTO toDTO(Reclamacion reclamacion);
    
    List<ReclamacionDTO> toDTOList(List<Reclamacion> reclamaciones);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroReclamacion", ignore = true)
    @Mapping(target = "numeroHoja", ignore = true)
    @Mapping(target = "fechaReclamacion", ignore = true)
    @Mapping(target = "estado", constant = "RECIBIDO")
    @Mapping(target = "asignadoA", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "respuestas", ignore = true)
    @Mapping(target = "seguimientos", ignore = true)
    @Mapping(target = "adjuntos", ignore = true)
    Reclamacion toEntity(CreateReclamacionDTO createDTO);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroReclamacion", ignore = true)
    @Mapping(target = "numeroHoja", ignore = true)
    @Mapping(target = "fechaReclamacion", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "respuestas", ignore = true)
    @Mapping(target = "seguimientos", ignore = true)
    @Mapping(target = "adjuntos", ignore = true)
    void updateEntity(UpdateReclamacionDTO updateDTO, @MappingTarget Reclamacion reclamacion);
    
    @Mapping(target = "tipoMotivo", expression = "java(reclamacion.getTipoMotivo().name())")
    @Mapping(target = "canalRecepcion", expression = "java(reclamacion.getCanalRecepcion().getDescripcion())")
    @Mapping(target = "estado", expression = "java(reclamacion.getEstado().getDescripcion())")
    @Mapping(target = "prioridad", expression = "java(reclamacion.getPrioridad().getDescripcion())")
    @Mapping(target = "esVencida", expression = "java(calcularEsVencida(reclamacion))")
    @Mapping(target = "diasVencidos", expression = "java(calcularDiasVencidos(reclamacion))")
    @Mapping(target = "totalRespuestas", expression = "java(calcularTotalRespuestas(reclamacion))")
    @Mapping(target = "descripcionResumida", expression = "java(getDescripcionResumida(reclamacion))")
    ReclamacionReporteDTO toReporteDTO(Reclamacion reclamacion);
    
    List<ReclamacionReporteDTO> toReporteDTOList(List<Reclamacion> reclamaciones);
    
    // Métodos de utilidad
    default Boolean calcularEsVencida(Reclamacion reclamacion) {
        if (!reclamacion.getRequiereRespuesta() || 
            reclamacion.getFechaLimiteRespuesta() == null ||
            reclamacion.getEstado().name().matches("RESUELTO|CERRADO|ANULADO")) {
            return false;
        }
        return LocalDateTime.now().isAfter(reclamacion.getFechaLimiteRespuesta());
    }
    
    default Long calcularDiasParaVencer(Reclamacion reclamacion) {
        if (!reclamacion.getRequiereRespuesta() || 
            reclamacion.getFechaLimiteRespuesta() == null ||
            reclamacion.getEstado().name().matches("RESUELTO|CERRADO|ANULADO")) {
            return null;
        }
        long dias = ChronoUnit.DAYS.between(LocalDateTime.now(), reclamacion.getFechaLimiteRespuesta());
        return dias;
    }
    
    default Integer calcularDiasVencidos(Reclamacion reclamacion) {
        if (!calcularEsVencida(reclamacion)) {
            return null;
        }
        return Math.toIntExact(ChronoUnit.DAYS.between(reclamacion.getFechaLimiteRespuesta(), LocalDateTime.now()));
    }
    
    default Integer calcularTotalRespuestas(Reclamacion reclamacion) {
        return reclamacion.getRespuestas() != null ? reclamacion.getRespuestas().size() : 0;
    }
    
    default Integer calcularTotalAdjuntos(Reclamacion reclamacion) {
        return reclamacion.getAdjuntos() != null ? reclamacion.getAdjuntos().size() : 0;
    }
    
    default String getDescripcionResumida(Reclamacion reclamacion) {
        if (reclamacion.getDescripcion() == null) return null;
        return reclamacion.getDescripcion().length() > 100 ? 
               reclamacion.getDescripcion().substring(0, 100) + "..." : 
               reclamacion.getDescripcion();
    }
}
