package com.idea.repository;

import com.idea.entity.AsignacionHorario;
import com.idea.entity.AsignacionZona;
import com.idea.entity.Empleado;
import com.idea.entity.HorarioResumenDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface AsignacionHorarioRepository  extends JpaRepository<AsignacionHorario, Long> {
    void deleteByEmpleado(Empleado empleado);


    @Query("SELECT a FROM AsignacionHorario a WHERE a.empleado.id = :empleadoId " +
            "ORDER BY CASE " +
            "WHEN a.diaDeLaSemana = 'Lunes' THEN 1 " +
            "WHEN a.diaDeLaSemana = 'Martes' THEN 2 " +
            "WHEN a.diaDeLaSemana = 'Miércoles' THEN 3 " +
            "WHEN a.diaDeLaSemana = 'Jueves' THEN 4 " +
            "WHEN a.diaDeLaSemana = 'Viernes' THEN 5 " +
            "WHEN a.diaDeLaSemana = 'Sábado' THEN 6 " +
            "WHEN a.diaDeLaSemana = 'Domingo' THEN 7 " +
            "ELSE 8 END")
    List<AsignacionHorario> findByEmpleadoId(@Param("empleadoId") Long empleadoId);


    /**
     * Cuenta la cantidad de asignaciones por periodo para un día específico de la semana
     * @param diaDeLaSemana el día de la semana (ej: "Lunes", "Martes", etc.)
     * @return Lista de objetos con el periodo y la cantidad
     */
    @Query("SELECT h.periodo as periodo, COUNT(a) as cantidad " +
            "FROM AsignacionHorario a " +
            "JOIN a.horario h " +
            "WHERE a.diaDeLaSemana = :diaDeLaSemana " +
            "GROUP BY h.periodo")
    List<Map<String, Object>> contarAsignacionesPorPeriodoYDia(@Param("diaDeLaSemana") String diaDeLaSemana);

    /**
     * Cuenta la cantidad de asignaciones para un periodo específico en un día específico
     * @param diaDeLaSemana el día de la semana
     * @param periodo el periodo ("Almuerzo", "Cena", "Ambos")
     * @return la cantidad de asignaciones
     */
    @Query("SELECT COUNT(a) FROM AsignacionHorario a " +
            "JOIN a.horario h " +
            "WHERE a.diaDeLaSemana = :diaDeLaSemana " +
            "AND h.periodo = :periodo")
    Long contarAsignacionesPorDiaYPeriodo(
            @Param("diaDeLaSemana") String diaDeLaSemana,
            @Param("periodo") String periodo
    );



    @Transactional
    @Modifying
    @Query("DELETE FROM AsignacionHorario a WHERE a.empleado.id = :empleadoId")
    void eliminarPorEmpleadoId(@Param("empleadoId") Long empleadoId);

    @Query("SELECT a.diaDeLaSemana, a.periodo, COUNT(a) " +
            "FROM AsignacionHorario a " +
            "GROUP BY a.diaDeLaSemana, a.periodo")
    List<Object[]> contarAsignacionesPorDiaYPeriodo();







    @Query("SELECT a.periodo, a.diaDeLaSemana, COUNT(a) as cantidad " +
            "FROM AsignacionHorario a " +
            "GROUP BY a.periodo, a.diaDeLaSemana " +
            "ORDER BY a.periodo, CASE " +
            "  WHEN a.diaDeLaSemana = 'Lunes' THEN 1 " +
            "  WHEN a.diaDeLaSemana = 'Martes' THEN 2 " +
            "  WHEN a.diaDeLaSemana = 'Miércoles' THEN 3 " +
            "  WHEN a.diaDeLaSemana = 'Jueves' THEN 4 " +
            "  WHEN a.diaDeLaSemana = 'Viernes' THEN 5 " +
            "  WHEN a.diaDeLaSemana = 'Sábado' THEN 6 " +
            "  WHEN a.diaDeLaSemana = 'Domingo' THEN 7 " +
            "  ELSE 8 END")
    List<Object[]> contarAsignacionesPorPeriodoYDia();




    @Query("SELECT COUNT(ah) FROM AsignacionHorario ah WHERE ah.diaDeLaSemana = :dia AND ah.horario.codigo != 'Free'")
    int countEmpleadosDisponiblesPorDia(String dia);


    @Query("SELECT ah FROM AsignacionHorario ah WHERE ah.empleado.id = :empleadoId AND LOWER(ah.diaDeLaSemana) = LOWER(:diaDeLaSemana)")
    List<AsignacionHorario> findByEmpleadoAndDiaDeLaSemana(@Param("empleadoId") Long empleadoId, @Param("diaDeLaSemana") String diaDeLaSemana);

    @Query("SELECT COUNT(DISTINCT a.empleado) FROM AsignacionHorario a")
    Long countDistinctEmpleados();
}
