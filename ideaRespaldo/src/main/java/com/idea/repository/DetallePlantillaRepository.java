package com.idea.repository;

import com.idea.entity.DetallePlantillaHorario;
import com.idea.entity.PlantillaHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DetallePlantillaRepository  extends JpaRepository<DetallePlantillaHorario,Long> {

    List<DetallePlantillaHorario> findByPlantillaHorario(PlantillaHorario plantillaHorario);




    // Método optimizado con proyección para obtener solo los datos necesarios
    @Query("SELECT new map(" +
            "d.diaDeLaSemana as dia, " +
            "h.codigo as codigo, " +
            "h.inicio as inicio, " +
            "h.fin as fin, " +
            "h.periodo as periodo) " +
            "FROM DetallePlantillaHorario d " +
            "JOIN d.horario h " +
            "WHERE d.plantillaHorario.id = :plantillaId " +
            "ORDER BY CASE " +
            "  WHEN d.diaDeLaSemana = 'Lunes' THEN 1 " +
            "  WHEN d.diaDeLaSemana = 'Martes' THEN 2 " +
            "  WHEN d.diaDeLaSemana = 'Miércoles' THEN 3 " +
            "  WHEN d.diaDeLaSemana = 'Jueves' THEN 4 " +
            "  WHEN d.diaDeLaSemana = 'Viernes' THEN 5 " +
            "  WHEN d.diaDeLaSemana = 'Sábado' THEN 6 " +
            "  WHEN d.diaDeLaSemana = 'Domingo' THEN 7 " +
            "END, h.inicio")
    List<Map<String, Object>> obtenerResumenHorariosPorPlantilla(@Param("plantillaId") Long plantillaId);
}
