package com.idea.controller;

import com.idea.entity.CategoriaHorarioDetalle;
import com.idea.service.CategoriaHorarioDetalleService;
import com.idea.service.CategoriaHorarioService;
import com.idea.service.GrupoZonaService;
import com.idea.service.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/detalles-horario")
public class CategoriaHorarioDetalleController {
@Autowired
    private  CategoriaHorarioDetalleService categoriaHorarioDetalleService;
    @Autowired
    private  CategoriaHorarioService categoriaHorarioService;
    @Autowired
    private  HorarioService horarioService;
    @Autowired
    private  GrupoZonaService grupoZonaService;



    @GetMapping
    public String listarDetalles(Model model) {
        List<CategoriaHorarioDetalle> detalles = categoriaHorarioDetalleService.getAllDetalles();
        model.addAttribute("detalles", detalles);
        return "categoriaHorarioDetalleLista"; // Página Thymeleaf
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("detalle", new CategoriaHorarioDetalle());
        model.addAttribute("categorias", categoriaHorarioService.getAllCategorias());
        model.addAttribute("horarios", horarioService.findAll());
        model.addAttribute("gruposZona",grupoZonaService.obtenerTodos());
        return "categoriaHorarioDetalleFormulario";
    }

    @PostMapping("/guardar")
    public String guardarDetalle(@ModelAttribute CategoriaHorarioDetalle detalle) {
        categoriaHorarioDetalleService.saveDetalle(detalle);
        return "redirect:/detalles-horario";
    }

    @GetMapping("/editar/{id}")
    public String editarDetalle(@PathVariable Long id, Model model) {
        Optional<CategoriaHorarioDetalle> detalleOpt = categoriaHorarioDetalleService.getDetalleById(id);
        if (detalleOpt.isPresent()) {
            model.addAttribute("detalle", detalleOpt.get());
            model.addAttribute("categorias", categoriaHorarioService.getAllCategorias());
            model.addAttribute("horarios", horarioService.findAll());
            return "categoriaHorarioDetalleFormulario"; // Usamos el mismo formulario para crear y editar
        }
        return "redirect:/detalles-horario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarDetalle(@PathVariable Long id) {
        categoriaHorarioDetalleService.deleteDetalle(id);
        return "redirect:/detalles-horario";
    }
}
