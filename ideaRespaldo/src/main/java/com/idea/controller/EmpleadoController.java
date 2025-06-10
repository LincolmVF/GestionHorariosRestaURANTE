package com.idea.controller;

import com.idea.entity.Empleado;
import com.idea.repository.DetallePlantillaRepository;
import com.idea.service.EmpleadoService;
import com.idea.service.PlantillaHorarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private PlantillaHorarioService plantillaHorarioService;

    @Autowired
    private DetallePlantillaRepository detallePlantillaRepository;

    @GetMapping
    public String listarEmpleados(Model model) {
        model.addAttribute("empleados", empleadoService.findAll());
        return "empleadoList";
    }

    @GetMapping("/nuevo")
    public String nuevoEmpleado(Model model) {
        model.addAttribute("empleado", new Empleado());
        model.addAttribute("plantillas", plantillaHorarioService.findAll());
        return "empleadoForm";
    }

    @PostMapping
    public String guardarEmpleado(@ModelAttribute Empleado empleado) {
        empleadoService.save(empleado);
        return "redirect:/empleados";
    }

    @GetMapping("/editar/{id}")
    public String editarEmpleado(@PathVariable Long id, Model model) {
        Optional<Empleado> empleado = empleadoService.findById(id);
        if (empleado.isPresent()) {
            model.addAttribute("empleado", empleado.get());
            model.addAttribute("plantillas", plantillaHorarioService.findAll());

            // Si el empleado ya tiene una plantilla, cargamos sus detalles
            if (empleado.get().getPlantillaHorario() != null) {
                Long plantillaId = empleado.get().getPlantillaHorario().getId();
                model.addAttribute("detallesPlantilla",
                        detallePlantillaRepository.obtenerResumenHorariosPorPlantilla(plantillaId));
            }

            return "empleadoForm";
        }
        return "redirect:/empleados";
    }

    @PostMapping("/actualizar")
    public String actualizarEmpleado(@ModelAttribute Empleado empleado) {
        empleadoService.update(empleado);
        return "redirect:/horarioSemanal";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable Long id) {
        empleadoService.deleteById(id);
        return "redirect:/empleados";
    }



    @PostMapping("/eliminarAsignacion/{empleadoId}")
    public String eliminarAsignaciones(@PathVariable Long empleadoId, RedirectAttributes redirectAttributes) {
        empleadoService.eliminarAsignacionesPorEmpleadoId(empleadoId);
        redirectAttributes.addFlashAttribute("mensaje", "Asignaciones eliminadas correctamente");
        return "redirect:/horarioSemanal";
    }


    // Endpoint para obtener los detalles de una plantilla vía AJAX
    @GetMapping("/detalles-plantilla/{plantillaId}")
    @ResponseBody
    public List<Map<String, Object>> obtenerDetallesPlantilla(@PathVariable Long plantillaId) {
        return detallePlantillaRepository.obtenerResumenHorariosPorPlantilla(plantillaId);
    }


}
