package dev.diegoqm.healthyme_citas.entity;

import dev.diegoqm.healthyme_citas.enums.EstadoCita;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cita")
public class Cita extends Auditable{

    @Id
    private String id;

    private LocalDate fecha;

    private LocalTime hora;
    
    private EstadoCita estado;
    @Field("id_paciente")
    private Long idPaciente;
    @Field("id_medico")
    private Integer idMedico;
    @Field("id_consultorio")
    private Long idConsultorio;

}