package com.idea.repository;

import com.idea.entity.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Long> {

    Periodo findByNombre(String nombre);
}
