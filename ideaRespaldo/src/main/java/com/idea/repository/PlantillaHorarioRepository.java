package com.idea.repository;

import com.idea.entity.PlantillaHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantillaHorarioRepository  extends JpaRepository<PlantillaHorario, Long> {
}
