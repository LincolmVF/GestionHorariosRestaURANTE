package com.idea.controller;

import com.idea.entity.CategoriaHorario;
import com.idea.service.CategoriaHorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categorias-horario")
public class CategoriaHorarioController {

    private final CategoriaHorarioService categoriaHorarioService;

    @Autowired
    public CategoriaHorarioController(CategoriaHorarioService categoriaHorarioService) {
        this.categoriaHorarioService = categoriaHorarioService;
    }

    @GetMapping
    public String listarCategorias(Model model) {
        List<CategoriaHorario> categorias = categoriaHorarioService.getAllCategorias();
        model.addAttribute("categorias", categorias);
        return "categoriaHorarioLista"; // Página Thymeleaf
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("categoriaHorario", new CategoriaHorario());
        return "categoriaHorarioFormulario";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(@ModelAttribute CategoriaHorario categoriaHorario) {
        categoriaHorarioService.saveCategoria(categoriaHorario);
        return "redirect:/categorias-horario";
    }

    @GetMapping("/editar/{id}")
    public String editarCategoria(@PathVariable Long id, Model model) {
        Optional<CategoriaHorario> categoria = categoriaHorarioService.getCategoriaById(id);
        if (categoria.isPresent()) {
            model.addAttribute("categoriaHorario", categoria.get());
            return "categoriaHorarioFormulario";
        }
        return "redirect:/categorias-horario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaHorarioService.deleteCategoria(id);
        return "redirect:/categorias-horario";
    }
}
