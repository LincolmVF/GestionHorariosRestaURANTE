package com.idea.repository;

import com.idea.entity.CategoriaHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaHorarioRepository extends JpaRepository<CategoriaHorario, Long> {
    /**
     * Busca una categoría de horario por su nombre
     * @param nombre Nombre de la categoría (ej: "Apertura", "Cubrir", "Relleno")
     * @return La categoría de horario encontrada o null si no existe
     */
    CategoriaHorario findByNombre(String nombre);

    /**
     * Verifica si existe una categoría de horario con el nombre especificado
     * @param nombre Nombre de la categoría a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca categorías de horario que contengan el texto especificado en su nombre
     * @param texto Texto a buscar en el nombre de la categoría
     * @return Lista de categorías que coinciden con el criterio de búsqueda
     */
    List<CategoriaHorario> findByNombreContaining(String texto);
}
