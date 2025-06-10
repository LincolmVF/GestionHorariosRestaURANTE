package com.idea.controller;

import com.idea.entity.*;
import com.idea.repository.*;
import com.idea.service.AsignacionZonaService;
import com.idea.service.EmpleadoService;
import com.idea.service.HorarioService;
import com.idea.service.ZonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/asignacionesZona")
public class AsignacionZonaController {

    @Autowired
    private HorarioService horarioService;

@Autowired
private AsignacionZonaRepository asignacionZonaRepository;
    @Autowired
    private ZonaRepository zonaRepository;
    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private PeriodoRepository periodoRepository;
@Autowired
private ZonaObligatoriaRepository zonaObligatoriaRepository;
@Autowired
private AsignacionHorarioRepository asignacionHorarioRepository;


    @Autowired
    private AsignacionZonaService asignacionZonaService;
    @Autowired
    private ZonaService zonaService;
    @Autowired
    private EmpleadoService empleadoService;



    private final List<String> DIAS_SEMANA = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");


    @GetMapping
    public String listarAsignaciones(Model model) {
        List<AsignacionZona> asignaciones = asignacionZonaService.getAllAsignaciones();
        List<Empleado> empleados = empleadoRepository.findAll();
        List<String> diasSemana = List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        List<Zona> zonas = zonaRepository.findAll();
        model.addAttribute("empleados", empleados);
        model.addAttribute("asignaciones", asignaciones);
        model.addAttribute("diasSemana", diasSemana); // Agregamos los días de la semana
        model.addAttribute("zonas", zonas);







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



        return "asignacionesZonas2";
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





    @PostMapping("/generar")
    public String generarAsignaciones() {
        asignacionZonaService.generarAsignacionesAutomaticas();
        return "redirect:/asignacionesZona";
    }


    @PostMapping("/eliminar-todas")
    public String eliminarTodasLasAsignaciones() {
        asignacionZonaService.eliminarTodasLasAsignaciones();
        return "redirect:/asignacionesZona";
    }

    @GetMapping("/editar/{id}")
    public String editarAsignacion(@PathVariable Long id, Model model) {
        AsignacionZona asignacion = asignacionZonaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID no válido: " + id));

        List<Empleado> empleados = empleadoRepository.findAll();
        List<Zona> zonas = zonaRepository.findAll();
        List<Periodo> periodos = periodoRepository.findAll();

        model.addAttribute("asignacion", asignacion);
        model.addAttribute("empleados", empleados);
        model.addAttribute("zonas", zonas);
        model.addAttribute("periodos", periodos);

        return "editarAsignacionZona"; // Retorna la vista de edición
    }

    @PostMapping("/actualizar")
    public String actualizarAsignacion(@ModelAttribute AsignacionZona asignacion) {
        AsignacionZona asignacionExistente = asignacionZonaRepository.findById(asignacion.getId())
                .orElseThrow(() -> new IllegalArgumentException("ID no válido: " + asignacion.getId()));

        // Actualiza los datos de la asignación existente
        asignacionExistente.setEmpleado(asignacion.getEmpleado());
        asignacionExistente.setZona(asignacion.getZona());
        asignacionExistente.setDiaDeLaSemana(asignacion.getDiaDeLaSemana());
        asignacionExistente.setPeriodo(asignacion.getPeriodo());
        asignacionExistente.setInicio(asignacion.getInicio());
        asignacionExistente.setFin(asignacion.getFin());

        asignacionZonaRepository.save(asignacionExistente); // Guarda los cambios

        return "redirect:/asignacionesZona"; // Redirige a la lista
    }





















































































    @GetMapping("/timeline")
    public String verTimelineAsignaciones(Model model) {
        // Obtener todas las asignaciones y zonas
        List<AsignacionZona> asignaciones = asignacionZonaService.getAllAsignaciones();
        List<Zona> zonas = zonaService.obtenerTodas();
        List<Empleado> empleados = empleadoService.findAll();

        // Definir los días de la semana
        List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

        // Agrupar asignaciones por día
        Map<String, List<AsignacionZona>> asignacionesPorDia = new HashMap<>();
        for (String dia : diasSemana) {
            List<AsignacionZona> asignacionesDelDia = new ArrayList<>();
            for (AsignacionZona asignacion : asignaciones) {
                if (asignacion.getDiaDeLaSemana().equals(dia)) {
                    asignacionesDelDia.add(asignacion);
                }
            }
            asignacionesPorDia.put(dia, asignacionesDelDia);
        }

        // MODIFICADO: Generar horas para el timeline con intervalos de 15 minutos (7:00 AM a 11:45 PM)
        List<String> horasPrincipales = new ArrayList<>();
        List<String> todasLasHoras = new ArrayList<>();
        List<Boolean> esHoraPrincipal = new ArrayList<>();

        for (int hora = 7; hora <= 23; hora++) {
            String horaPrincipal = String.format("%02d:00", hora);
            horasPrincipales.add(horaPrincipal);

            // Añadir todas las horas con intervalos de 15 minutos
            todasLasHoras.add(String.format("%02d:00", hora));
            esHoraPrincipal.add(true);

            todasLasHoras.add(String.format("%02d:15", hora));
            esHoraPrincipal.add(false);

            todasLasHoras.add(String.format("%02d:30", hora));
            esHoraPrincipal.add(false);

            todasLasHoras.add(String.format("%02d:45", hora));
            esHoraPrincipal.add(false);
        }

        // Mantener la lista original de horas para compatibilidad
        List<String> horas = horasPrincipales;

        model.addAttribute("horasPrincipales", horasPrincipales);
        model.addAttribute("todasLasHoras", todasLasHoras);
        model.addAttribute("esHoraPrincipal", esHoraPrincipal);
        model.addAttribute("horas", horas);

        // Preparar datos de posicionamiento para cada asignación
        Map<Long, Map<String, String>> posicionesAsignaciones = new HashMap<>();

        for (AsignacionZona asignacion : asignaciones) {
            Map<String, String> posicion = calcularPosicion(asignacion.getInicio(), asignacion.getFin());
            posicionesAsignaciones.put(asignacion.getId(), posicion);
        }

        // Colores para los empleados
        Map<Long, String> coloresEmpleados = new HashMap<>();
        String[] colores = {
                "#3b82f6", "#22c55e", "#a855f7", "#f59e0b",
                "#f43f5e", "#06b6d4", "#6366f1", "#10b981"
        };

        int colorIndex = 0;
        for (Empleado empleado : empleados) {
            coloresEmpleados.put(empleado.getId(), colores[colorIndex % colores.length]);
            colorIndex++;
        }

        // Añadir datos al modelo
        List<AsignacionZona> asignacionZonas = asignacionZonaService.getAllAsignaciones();

        // Añadir datos al modelo
        model.addAttribute("asignacionesZona", asignacionZonas);
        model.addAttribute("zonas", zonas);
        model.addAttribute("diasSemana", diasSemana);
        model.addAttribute("asignacionesPorDia", asignacionesPorDia);
        model.addAttribute("posicionesAsignaciones", posicionesAsignaciones);
        model.addAttribute("coloresEmpleados", coloresEmpleados);

        // Asignar carriles para manejar superposiciones
        Map<Long, Integer> carrilesAsignaciones = asignarCarriles(asignaciones);
        model.addAttribute("carrilesAsignaciones", carrilesAsignaciones);

        // Calcular la altura necesaria para cada zona basada en el número máximo de carriles
        Map<Long, Integer> alturaZonas = new HashMap<>();
        for (Zona zona : zonas) {
            int maxCarril = 0;
            for (AsignacionZona asignacion : asignaciones) {
                if (asignacion.getZona().getId().equals(zona.getId())) {
                    int carril = carrilesAsignaciones.get(asignacion.getId());
                    if (carril > maxCarril) {
                        maxCarril = carril;
                    }
                }
            }
            // Altura mínima de 1 carril, máxima según se necesite
            alturaZonas.put(zona.getId(), Math.max(1, maxCarril + 1));
        }
        model.addAttribute("alturaZonas", alturaZonas);

        return "asignaciones-timeline";
    }

    // MODIFICADO: Método auxiliar para calcular la posición con mayor precisión
    private Map<String, String> calcularPosicion(LocalTime inicio, LocalTime fin) {
        Map<String, String> resultado = new HashMap<>();

        // Definir el rango de horas exacto (7:00 AM a 23:45 PM)
        LocalTime horaInicio = LocalTime.of(7, 0);
        LocalTime horaFin = LocalTime.of(23, 45);

        // Calcular minutos totales en el rango (1005 minutos = 16h45m)
        long minutosTotales = Duration.between(horaInicio, horaFin).toMinutes();

        // Asegurar que el inicio y fin estén dentro del rango
        LocalTime inicioAjustado = inicio.isBefore(horaInicio) ? horaInicio : inicio;
        LocalTime finAjustado = fin.isAfter(horaFin) ? horaFin : fin;

        // Calcular minutos desde el inicio del rango hasta el inicio de la asignación
        long minutosDesdeInicio = Duration.between(horaInicio, inicioAjustado).toMinutes();

        // Calcular duración exacta en minutos
        long duracionMinutos = Duration.between(inicioAjustado, finAjustado).toMinutes();

        // Calcular porcentajes para CSS con precisión de 4 decimales
        double left = (minutosDesdeInicio * 100.0) / minutosTotales;
        double width = (duracionMinutos * 100.0) / minutosTotales;

        // Usar valores exactos sin formateo para evitar problemas de precisión
        resultado.put("left", left + "%");
        resultado.put("width", width + "%");

        // Añadir información de depuración
        resultado.put("inicioReal", inicioAjustado.toString());
        resultado.put("finReal", finAjustado.toString());
        resultado.put("duracionMinutos", String.valueOf(duracionMinutos));

        return resultado;
    }

    // Método para asignar carriles a las asignaciones superpuestas
    private Map<Long, Integer> asignarCarriles(List<AsignacionZona> asignaciones) {
        Map<Long, Integer> carriles = new HashMap<>();

        // Agrupar por zona y día
        Map<Long, Map<String, List<AsignacionZona>>> asignacionesPorZonaDia = new HashMap<>();

        // Inicializar el mapa para cada zona y día
        for (AsignacionZona asignacion : asignaciones) {
            Long zonaId = asignacion.getZona().getId();
            String dia = asignacion.getDiaDeLaSemana();

            if (!asignacionesPorZonaDia.containsKey(zonaId)) {
                asignacionesPorZonaDia.put(zonaId, new HashMap<>());
            }

            Map<String, List<AsignacionZona>> porDia = asignacionesPorZonaDia.get(zonaId);
            if (!porDia.containsKey(dia)) {
                porDia.put(dia, new ArrayList<>());
            }

            porDia.get(dia).add(asignacion);
        }

        // Para cada zona y día, asignar carriles
        for (Map.Entry<Long, Map<String, List<AsignacionZona>>> entryZona : asignacionesPorZonaDia.entrySet()) {
            for (Map.Entry<String, List<AsignacionZona>> entryDia : entryZona.getValue().entrySet()) {
                List<AsignacionZona> asignacionesZonaDia = entryDia.getValue();

                // Ordenar por hora de inicio
                asignacionesZonaDia.sort(Comparator.comparing(AsignacionZona::getInicio));

                // Lista de "fin de carril" para cada carril
                List<LocalTime> finCarriles = new ArrayList<>();

                // Procesar cada asignación
                for (AsignacionZona asignacion : asignacionesZonaDia) {
                    // Buscar un carril disponible
                    int carril = 0;
                    boolean asignado = false;

                    for (int i = 0; i < finCarriles.size(); i++) {
                        // Si la hora de inicio es posterior al fin del carril, podemos usar este carril
                        if (asignacion.getInicio().isAfter(finCarriles.get(i)) ||
                                asignacion.getInicio().equals(finCarriles.get(i))) {
                            carril = i;
                            asignado = true;
                            // Actualizar el fin del carril
                            finCarriles.set(i, asignacion.getFin());
                            break;
                        }
                    }

                    // Si no encontramos carril disponible, crear uno nuevo
                    if (!asignado) {
                        carril = finCarriles.size();
                        finCarriles.add(asignacion.getFin());
                    }

                    // Asignar el carril a la asignación
                    carriles.put(asignacion.getId(), carril);
                }
            }
        }

        return carriles;
    }



















    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        // Preparar el formulario con los datos necesarios

        // Definir los días de la semana
        List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

        model.addAttribute("asignacionZona", new AsignacionZona());
        model.addAttribute("empleados", empleadoRepository.findAll());
        model.addAttribute("zonas", zonaRepository.findAll());
        model.addAttribute("periodos", periodoRepository.findAll());
        model.addAttribute("diasSemana", diasSemana);





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
        model.addAttribute("horarios", horarioService.findAll());


        return "asignacioneszonaFormulario";
    }

    @PostMapping("/guardar")
    public String guardarAsignacionZona(
            @ModelAttribute  AsignacionZona asignacionZona,
            BindingResult result,
            @RequestParam("inicioHora") @DateTimeFormat(pattern = "HH:mm") LocalTime inicioHora,
            @RequestParam("finHora") @DateTimeFormat(pattern = "HH:mm") LocalTime finHora,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Definir los días de la semana
        List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");


        // Si hay errores en la validación, volver al formulario
        if (result.hasErrors()) {
            model.addAttribute("empleados", empleadoRepository.findAll());
            model.addAttribute("zonas", zonaRepository.findAll());
            model.addAttribute("periodos", periodoRepository.findAll());
            model.addAttribute("diasSemana", diasSemana);
            return "asignacioneszonaFormulario";
        }

        // Establecer las horas de inicio y fin
        asignacionZona.setInicio(inicioHora);
        asignacionZona.setFin(finHora);

        // Guardar la asignación
        asignacionZonaRepository.save(asignacionZona);

        redirectAttributes.addFlashAttribute("mensaje", "Asignación de zona guardada correctamente");
        return "redirect:/asignacionesZona";
    }






    @GetMapping("/eliminar/{id}")
    public String eliminarAsignacionZona(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AsignacionZona asignacionZona = asignacionZonaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de asignación inválido: " + id));

        asignacionZonaRepository.delete(asignacionZona);
        redirectAttributes.addFlashAttribute("mensaje", "Asignación de zona eliminada correctamente");

        return "redirect:/asignacionesZona";
    }






}

