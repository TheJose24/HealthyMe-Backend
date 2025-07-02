package studio.devbyjose.healthyme_pacientes.dto;

public class PacientesPorMesDTO {
    private String mes;
    private Long cantidad;

    public PacientesPorMesDTO(String mes, Long cantidad) {
        this.setMes(mes);
        this.setCantidad(cantidad);
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    // Getters y setters
}
