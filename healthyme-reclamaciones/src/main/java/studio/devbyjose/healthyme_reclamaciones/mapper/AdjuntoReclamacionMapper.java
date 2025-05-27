package studio.devbyjose.healthyme_reclamaciones.mapper;

import org.mapstruct.*;
import studio.devbyjose.healthyme_reclamaciones.dto.AdjuntoReclamacionDTO;
import studio.devbyjose.healthyme_reclamaciones.entity.AdjuntoReclamacion;

import java.text.DecimalFormat;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AdjuntoReclamacionMapper {
    
    @Mapping(target = "reclamacionId", source = "reclamacion.id")
    @Mapping(target = "numeroReclamacion", source = "reclamacion.numeroReclamacion")
    @Mapping(target = "tamanoFormateado", expression = "java(formatearTamano(adjunto.getTamanoArchivo()))")
    @Mapping(target = "urlDescarga", expression = "java(generarUrlDescarga(adjunto))")
    AdjuntoReclamacionDTO toDTO(AdjuntoReclamacion adjunto);
    
    List<AdjuntoReclamacionDTO> toDTOList(List<AdjuntoReclamacion> adjuntos);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaSubida", ignore = true)
    @Mapping(target = "reclamacion", ignore = true)
    AdjuntoReclamacion toEntity(AdjuntoReclamacionDTO dto);
    
    // Métodos de utilidad
    default String formatearTamano(Long tamanoBytes) {
        if (tamanoBytes == null) return "0 B";
        
        String[] unidades = {"B", "KB", "MB", "GB"};
        int unidadIndex = 0;
        double tamano = tamanoBytes.doubleValue();
        
        while (tamano >= 1024 && unidadIndex < unidades.length - 1) {
            tamano /= 1024;
            unidadIndex++;
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(tamano) + " " + unidades[unidadIndex];
    }
    
    default String generarUrlDescarga(AdjuntoReclamacion adjunto) {
        return "/api/reclamaciones/" + adjunto.getReclamacion().getId() + 
               "/adjuntos/" + adjunto.getId() + "/download";
    }
}