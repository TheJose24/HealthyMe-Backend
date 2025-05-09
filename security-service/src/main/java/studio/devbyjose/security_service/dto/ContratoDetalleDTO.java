package studio.devbyjose.security_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ContratoDetalleDTO extends ContratoDTO {
    private String cargo;
    private String descripcion;
    private BigDecimal salario;
    private Integer horasSemana;

    // Constructor que recibe un ContratoDTO y valores adicionales
    public ContratoDetalleDTO(ContratoDTO contratoDTO, String cargo, String descripcion,
                              BigDecimal salario, Integer horasSemana) {
        super(contratoDTO.getIdContrato(), contratoDTO.getFechaInicio(),
                contratoDTO.getFechaFin(), contratoDTO.isActivo());
        this.cargo = cargo;
        this.descripcion = descripcion;
        this.salario = salario;
        this.horasSemana = horasSemana;
    }
}