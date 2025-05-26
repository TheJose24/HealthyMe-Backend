package studio.devbyjose.healthyme_reclamaciones.exception;

/**
 * Excepción lanzada cuando no se encuentra una reclamación
 */
public class ReclamacionNotFoundException extends RuntimeException {
    
    private static final String ENTITY_NAME = "Reclamación";
    
    /**
     * Constructor por ID
     */
    public ReclamacionNotFoundException(Long id) {
        super(String.format("%s no encontrada con ID: %d", ENTITY_NAME, id));
    }
    
    /**
     * Constructor por número de reclamación
     */
    public ReclamacionNotFoundException(String numeroReclamacion) {
        super(String.format("%s no encontrada con número: %s", ENTITY_NAME, numeroReclamacion));
    }
    
    /**
     * Constructor genérico con campo y valor
     */
    public ReclamacionNotFoundException(String field, String value) {
        super(String.format("%s no encontrada con %s: %s", ENTITY_NAME, field, value));
    }
    
    /**
     * Constructor con mensaje personalizado
     */
    public ReclamacionNotFoundException(String message, boolean customMessage) {
        super(message);
    }

    
    /**
     * Constructor con ID y causa
     */
    public ReclamacionNotFoundException(Long id, Throwable cause) {
        super(String.format("%s no encontrada con ID: %d", ENTITY_NAME, id), cause);
    }
    
    /**
     * Constructor con número de reclamación y causa
     */
    public ReclamacionNotFoundException(String numeroReclamacion, Throwable cause) {
        super(String.format("%s no encontrada con número: %s", ENTITY_NAME, numeroReclamacion), cause);
    }
}
