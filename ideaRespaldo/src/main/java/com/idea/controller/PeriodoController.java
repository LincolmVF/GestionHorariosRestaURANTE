package com.idea.controller;
import com.idea.entity.Periodo;
import com.idea.service.PeriodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/periodos")
public class PeriodoController {

    @Autowired
    private PeriodoService periodoService;

    @GetMapping
    public String listarTodos(Model model) {
        List<Periodo> periodos = periodoService.listarTodos();
        model.addAttribute("periodos", periodos);
        return "periodoLista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("periodo", new Periodo());
        return "periodoFormulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Periodo periodo) {
        periodoService.guardar(periodo);
        return "redirect:/periodos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Periodo> periodo = periodoService.obtenerPorId(id);
        if (periodo.isPresent()) {
            model.addAttribute("periodo", periodo.get());
            return "periodoFormulario";
        }
        return "redirect:/periodos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        periodoService.eliminar(id);
        return "redirect:/periodos";
    }
}

