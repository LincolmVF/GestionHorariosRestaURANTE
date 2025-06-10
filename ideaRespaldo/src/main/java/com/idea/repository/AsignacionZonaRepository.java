package com.idea.repository;

import com.idea.entity.AsignacionZona;
import com.idea.entity.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface AsignacionZonaRepository extends JpaRepository<AsignacionZona, Long> {
    void deleteAll();
    boolean existsByZonaAndDiaDeLaSemana(Zona zona, String diaDeLaSemana);
    boolean existsByZonaAndDiaDeLaSemanaAndInicioBeforeAndFinAfter(
            Zona zona, String diaDeLaSemana, LocalTime fin, LocalTime inicio
    );

}
