package com.idea.controller;

import com.idea.entity.AsignacionHorario;
import com.idea.entity.Empleado;
import com.idea.service.AsignacionHorarioService;
import com.idea.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/horarioSemanal")
public class HorarioSemanalController {
    @Autowired
    private AsignacionHorarioService asignacionHorarioService;

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    public String mostrarHorarioSemanal(Model model) {
        List<Empleado> empleados = empleadoService.findAll();
        Map<Long, List<AsignacionHorario>> horariosPorEmpleado = new HashMap<>();

        for (Empleado empleado : empleados) {
            List<AsignacionHorario> horarioSemanal = asignacionHorarioService.obtenerHorarioSemanal(empleado.getId());
            horariosPorEmpleado.put(empleado.getId(), horarioSemanal);
        }

        model.addAttribute("empleados", empleados);
        model.addAttribute("horariosPorEmpleado", horariosPorEmpleado);


        return "horarioSemanal";
    }


    @GetMapping("/resumen")
    public String mostrarResumenAsignaciones(Model model) {
        model.addAttribute("matrizAsignaciones", asignacionHorarioService.obtenerMatrizAsignaciones());
        model.addAttribute("diasSemana", asignacionHorarioService.obtenerDiasSemana());
        return "resumen-asignaciones";
    }



}
