package com.idea.repository;

import com.idea.entity.CategoriaHorario;
import com.idea.entity.CategoriaHorarioDetalle;
import com.idea.entity.GrupoZona;
import com.idea.entity.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriaHorarioDetalleRepository extends JpaRepository<CategoriaHorarioDetalle, Long> {
    List<CategoriaHorarioDetalle> findByCategoriaHorario_Nombre(String nombreCategoria);
    /**
     * Busca detalles de categoría de horario por categoría y horario
     * @param categoriaHorario La categoría de horario
     * @param horario El horario
     * @return Lista de detalles que coinciden con la categoría y horario
     */
    List<CategoriaHorarioDetalle> findByCategoriaHorarioAndHorario(CategoriaHorario categoriaHorario, Horario horario);

    /**
     * Busca detalles de categoría de horario por grupo de zona
     * @param grupoZona El grupo de zona
     * @return Lista de detalles asociados al grupo de zona especificado
     */
    List<CategoriaHorarioDetalle> findByGrupoZona(GrupoZona grupoZona);

    /**
     * Busca detalles de categoría de horario por categoría
     * @param categoriaHorario La categoría de horario
     * @return Lista de detalles asociados a la categoría especificada
     */
    List<CategoriaHorarioDetalle> findByCategoriaHorario(CategoriaHorario categoriaHorario);

    /**
     * Busca detalles de categoría de horario por horario
     * @param horario El horario
     * @return Lista de detalles asociados al horario especificado
     */
    List<CategoriaHorarioDetalle> findByHorario(Horario horario);

    /**
     * Busca detalles de categoría de horario por categoría y grupo de zona
     * @param categoriaHorario La categoría de horario
     * @param grupoZona El grupo de zona
     * @return Lista de detalles que coinciden con la categoría y grupo de zona
     */
    List<CategoriaHorarioDetalle> findByCategoriaHorarioAndGrupoZona(CategoriaHorario categoriaHorario, GrupoZona grupoZona);

    /**
     * Verifica si existe un detalle para la combinación de categoría, horario y grupo de zona
     * @param categoriaHorario La categoría de horario
     * @param horario El horario
     * @param grupoZona El grupo de zona
     * @return true si existe, false en caso contrario
     */
    boolean existsByCategoriaHorarioAndHorarioAndGrupoZona(CategoriaHorario categoriaHorario, Horario horario, GrupoZona grupoZona);

    /**
     * Busca detalles de categoría de horario por categoría y día de la semana
     * Este método requiere una consulta personalizada para acceder al día de la semana a través del horario
     * @param categoriaId ID de la categoría de horario
     * @param diaSemana Día de la semana (ej: "Lunes", "Martes")
     * @return Lista de detalles que coinciden con la categoría y día de la semana
     */
    @Query("SELECT chd FROM CategoriaHorarioDetalle chd " +
            "JOIN Horario h ON chd.horario = h " +
            "JOIN AsignacionHorario ah ON ah.horario = h " +
            "WHERE chd.categoriaHorario.id = :categoriaId " +
            "AND ah.diaDeLaSemana = :diaSemana")
    List<CategoriaHorarioDetalle> findByCategoriaAndDiaSemana(@Param("categoriaId") Long categoriaId, @Param("diaSemana") String diaSemana);

    /**
     * Elimina todos los detalles asociados a una categoría de horario
     * @param categoriaHorario La categoría de horario
     */
    void deleteByCategoriaHorario(CategoriaHorario categoriaHorario);

    /**
     * Elimina todos los detalles asociados a un grupo de zona
     * @param grupoZona El grupo de zona
     */
    void deleteByGrupoZona(GrupoZona grupoZona);
}
