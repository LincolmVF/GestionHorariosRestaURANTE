package com.idea.service;

import com.idea.entity.Zona;
import com.idea.repository.ZonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZonaService {
    @Autowired
    private ZonaRepository zonaRepository;

    public List<Zona> obtenerTodas() {
        return zonaRepository.findAll();
    }

    public Optional<Zona> obtenerPorId(Long id) {
        return zonaRepository.findById(id);
    }

    public Zona guardar(Zona zona) {
        return zonaRepository.save(zona);
    }

    public void eliminar(Long id) {
        zonaRepository.deleteById(id);
    }

    public List<Zona> obtenerPorGrupo(Long grupoId) {
        return zonaRepository.findByGrupoZonaId(grupoId);
    }
}
