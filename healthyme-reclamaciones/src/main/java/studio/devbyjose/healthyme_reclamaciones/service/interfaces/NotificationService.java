package studio.devbyjose.healthyme_reclamaciones.service.interfaces;

import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.enums.*;

/**
 * Servicio para el envío de notificaciones relacionadas con reclamaciones
 */
public interface NotificationService {

    // =================== NOTIFICACIONES AL CLIENTE ===================

    /**
     * Notificar al cliente sobre la recepción de su reclamación
     * @param reclamacionDTO Datos de la reclamación
     * @return boolean True si se envió correctamente
     */
    boolean notificarRecepcionReclamacion(ReclamacionDTO reclamacionDTO);

    /**
     * Notificar al cliente sobre cambio de estado
     * @param reclamacionDTO Datos de la reclamación
     * @param estadoAnterior Estado anterior
     * @param comentario Comentario del cambio
     * @return boolean True si se envió correctamente
     */
    boolean notificarCambioEstado(ReclamacionDTO reclamacionDTO, 
                                 EstadoReclamacion estadoAnterior, 
                                 String comentario);

    /**
     * Notificar al cliente sobre nueva respuesta
     * @param respuestaDTO Datos de la respuesta
     * @return boolean True si se envió correctamente
     */
    boolean notificarNuevaRespuesta(RespuestaReclamacionDTO respuestaDTO);

    /**
     * Notificar al cliente sobre cierre de reclamación
     * @param reclamacionDTO Datos de la reclamación
     * @param respuestaFinal Respuesta final
     * @return boolean True si se envió correctamente
     */
    boolean notificarCierreReclamacion(ReclamacionDTO reclamacionDTO, 
                                      RespuestaReclamacionDTO respuestaFinal);

    // =================== NOTIFICACIONES INTERNAS ===================

    /**
     * Notificar asignación de reclamación a usuario
     * @param reclamacionDTO Datos de la reclamación
     * @param usuarioAsignado Usuario asignado
     * @return boolean True si se envió correctamente
     */
    boolean notificarAsignacionReclamacion(ReclamacionDTO reclamacionDTO, 
                                          String usuarioAsignado);

    /**
     * Notificar escalamiento a supervisor
     * @param reclamacionDTO Datos de la reclamación
     * @param supervisor Supervisor asignado
     * @param motivo Motivo del escalamiento
     * @return boolean True si se envió correctamente
     */
    boolean notificarEscalamiento(ReclamacionDTO reclamacionDTO, 
                                 String supervisor, 
                                 String motivo);

    /**
     * Notificar reclamación vencida
     * @param reclamacionDTO Datos de la reclamación
     * @param diasVencidos Días de vencimiento
     * @return boolean True si se envió correctamente
     */
    boolean notificarReclamacionVencida(ReclamacionDTO reclamacionDTO, 
                                       int diasVencidos);

    /**
     * Notificar reclamación por vencer
     * @param reclamacionDTO Datos de la reclamación
     * @param horasRestantes Horas restantes para vencimiento
     * @return boolean True si se envió correctamente
     */
    boolean notificarReclamacionPorVencer(ReclamacionDTO reclamacionDTO, 
                                         int horasRestantes);

    // =================== NOTIFICACIONES MASIVAS ===================

    /**
     * Enviar resumen diario de reclamaciones a supervisores
     * @return boolean True si se envió correctamente
     */
    boolean enviarResumenDiario();

    /**
     * Enviar reporte semanal de estadísticas
     * @return boolean True si se envió correctamente
     */
    boolean enviarReporteSemanal();

    /**
     * Enviar alertas de reclamaciones críticas
     * @return boolean True si se envió correctamente
     */
    boolean enviarAlertasCriticas();

    // =================== CONFIGURACIÓN Y VALIDACIÓN ===================

    /**
     * Verificar si el cliente desea recibir notificaciones
     * @param emailCliente Email del cliente
     * @return boolean True si acepta notificaciones
     */
    boolean clienteAceptaNotificaciones(String emailCliente);

    /**
     * Validar datos de contacto del cliente
     * @param reclamacionDTO Datos de la reclamación
     * @return boolean True si los datos son válidos
     */
    boolean validarDatosContacto(ReclamacionDTO reclamacionDTO);

    /**
     * Registrar intento de notificación
     * @param tipoNotificacion Tipo de notificación
     * @param destinatario Destinatario
     * @param exitoso Si fue exitoso o no
     * @param error Mensaje de error si falló
     */
    void registrarIntentoNotificacion(String tipoNotificacion, 
                                     String destinatario, 
                                     boolean exitoso, 
                                     String error);
}
