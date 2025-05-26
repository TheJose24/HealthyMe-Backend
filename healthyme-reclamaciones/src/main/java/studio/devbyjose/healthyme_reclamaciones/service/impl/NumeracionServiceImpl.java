package studio.devbyjose.healthyme_reclamaciones.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import studio.devbyjose.healthyme_reclamaciones.service.interfaces.NumeracionService;
import studio.devbyjose.healthyme_reclamaciones.repository.ReclamacionRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * Implementación del servicio de numeración para reclamaciones
 * 
 * Genera números únicos con el formato:
 * - Reclamaciones: REC-YYYY-NNNNNN (ej: REC-2025-000001)
 * - Hojas: HOJA-YYYY-NNNNNN (ej: HOJA-2025-000001)
 * 
 * La numeración se reinicia cada año y mantiene secuencias separadas
 * por tipo de documento.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NumeracionServiceImpl implements NumeracionService {

    private final ReclamacionRepository reclamacionRepository;

    // Cache de secuencias en memoria para mejor rendimiento
    private final ConcurrentHashMap<String, AtomicLong> secuenciaCache = new ConcurrentHashMap<>();
    
    // Patrones de validación
    private static final Pattern PATTERN_RECLAMACION = Pattern.compile("^REC-\\d{4}-\\d{6}$");
    private static final Pattern PATTERN_HOJA = Pattern.compile("^HOJA-\\d{4}-\\d{6}$");
    private static final Pattern PATTERN_GENERAL = Pattern.compile("^[A-Z]+-\\d{4}-\\d{6}$");
    
    // Constantes de configuración
    private static final String PREFIJO_RECLAMACION = "REC";
    private static final String PREFIJO_HOJA = "HOJA";
    private static final int LONGITUD_NUMERO = 6;
    private static final String FORMATO_FECHA = "yyyy";

    @Override
    @Transactional
    public String generarNumeroReclamacion() {
        log.debug("Generando nuevo número de reclamación");
        
        try {
            String year = obtenerAnoActual();
            Long siguienteSecuencia = obtenerSiguienteSecuencia(PREFIJO_RECLAMACION);
            
            String numeroGenerado = construirNumero(PREFIJO_RECLAMACION, year, siguienteSecuencia);
            
            // Verificar que el número no exista en BD (seguridad extra)
            if (existeNumeroEnBD(numeroGenerado)) {
                log.warn("Número de reclamación {} ya existe, regenerando...", numeroGenerado);
                return generarNumeroReclamacion(); // Recursión para generar nuevo número
            }
            
            log.info("Número de reclamación generado exitosamente: {}", numeroGenerado);
            return numeroGenerado;
            
        } catch (Exception e) {
            log.error("Error al generar número de reclamación: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar número de reclamación", e);
        }
    }

    @Override
    @Transactional
    public String generarNumeroHoja() {
        log.debug("Generando nuevo número de hoja de reclamación");
        
        try {
            String year = obtenerAnoActual();
            Long siguienteSecuencia = obtenerSiguienteSecuencia(PREFIJO_HOJA);
            
            String numeroGenerado = construirNumero(PREFIJO_HOJA, year, siguienteSecuencia);
            
            log.info("Número de hoja generado exitosamente: {}", numeroGenerado);
            return numeroGenerado;
            
        } catch (Exception e) {
            log.error("Error al generar número de hoja: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar número de hoja", e);
        }
    }

    @Override
    public boolean validarFormatoNumero(String numeroReclamacion) {
        if (numeroReclamacion == null || numeroReclamacion.trim().isEmpty()) {
            log.debug("Número de reclamación vacío o nulo");
            return false;
        }
        
        String numero = numeroReclamacion.trim().toUpperCase();
        boolean esValido = false;
        
        if (numero.startsWith(PREFIJO_RECLAMACION + "-")) {
            esValido = PATTERN_RECLAMACION.matcher(numero).matches();
        } else if (numero.startsWith(PREFIJO_HOJA + "-")) {
            esValido = PATTERN_HOJA.matcher(numero).matches();
        } else {
            // Validación general para otros tipos de números
            esValido = PATTERN_GENERAL.matcher(numero).matches();
        }
        if (!esValido) {
            log.warn("Número de reclamación no válido: {}", numeroReclamacion);
        }
        
        log.debug("Validación de formato para '{}': {}", numeroReclamacion, esValido);
        return esValido;
    }

    @Override
    @Transactional
    public Long obtenerSiguienteSecuencia(String prefijo) {
        if (prefijo == null || prefijo.trim().isEmpty()) {
            throw new IllegalArgumentException("El prefijo no puede estar vacío");
        }
        
        String prefijoNormalizado = prefijo.trim().toUpperCase();
        String year = obtenerAnoActual();
        String claveSecuencia = construirClaveSecuencia(prefijoNormalizado, year);
        
        log.debug("Obteniendo siguiente secuencia para clave: {}", claveSecuencia);
        
        // Usar cache para mejor rendimiento
        AtomicLong secuencia = secuenciaCache.computeIfAbsent(claveSecuencia, 
            k -> new AtomicLong(obtenerUltimaSecuenciaDeBD(prefijoNormalizado, year)));
        
        Long siguienteNumero = secuencia.incrementAndGet();
        
        log.debug("Siguiente secuencia para '{}': {}", claveSecuencia, siguienteNumero);
        return siguienteNumero;
    }

    @Override
    @Transactional
    public boolean reiniciarSecuenciaAnual(String prefijo) {
        if (prefijo == null || prefijo.trim().isEmpty()) {
            log.warn("Intento de reiniciar secuencia con prefijo vacío");
            return false;
        }
        
        try {
            String prefijoNormalizado = prefijo.trim().toUpperCase();
            String year = obtenerAnoActual();
            String claveSecuencia = construirClaveSecuencia(prefijoNormalizado, year);
            
            log.info("Reiniciando secuencia anual para: {}", claveSecuencia);
            
            // Limpiar cache
            secuenciaCache.remove(claveSecuencia);
            
            // Reinicializar en cache
            secuenciaCache.put(claveSecuencia, new AtomicLong(0L));
            
            log.info("Secuencia reiniciada exitosamente para: {}", claveSecuencia);
            return true;
            
        } catch (Exception e) {
            log.error("Error al reiniciar secuencia anual para prefijo '{}': {}", 
                     prefijo, e.getMessage(), e);
            return false;
        }
    }

    // =================== MÉTODOS PRIVADOS DE APOYO ===================

    /**
     * Construir número con formato estándar
     */
    private String construirNumero(String prefijo, String year, Long secuencia) {
        String numeroFormateado = String.format("%0" + LONGITUD_NUMERO + "d", secuencia);
        return String.format("%s-%s-%s", prefijo, year, numeroFormateado);
    }

    /**
     * Obtener año actual como string
     */
    private String obtenerAnoActual() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMATO_FECHA));
    }

    /**
     * Construir clave única para secuencia
     */
    private String construirClaveSecuencia(String prefijo, String year) {
        return prefijo + "_" + year;
    }

    /**
     * Obtener la última secuencia usada desde base de datos
     */
    private Long obtenerUltimaSecuenciaDeBD(String prefijo, String year) {
        try {
            String patron = prefijo + "-" + year + "-%";
            
            // Primero intentar con la consulta optimizada de máxima secuencia
            Optional<Long> maxSecuencia = reclamacionRepository.findMaxSecuenciaByPatron(patron);
            if (maxSecuencia.isPresent() && maxSecuencia.get() != null) {
                log.debug("Última secuencia encontrada para {}-{}: {}", prefijo, year, maxSecuencia.get());
                return maxSecuencia.get();
            }
            
            // Fallback al método original si la consulta optimizada no funciona
            String ultimoNumero = reclamacionRepository.findUltimoNumeroByPatron(patron);
            
            if (ultimoNumero != null && !ultimoNumero.isEmpty()) {
                // Extraer la secuencia del número (últimos 6 dígitos)
                String[] partes = ultimoNumero.split("-");
                if (partes.length == 3) {
                    Long secuencia = Long.parseLong(partes[2]);
                    log.debug("Última secuencia extraída para {}-{}: {}", prefijo, year, secuencia);
                    return secuencia;
                }
            }
            
            log.debug("No se encontró secuencia previa para {}-{}, iniciando en 0", prefijo, year);
            return 0L;
            
        } catch (Exception e) {
            log.warn("Error al obtener última secuencia de BD para {}-{}: {}", 
                    prefijo, year, e.getMessage());
            return 0L; // En caso de error, iniciar desde 0
        }
    }

    /**
     * Verificar si un número ya existe en base de datos (versión optimizada)
     */
    private boolean existeNumeroEnBD(String numero) {
        try {
            // Usar la consulta optimizada si está disponible
            return reclamacionRepository.existsReclamacionWithNumero(numero);
        } catch (Exception e) {
            log.warn("Error al verificar existencia de número en BD con consulta optimizada, usando fallback: {}", e.getMessage());
            try {
                // Fallback al método original
                return reclamacionRepository.existsByNumeroReclamacion(numero);
            } catch (Exception e2) {
                log.warn("Error al verificar existencia de número en BD con fallback: {}", e2.getMessage());
                return false; // En caso de error, asumir que no existe
            }
        }
    }

    /**
     * Validar y normalizar prefijo
     */
    private String normalizarPrefijo(String prefijo) {
        if (prefijo == null || prefijo.trim().isEmpty()) {
            throw new IllegalArgumentException("El prefijo no puede estar vacío");
        }
        
        String normalizado = prefijo.trim().toUpperCase();
        
        // Validar que el prefijo solo contenga letras
        if (!normalizado.matches("^[A-Z]+$")) {
            throw new IllegalArgumentException("El prefijo solo debe contener letras: " + prefijo);
        }
        
        return normalizado;
    }

    /**
     * Generar número con prefijo personalizado (para futuras extensiones)
     */
    public String generarNumeroConPrefijo(String prefijoPersonalizado) {
        String prefijo = normalizarPrefijo(prefijoPersonalizado);
        String year = obtenerAnoActual();
        Long siguienteSecuencia = obtenerSiguienteSecuencia(prefijo);
        
        return construirNumero(prefijo, year, siguienteSecuencia);
    }

    /**
     * Obtener información de un número parseado
     */
    public NumeroInfo parsearNumero(String numero) {
        if (!validarFormatoNumero(numero)) {
            return null;
        }
        
        String[] partes = numero.trim().toUpperCase().split("-");
        return NumeroInfo.builder()
                .numeroCompleto(numero.trim().toUpperCase())
                .prefijo(partes[0])
                .year(Integer.parseInt(partes[1]))
                .secuencia(Long.parseLong(partes[2]))
                .build();
    }

    /**
     * Clase interna para información de número parseado
     */
    @lombok.Builder
    @lombok.Data
    public static class NumeroInfo {
        private String numeroCompleto;
        private String prefijo;
        private Integer year;
        private Long secuencia;
    }
}
