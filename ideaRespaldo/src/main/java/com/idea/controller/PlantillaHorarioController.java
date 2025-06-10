package com.idea.controller;


import com.idea.entity.DetallePlantillaHorario;
import com.idea.entity.PlantillaHorario;
import com.idea.service.PlantillaHorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Controller
@RequestMapping("/plantillas")
public class PlantillaHorarioController {
    @Autowired
    private PlantillaHorarioService plantillaHorarioService;

    @GetMapping
    public String listarPlantillas(Model model) {
        model.addAttribute("plantillas", plantillaHorarioService.findAll());
        return "plantillaList";
    }

    @GetMapping("/nueva")
    public String nuevaPlantilla(Model model) {
        model.addAttribute("plantilla", new PlantillaHorario());
        return "plantillaForm";
    }

    @PostMapping
    public String guardarPlantilla(@ModelAttribute PlantillaHorario plantilla) {
        plantillaHorarioService.save(plantilla);
        return "redirect:/plantillas";
    }

    @GetMapping("/editar/{id}")
    public String editarPlantilla(@PathVariable Long id, Model model) {
        Optional<PlantillaHorario> plantilla = plantillaHorarioService.findById(id);
        if (plantilla.isPresent()) {
            model.addAttribute("plantilla", plantilla.get());
            return "plantillaForm";
        }
        return "redirect:/plantillas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPlantilla(@PathVariable Long id) {
        plantillaHorarioService.deleteById(id);
        return "redirect:/plantillas";
    }



    // Nuevo método optimizado para obtener el resumen de la plantilla
    @GetMapping("/detalle/{id}")
    @ResponseBody
    public Map<String, Object> obtenerDetallePlantilla(@PathVariable Long id) {
        return plantillaHorarioService.obtenerResumenPlantilla(id);
    }
}
