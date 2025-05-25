package studio.devbyjose.healthyme_notification.service.interfaces;

import java.util.Map;

public interface FacturaPdfService {
    byte[] generarFacturaPdf(Map<String, Object> datosFactura);
}