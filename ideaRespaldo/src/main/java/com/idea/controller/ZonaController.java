package com.idea.controller;
import com.idea.entity.Zona;
import com.idea.service.GrupoZonaService;
import com.idea.service.ZonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/zonas")
public class ZonaController {

    @Autowired
    private ZonaService zonaService;

    @Autowired
    private GrupoZonaService grupoZonaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("zonas", zonaService.obtenerTodas());
        return "zonaLista"; // Vista Thymeleaf
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("zona", new Zona());
        model.addAttribute("gruposZona", grupoZonaService.obtenerTodos()); // Para dropdown de grupo
        return "zonaFormulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Zona zona) {
        zonaService.guardar(zona);
        return "redirect:/zonas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("zona", zonaService.obtenerPorId(id).orElse(new Zona()));
        model.addAttribute("gruposZona", grupoZonaService.obtenerTodos());
        return "zonaFormulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        zonaService.eliminar(id);
        return "redirect:/zonas";
    }
}
