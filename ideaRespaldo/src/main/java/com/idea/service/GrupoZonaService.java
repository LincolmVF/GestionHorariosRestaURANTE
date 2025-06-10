package com.idea.service;

import com.idea.entity.GrupoZona;
import com.idea.repository.GrupoZonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoZonaService {
    @Autowired
    private GrupoZonaRepository grupoZonaRepository;

    public List<GrupoZona> obtenerTodos() {
        return grupoZonaRepository.findAll();
    }

    public Optional<GrupoZona> obtenerPorId(Long id) {
        return grupoZonaRepository.findById(id);
    }

    public GrupoZona guardar(GrupoZona grupoZona) {
        return grupoZonaRepository.save(grupoZona);
    }

    public void eliminar(Long id) {
        grupoZonaRepository.deleteById(id);
    }
}
