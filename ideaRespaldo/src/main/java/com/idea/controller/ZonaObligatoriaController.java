package com.idea.controller;

import com.idea.entity.ZonaObligatoria;
import com.idea.repository.ZonaRepository;
import com.idea.service.ZonaObligatoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/zonas-obligatorias")
public class ZonaObligatoriaController {

    @Autowired
    private ZonaObligatoriaService zonaObligatoriaService;

    @Autowired
    private ZonaRepository zonaRepository; // Para listar zonas en el formulario

    @GetMapping
    public String listar(Model model) {
        List<ZonaObligatoria> zonasObligatorias = zonaObligatoriaService.listarTodas();
        model.addAttribute("zonasObligatorias", zonasObligatorias);
        return "zona_obligatoriaListar"; // Thymeleaf template
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("zonaObligatoria", new ZonaObligatoria());
        model.addAttribute("zonas", zonaRepository.findAll());
        return "zona_obligatoriaFormulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ZonaObligatoria zonaObligatoria) {
        zonaObligatoriaService.guardar(zonaObligatoria);
        return "redirect:/zonas-obligatorias";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<ZonaObligatoria> zonaObligatoria = zonaObligatoriaService.obtenerPorId(id);
        if (zonaObligatoria.isPresent()) {
            model.addAttribute("zonaObligatoria", zonaObligatoria.get());
            model.addAttribute("zonas", zonaRepository.findAll());
            return "zona_obligatoriaFormulario";
        } else {
            return "redirect:/zonas-obligatorias";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        zonaObligatoriaService.eliminar(id);
        return "redirect:/zonas-obligatorias";
    }
}
