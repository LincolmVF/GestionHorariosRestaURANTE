package com.idea.controller;

import com.idea.entity.DetallePlantillaHorario;
import com.idea.service.DetallePlantillaService;
import com.idea.service.GrupoZonaService;
import com.idea.service.HorarioService;
import com.idea.service.PlantillaHorarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/detalles-plantilla")
public class DetallePlantillaHorarioController {

    @Autowired
    private DetallePlantillaService detallePlantillaHorarioService;

    @Autowired
    private PlantillaHorarioService plantillaHorarioService;

    @Autowired
    private HorarioService horarioService;
    @Autowired
    private GrupoZonaService grupoZonaService;

    @GetMapping
    public String listarDetalles(Model model) {
        model.addAttribute("detalles", detallePlantillaHorarioService.findAll());
        return "detallePlantillaList";
    }

    @GetMapping("/nuevo")
    public String nuevoDetalle(Model model) {
        model.addAttribute("detalle", new DetallePlantillaHorario());
        model.addAttribute("plantillas", plantillaHorarioService.findAll());
        model.addAttribute("horarios", horarioService.findAll());
        model.addAttribute("gruposZona", grupoZonaService.obtenerTodos());
        return "detallePlantillaForm";
    }

    @PostMapping
    public String guardarDetalle(@ModelAttribute DetallePlantillaHorario detalle) {
        detallePlantillaHorarioService.save(detalle);
        return "redirect:/detalles-plantilla";
    }

    @GetMapping("/editar/{id}")
    public String editarDetalle(@PathVariable Long id, Model model) {
        Optional<DetallePlantillaHorario> detalle = detallePlantillaHorarioService.findById(id);
        if (detalle.isPresent()) {
            model.addAttribute("detalle", detalle.get());
            model.addAttribute("plantillas", plantillaHorarioService.findAll());
            model.addAttribute("horarios", horarioService.findAll());
            model.addAttribute("gruposZona", grupoZonaService.obtenerTodos());
            return "detallePlantillaForm";
        }
        return "redirect:/detalles-plantilla";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarDetalle(@PathVariable Long id) {
        detallePlantillaHorarioService.deleteById(id);
        return "redirect:/detalles-plantilla";
    }
}
