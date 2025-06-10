package com.idea.repository;

import com.idea.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
//    @Query("SELECT e FROM Empleado e JOIN AsignacionHorario ah ON e.id = ah.empleado.id WHERE ah.diaDeLaSemana = :dia AND ah.horario.codigo != 'Free'")
//    List<Empleado> findEmpleadosDisponiblesPorDia(String dia);

    @Query("SELECT e FROM Empleado e WHERE e.id NOT IN " +
            "(SELECT ah.empleado.id FROM AsignacionHorario ah WHERE ah.diaDeLaSemana = :dia AND ah.horario.codigo = 'Free')")
    List<Empleado> findEmpleadosDisponiblesPorDia(@Param("dia") String dia);





    @Query(value = """
        SELECT DISTINCT e.*
        FROM empleado e
        JOIN plantilla_horario ph ON e.plantilla_horario_id = ph.id
        JOIN detalle_plantilla_horario dph ON ph.id = dph.plantilla_horario_id
        WHERE dph.dia_de_la_semana = :dia
        AND e.id NOT IN (
            SELECT az.empleado_id
            FROM asignacion_zona az
            WHERE az.dia_de_la_semana = :dia
        )
        """, nativeQuery = true)
    List<Empleado> findEmpleadosDisponiblesNoAsignados(@Param("dia") String dia);

}
