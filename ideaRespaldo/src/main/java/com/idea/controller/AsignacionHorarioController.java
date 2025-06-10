package com.idea.controller;

import com.idea.entity.AsignacionHorario;
import com.idea.service.AsignacionHorarioService;
import com.idea.service.EmpleadoService;
import com.idea.service.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionHorarioController {

    @Autowired
    private AsignacionHorarioService asignacionHorarioService;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    public String listarAsignaciones(Model model) {
        model.addAttribute("asignaciones", asignacionHorarioService.findAll());
        return "asignacionList";
    }

    @GetMapping("/editar/{id}")
    public String editarAsignacion(@PathVariable Long id, Model model) {
        Optional<AsignacionHorario> asignacion = asignacionHorarioService.findById(id);
        if (asignacion.isPresent()) {
            model.addAttribute("asignacion", asignacion.get());
            model.addAttribute("horarios", horarioService.findAll());
            model.addAttribute("empleados", empleadoService.findAll());
            return "asignacionForm";
        }
        return "redirect:/asignaciones";
    }

    @PostMapping("/actualizar")
    public String actualizarAsignacion(@ModelAttribute AsignacionHorario asignacion) {
        asignacionHorarioService.save(asignacion);
        return "redirect:/horarioSemanal";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAsignacion(@PathVariable Long id) {
        asignacionHorarioService.deleteById(id);
        return "redirect:/asignaciones";
    }
}
