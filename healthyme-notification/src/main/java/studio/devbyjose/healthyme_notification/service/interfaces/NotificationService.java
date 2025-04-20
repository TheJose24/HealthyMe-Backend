package studio.devbyjose.healthyme_notification.service.interfaces;

import java.util.List;
import studio.devbyjose.healthyme_notification.dto.NotificacionDTO;
import studio.devbyjose.healthyme_notification.dto.PlantillaDTO;
import studio.devbyjose.healthyme_notification.event.CitaEvent;
import studio.devbyjose.healthyme_notification.event.ExamenEvent;
import studio.devbyjose.healthyme_notification.event.RecetaEvent;

public interface NotificationService {

    // Métodos básicos de notificación
    void enviarNotificacion(NotificacionDTO notificacionDTO);

    void reintentarNotificacionesFallidas();

    List<NotificacionDTO> obtenerNotificacionesPorDestinatario(String destinatario, int pagina, int tamaño);

    // Métodos específicos para tipos de eventos
    void enviarConfirmacionCita(CitaEvent citaEvent);

    void enviarActualizacionCita(CitaEvent citaEvent);

    void enviarCancelacionCita(CitaEvent citaEvent);

    void enviarRecordatorioCita(CitaEvent citaEvent);

    void enviarReceta(RecetaEvent recetaEvent);

    void enviarResultadoExamen(ExamenEvent examenEvent);

    // Gestión de plantillas
    List<PlantillaDTO> obtenerTodasLasPlantillas();

    PlantillaDTO obtenerPlantilla(Integer idPlantilla);

    PlantillaDTO crearPlantilla(PlantillaDTO plantillaDTO);

    PlantillaDTO actualizarPlantilla(Integer idPlantilla, PlantillaDTO plantillaDTO);

    void eliminarPlantilla(Integer idPlantilla);
}