package studio.devbyjose.healthyme_reclamaciones.service.interfaces;

/**
 * Servicio para la generación de números únicos de reclamación
 */
public interface NumeracionService {

    /**
     * Generar número único para una nueva reclamación
     * @return String Número único generado
     */
    String generarNumeroReclamacion();

    /**
     * Generar número de hoja de reclamación
     * @return String Número de hoja generado
     */
    String generarNumeroHoja();

    /**
     * Validar formato de número de reclamación
     * @param numeroReclamacion Número a validar
     * @return boolean True si el formato es válido
     */
    boolean validarFormatoNumero(String numeroReclamacion);

    /**
     * Obtener siguiente número de secuencia
     * @param prefijo Prefijo del número (REC, HOJA, etc.)
     * @return Long Siguiente número de secuencia
     */
    Long obtenerSiguienteSecuencia(String prefijo);

    /**
     * Reiniciar secuencia para nuevo año
     * @param prefijo Prefijo de la secuencia
     * @return boolean True si se reinició correctamente
     */
    boolean reiniciarSecuenciaAnual(String prefijo);
}
