package com.idea.repository;

import com.idea.entity.Zona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZonaRepository extends JpaRepository<Zona, Long> {

    List<Zona> findByGrupoZonaId(Long grupoId);
}
