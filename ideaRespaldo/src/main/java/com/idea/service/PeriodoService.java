package com.idea.service;

import com.idea.entity.Periodo;
import com.idea.repository.PeriodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PeriodoService {

    @Autowired
    private PeriodoRepository periodoRepository;

    public List<Periodo> listarTodos() {
        return periodoRepository.findAll();
    }

    public Optional<Periodo> obtenerPorId(Long id) {
        return periodoRepository.findById(id);
    }

    public Periodo guardar(Periodo periodo) {
        return periodoRepository.save(periodo);
    }

    public Periodo actualizar(Long id, Periodo nuevoPeriodo) {
        return periodoRepository.findById(id).map(periodo -> {
            periodo.setNombre(nuevoPeriodo.getNombre());
            periodo.setInicio(nuevoPeriodo.getInicio());
            periodo.setFin(nuevoPeriodo.getFin());
            return periodoRepository.save(periodo);
        }).orElseThrow(() -> new RuntimeException("Periodo no encontrado"));
    }

    public void eliminar(Long id) {
        periodoRepository.deleteById(id);
    }
}
