package studio.devbyjose.healthyme_reclamaciones.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AdjuntoReclamacionException extends RuntimeException {
    
    private final String errorCode;
    
    public AdjuntoReclamacionException(String message) {
        super(message);
        this.errorCode = "ADJUNTO_ERROR";
    }
    
    public AdjuntoReclamacionException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public AdjuntoReclamacionException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "ADJUNTO_ERROR";
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    // Métodos estáticos para casos específicos
    public static AdjuntoReclamacionException archivoMuyGrande(String nombreArchivo, long tamanoMaximo) {
        return new AdjuntoReclamacionException(
            "El archivo '" + nombreArchivo + "' excede el tamaño máximo permitido de " + 
            formatearTamano(tamanoMaximo),
            "ARCHIVO_MUY_GRANDE"
        );
    }
    
    public static AdjuntoReclamacionException tipoArchivoNoPermitido(String nombreArchivo, String tipoContenido) {
        return new AdjuntoReclamacionException(
            "El tipo de archivo '" + tipoContenido + "' del archivo '" + nombreArchivo + "' no está permitido",
            "TIPO_ARCHIVO_NO_PERMITIDO"
        );
    }
    
    public static AdjuntoReclamacionException errorAlGuardar(String nombreArchivo, String motivo) {
        return new AdjuntoReclamacionException(
            "Error al guardar el archivo '" + nombreArchivo + "': " + motivo,
            "ERROR_GUARDAR_ARCHIVO"
        );
    }
    
    public static AdjuntoReclamacionException archivoNoEncontrado(String nombreArchivo) {
        return new AdjuntoReclamacionException(
            "No se encontró el archivo: " + nombreArchivo,
            "ARCHIVO_NO_ENCONTRADO"
        );
    }
    
    public static AdjuntoReclamacionException limiteCantidadExcedido(int cantidadActual, int limite) {
        return new AdjuntoReclamacionException(
            "Se ha excedido el límite de archivos adjuntos. Actual: " + cantidadActual + ", Límite: " + limite,
            "LIMITE_CANTIDAD_EXCEDIDO"
        );
    }
    
    private static String formatearTamano(long bytes) {
        String[] unidades = {"B", "KB", "MB", "GB"};
        int unidadIndex = 0;
        double tamano = bytes;
        
        while (tamano >= 1024 && unidadIndex < unidades.length - 1) {
            tamano /= 1024;
            unidadIndex++;
        }
        
        return String.format("%.2f %s", tamano, unidades[unidadIndex]);
    }
}