package com.idea.controller;

import com.idea.entity.GrupoZona;
import com.idea.service.GrupoZonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/grupos-zona")
public class GrupoZonaController {

    @Autowired
    private GrupoZonaService grupoZonaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("gruposZona", grupoZonaService.obtenerTodos());
        return "grupoZonaLista"; // Vista Thymeleaf
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("grupoZona", new GrupoZona());
        return "grupoZonaFormulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute GrupoZona grupoZona) {
        grupoZonaService.guardar(grupoZona);
        return "redirect:/grupos-zona";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("grupoZona", grupoZonaService.obtenerPorId(id).orElse(new GrupoZona()));
        return "grupoZonaFormulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        grupoZonaService.eliminar(id);
        return "redirect:/grupos-zona";
    }
}
