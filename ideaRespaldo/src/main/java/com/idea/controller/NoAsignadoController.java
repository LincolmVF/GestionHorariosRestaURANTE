package com.idea.controller;

import com.idea.entity.DetallePlantillaHorario;
import com.idea.entity.Empleado;
import com.idea.entity.Horario;
import com.idea.entity.PlantillaHorario;
import com.idea.repository.EmpleadoRepository;
import com.idea.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/noAsig")
public class NoAsignadoController {
    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    private final List<String> DIAS_SEMANA = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

    @GetMapping("/pendientes")
    public String mostrarEmpleadosPendientes(Model model) {
        // Mapa para almacenar la información de empleados por día
        Map<String, List<Map<String, Object>>> empleadosPorDia = new LinkedHashMap<>();

        // Inicializar el mapa con listas vacías para cada día
        for (String dia : DIAS_SEMANA) {
            empleadosPorDia.put(dia, new ArrayList<>());
        }

        // Para cada día de la semana, obtener los empleados sin asignación
        for (String dia : DIAS_SEMANA) {
            List<Empleado> empleadosSinAsignacion = empleadoRepository.findEmpleadosDisponiblesNoAsignados(dia);

            if (empleadosSinAsignacion != null) {
                for (Empleado empleado : empleadosSinAsignacion) {
                    if (empleado != null) {
                        Map<String, Object> info = new HashMap<>();
                        info.put("empleado", empleado);

                        // Obtener el horario del empleado para ese día
                        String codigoHorario = obtenerCodigoHorarioEmpleado(empleado, dia);
                        info.put("codigoHorario", codigoHorario);

                        // Agregar a la lista del día correspondiente
                        empleadosPorDia.get(dia).add(info);
                    }
                }
            }
        }

        model.addAttribute("empleadosPorDia", empleadosPorDia);
        model.addAttribute("diasSemana", DIAS_SEMANA);

        return "empleados_no_asignados";
    }

    // Método auxiliar para obtener el código de horario de un empleado en un día específico
    private String obtenerCodigoHorarioEmpleado(Empleado empleado, String dia) {
        try {
            // Si el empleado tiene una plantilla de horario
            PlantillaHorario plantilla = empleado.getPlantillaHorario();
            if (plantilla != null) {
                List<DetallePlantillaHorario> detalles = plantilla.getDetalles();
                // Buscar en los detalles de la plantilla el horario para ese día
                if (detalles != null) {
                    for (DetallePlantillaHorario detalle : detalles) {
                        if (detalle != null &&
                                detalle.getDiaDeLaSemana() != null &&
                                detalle.getDiaDeLaSemana().equals(dia) &&
                                detalle.getHorario() != null) {

                            Horario horario = detalle.getHorario();
                            String codigo = horario.getCodigo();
                            if (codigo != null) {
                                return codigo;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Capturar cualquier excepción para evitar errores en la vista
            return "Error";
        }

        // Si no se encuentra un horario específico
        return "No asignado";
    }
}