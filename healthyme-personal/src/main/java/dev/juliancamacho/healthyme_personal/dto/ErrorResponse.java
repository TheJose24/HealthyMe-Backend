package dev.juliancamacho.healthyme_personal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private Integer status;
    private String message;
    private Instant timestamp;
    private Map<String, String> errors;
}