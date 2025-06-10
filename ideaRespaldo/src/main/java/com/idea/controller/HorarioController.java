package com.idea.controller;

import com.idea.entity.Horario;
import com.idea.service.HorarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/horarios")
public class HorarioController {
    @Autowired
    private HorarioService horarioService;

    @GetMapping
    public String listarHorarios(Model model) {
        model.addAttribute("horarios", horarioService.findAll());
        return "horarioList";
    }

    @GetMapping("/nuevo")
    public String nuevoHorario(Model model) {
        model.addAttribute("horario", new Horario());
        return "horarioForm";
    }

    @PostMapping
    public String guardarHorario(@ModelAttribute Horario horario) {
        horarioService.save(horario);
        return "redirect:/horarios";
    }

    @GetMapping("/editar/{id}")
    public String editarHorario(@PathVariable Long id, Model model) {
        Optional<Horario> horario = horarioService.findById(id);
        if (horario.isPresent()) {
            model.addAttribute("horario", horario.get());
            return "horarioForm";
        }
        return "redirect:/horarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarHorario(@PathVariable Long id) {
        horarioService.deleteById(id);
        return "redirect:/horarios";
    }
}