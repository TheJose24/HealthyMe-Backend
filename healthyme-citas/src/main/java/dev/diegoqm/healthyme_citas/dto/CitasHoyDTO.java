package dev.diegoqm.healthyme_citas.dto;

import java.time.LocalTime;

public class CitasHoyDTO {
    private String nombreMedico;
    private String especialidad;
    private LocalTime horaInicio;

    // Getters & Setters

    public String getNombreMedico() {
        return nombreMedico;
    }
    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }
    public String getEspecialidad() {
        return especialidad;
    }
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
}