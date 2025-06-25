package studio.devbyjose.healthyme_payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class HistorialPagoDTO {
    private String doctor;
    private String areaTrabajo;
    private LocalDate fecha;
    private LocalTime hora;
    private BigDecimal monto;

    // Getters y Setters

    public String getDoctor() {
        return doctor;
    }
    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }
    public String getAreaTrabajo() {
        return areaTrabajo;
    }
    public void setAreaTrabajo(String areaTrabajo) {
        this.areaTrabajo = areaTrabajo;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public LocalTime getHora() {
        return hora;
    }
    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
    public BigDecimal getMonto() {
        return monto;
    }
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}