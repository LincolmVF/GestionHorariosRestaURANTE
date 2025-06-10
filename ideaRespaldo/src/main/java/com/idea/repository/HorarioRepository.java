package com.idea.repository;

import com.idea.entity.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioRepository  extends JpaRepository<Horario, Long> {
    @Query("SELECT h FROM Horario h " +
            "JOIN DetallePlantillaHorario dph ON h.id = dph.horario.id " +
            "JOIN PlantillaHorario ph ON dph.plantillaHorario.id = ph.id " +
            "JOIN Empleado e ON e.plantillaHorario.id = ph.id " +
            "WHERE e.id = :empleadoId AND dph.diaDeLaSemana = :dia")
    Horario findHorarioByEmpleadoAndDia(@Param("empleadoId") Long empleadoId, @Param("dia") String dia);
}
