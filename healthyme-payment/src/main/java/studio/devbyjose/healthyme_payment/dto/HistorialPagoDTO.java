package studio.devbyjose.healthyme_payment.dto;

import java.math.BigDecimal;

public class HistorialPagoDTO {
    private String id;
    private String patient;
    private BigDecimal amount;
    private String date; // Formato: yyyy-MM-dd
    private String status; // paid | pending | failed

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getters y Setters


}