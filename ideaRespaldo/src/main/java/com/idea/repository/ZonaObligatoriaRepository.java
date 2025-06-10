package com.idea.repository;

import com.idea.entity.ZonaObligatoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZonaObligatoriaRepository extends JpaRepository<ZonaObligatoria, Long> {


    List<ZonaObligatoria> findByDiaDeLaSemana(String dia);

}
