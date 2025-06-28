package dev.diegoqm.healthyme_citas.dto;

public class EspecialidadContadaDTO {
    private String especialidad;
    private Long cantidad;

    public EspecialidadContadaDTO(String especialidad, Long cantidad) {
        this.setEspecialidad(especialidad);
        this.setCantidad(cantidad);
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    // Getters y setters
}
