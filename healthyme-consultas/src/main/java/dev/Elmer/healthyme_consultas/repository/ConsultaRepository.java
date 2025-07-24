package dev.Elmer.healthyme_consultas.repository;

import dev.Elmer.healthyme_consultas.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
    List<Consulta> findByIdPacienteOrderByFechaDesc(Integer idPaciente);

}