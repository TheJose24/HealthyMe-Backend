package dev.diegoqm.healthyme_infraestructura.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SedeDTO {

    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private Integer idHorario;
}