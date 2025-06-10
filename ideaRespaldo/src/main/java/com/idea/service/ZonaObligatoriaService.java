package com.idea.service;

import com.idea.entity.ZonaObligatoria;
import com.idea.repository.ZonaObligatoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ZonaObligatoriaService {

    @Autowired
    private ZonaObligatoriaRepository zonaObligatoriaRepository;

    public List<ZonaObligatoria> listarTodas() {
        return zonaObligatoriaRepository.findAll();
    }

    public Optional<ZonaObligatoria> obtenerPorId(Long id) {
        return zonaObligatoriaRepository.findById(id);
    }

    public void guardar(ZonaObligatoria zonaObligatoria) {
        zonaObligatoriaRepository.save(zonaObligatoria);
    }

    public void eliminar(Long id) {
        zonaObligatoriaRepository.deleteById(id);
    }
}
