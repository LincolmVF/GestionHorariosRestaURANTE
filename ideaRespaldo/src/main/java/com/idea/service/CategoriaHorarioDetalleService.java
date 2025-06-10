package com.idea.service;

import com.idea.entity.CategoriaHorarioDetalle;
import com.idea.repository.CategoriaHorarioDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaHorarioDetalleService {

    private final CategoriaHorarioDetalleRepository categoriaHorarioDetalleRepository;

    @Autowired
    public CategoriaHorarioDetalleService(CategoriaHorarioDetalleRepository categoriaHorarioDetalleRepository) {
        this.categoriaHorarioDetalleRepository = categoriaHorarioDetalleRepository;
    }

    public List<CategoriaHorarioDetalle> getAllDetalles() {
        return categoriaHorarioDetalleRepository.findAll();
    }

    public List<CategoriaHorarioDetalle> getDetallesByCategoria(String nombreCategoria) {
        return categoriaHorarioDetalleRepository.findByCategoriaHorario_Nombre(nombreCategoria);
    }

    public Optional<CategoriaHorarioDetalle> getDetalleById(Long id) {
        return categoriaHorarioDetalleRepository.findById(id);
    }

    public CategoriaHorarioDetalle saveDetalle(CategoriaHorarioDetalle detalle) {
        return categoriaHorarioDetalleRepository.save(detalle);
    }

    public void deleteDetalle(Long id) {
        categoriaHorarioDetalleRepository.deleteById(id);
    }
}
