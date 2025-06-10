package com.idea.service;

import com.idea.entity.CategoriaHorario;
import com.idea.repository.CategoriaHorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaHorarioService {

    private final CategoriaHorarioRepository categoriaHorarioRepository;

    @Autowired
    public CategoriaHorarioService(CategoriaHorarioRepository categoriaHorarioRepository) {
        this.categoriaHorarioRepository = categoriaHorarioRepository;
    }

    public List<CategoriaHorario> getAllCategorias() {
        return categoriaHorarioRepository.findAll();
    }

    public Optional<CategoriaHorario> getCategoriaById(Long id) {
        return categoriaHorarioRepository.findById(id);
    }

    public CategoriaHorario saveCategoria(CategoriaHorario categoriaHorario) {
        return categoriaHorarioRepository.save(categoriaHorario);
    }

    public void deleteCategoria(Long id) {
        categoriaHorarioRepository.deleteById(id);
    }
}
