package com.idea.service;

import com.idea.entity.*;
import com.idea.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AsignacionZonaService {
    @Autowired
    private AsignacionZonaRepository asignacionZonaRepository;
@Autowired
private CategoriaHorarioRepository categoriaHorarioRepository;
@Autowired
private CategoriaHorarioDetalleRepository categoriaHorarioDetalleRepository;


    @Autowired
    private ZonaObligatoriaRepository zonaObligatoriaRepository;

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private AsignacionHorarioRepository asignacionHorarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PeriodoRepository periodoRepository;


    public List<AsignacionZona> getAllAsignaciones() {
        return asignacionZonaRepository.findAll();
    }

    public Optional<AsignacionZona> getAsignacionById(Long id) {
        return asignacionZonaRepository.findById(id);
    }

    public AsignacionZona saveAsignacion(AsignacionZona asignacionZona) {
        return asignacionZonaRepository.save(asignacionZona);
    }

    public void deleteAsignacion(Long id) {
        asignacionZonaRepository.deleteById(id);
    }

    public List<AsignacionHorario> getAsignacionesPorEmpleadoYDia(Long empleadoId, String diaDeLaSemana) {
        return asignacionHorarioRepository.findByEmpleadoAndDiaDeLaSemana(empleadoId, diaDeLaSemana);
    }

    @Transactional
    public void eliminarTodasLasAsignaciones() {
        asignacionZonaRepository.deleteAll();
    }







//
//    public void generarAsignacionesAutomaticas() {
//        List<String> diasSemana = List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
//
//        Periodo almuerzo = periodoRepository.findByNombre("Almuerzo"); // 08:00 - 16:00
//        Periodo cena = periodoRepository.findByNombre("Cena"); // 16:00 - 23:00
//
//        for (String dia : diasSemana) {
//            List<ZonaObligatoria> zonasObligatorias = zonaObligatoriaRepository.findByDiaDeLaSemana(dia);
//            List<Zona> todasLasZonas = zonaRepository.findAll();
//            List<Empleado> empleadosDisponibles = empleadoRepository.findEmpleadosDisponiblesPorDia(dia);
//
//            Set<Zona> zonasObligatoriasAsignadas = new HashSet<>(); // 🔹 Rastrea qué zonas obligatorias ya fueron asignadas
//            Map<Empleado, Zona> zonasAsignadas = new HashMap<>();
//
//            for (Empleado empleado : empleadosDisponibles) {
//                List<AsignacionHorario> asignacionesEmpleado = asignacionHorarioRepository.findByEmpleadoAndDiaDeLaSemana(empleado.getId(), dia);
//
//                for (AsignacionHorario asignacion : asignacionesEmpleado) {
//                    Horario horario = asignacion.getHorario();
//                    LocalTime inicioEmpleado = horario.getInicio();
//                    LocalTime finEmpleado = horario.getFin();
//
//                    // 🔹 Intentamos asignarle una zona obligatoria si hay disponible
//                    Zona zonaAsignada = null;
//                    for (ZonaObligatoria zonaObligatoria : zonasObligatorias) {
//                        Zona zona = zonaObligatoria.getZona();
//                        if (!zonasObligatoriasAsignadas.contains(zona)) { // 🔥 Solo si la zona aún no está ocupada
//                            zonaAsignada = zona;
//                            zonasObligatoriasAsignadas.add(zona);
//                            break;
//                        }
//                    }
//
//                    // 🔹 Si no hay zonas obligatorias disponibles, asignamos una zona aleatoria
//                    if (zonaAsignada == null) {
//                        List<Zona> zonasDisponibles = todasLasZonas.stream()
//                                .filter(z -> !zonasObligatoriasAsignadas.contains(z) && !zonasAsignadas.containsValue(z))
//                                .collect(Collectors.toList());
//
//                        if (!zonasDisponibles.isEmpty()) {
//                            zonaAsignada = zonasDisponibles.get((int) (Math.random() * zonasDisponibles.size()));
//                        }
//                    }
//
//                    // 🔹 Si el empleado aún no tiene una zona, continuar con el siguiente
//                    if (zonaAsignada == null) continue;
//
//                    zonasAsignadas.put(empleado, zonaAsignada); // Guardamos la asignación
//
//                    // 🔥 Asignar en el almuerzo si el horario coincide
//                    if (inicioEmpleado.isBefore(almuerzo.getFin()) && finEmpleado.isAfter(almuerzo.getInicio())) {
//                        LocalTime inicioAsignacion = inicioEmpleado.isBefore(almuerzo.getInicio()) ? almuerzo.getInicio() : inicioEmpleado;
//                        LocalTime finAsignacion = finEmpleado.isAfter(almuerzo.getFin()) ? almuerzo.getFin() : finEmpleado;
//                        crearAsignacion(empleado, zonaAsignada, dia, inicioAsignacion, finAsignacion, almuerzo);
//                    }
//
//                    // 🔥 Asignar en la cena si el horario coincide
//                    if (inicioEmpleado.isBefore(cena.getFin()) && finEmpleado.isAfter(cena.getInicio())) {
//                        LocalTime inicioAsignacion = inicioEmpleado.isBefore(cena.getInicio()) ? cena.getInicio() : inicioEmpleado;
//                        LocalTime finAsignacion = finEmpleado.isAfter(cena.getFin()) ? cena.getFin() : finEmpleado;
//                        crearAsignacion(empleado, zonaAsignada, dia, inicioAsignacion, finAsignacion, cena);
//                    }
//                }
//            }
//        }
//    }
//
//
//
//
//    // 🔹 Método para evitar duplicación de código
//    private void crearAsignacion(Empleado empleado, Zona zona, String dia, LocalTime inicio, LocalTime fin, Periodo periodo) {
//        boolean conflicto = asignacionZonaRepository.existsByZonaAndDiaDeLaSemanaAndInicioBeforeAndFinAfter(
//                zona, dia, fin, inicio);
//
//        if (!conflicto) {
//            AsignacionZona nuevaAsignacion = new AsignacionZona();
//            nuevaAsignacion.setEmpleado(empleado);
//            nuevaAsignacion.setZona(zona);
//            nuevaAsignacion.setDiaDeLaSemana(dia);
//            nuevaAsignacion.setInicio(inicio);
//            nuevaAsignacion.setFin(fin);
//            nuevaAsignacion.setPeriodo(periodo); // ✅ Se asigna el período correspondiente
//
//            asignacionZonaRepository.save(nuevaAsignacion);
//        }
//    }
//










    /**
     * Genera asignaciones automáticas de zonas para empleados según su categoría de horario
     * y las reglas de negocio establecidas.
     */
    public void generarAsignacionesAutomaticas() {
        List<String> diasSemana = List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

        Periodo almuerzo = periodoRepository.findByNombre("Almuerzo");
        Periodo cena = periodoRepository.findByNombre("Cena");

        // Obtenemos las categorías de horario
        CategoriaHorario categoriaApertura = categoriaHorarioRepository.findByNombre("Apertura");
        CategoriaHorario categoriaCubrir = categoriaHorarioRepository.findByNombre("Cubrir");

        for (String dia : diasSemana) {
            List<Empleado> empleadosDisponibles = empleadoRepository.findEmpleadosDisponiblesPorDia(dia);

            // Mapas para rastrear asignaciones
            Map<GrupoZona, List<AsignacionZona>> asignacionesPorGrupoZona = new HashMap<>();
            Map<Empleado, Zona> zonasAsignadas = new HashMap<>();

            // Mapa para contar cuántos empleados hay por zona
            Map<Zona, Integer> contadorEmpleadosPorZona = new HashMap<>();

            // Conjunto para rastrear qué zonas ya tienen una persona asignada
            Set<Zona> zonasOcupadas = new HashSet<>();

            // Mapa para rastrear qué grupos de zona tienen empleados de apertura
            Map<GrupoZona, Set<Zona>> zonasAperturasPorGrupo = new HashMap<>();

            // Mapa para rastrear qué grupos de zona ya tienen un empleado de cubrir asignado
            Set<GrupoZona> gruposConCubrir = new HashSet<>();

            // PASO 1: Asignar empleados con categoría "Apertura" a grupos específicos
            List<Empleado> empleadosApertura = filtrarEmpleadosPorCategoriaHorario(empleadosDisponibles, dia, categoriaApertura);

            for (Empleado empleado : empleadosApertura) {
                // Si este empleado ya fue asignado, continuamos con el siguiente
                if (zonasAsignadas.containsKey(empleado)) continue;

                // Obtenemos los horarios del empleado para este día
                List<AsignacionHorario> asignacionesHorario = asignacionHorarioRepository.findByEmpleadoAndDiaDeLaSemana(empleado.getId(), dia);

                // Para cada horario, buscamos los grupos de zonas configurados para este empleado
                for (AsignacionHorario asignacionHorario : asignacionesHorario) {
                    Horario horario = asignacionHorario.getHorario();

                    // Obtenemos los detalles de categoría para este horario específico
                    List<CategoriaHorarioDetalle> detalles = categoriaHorarioDetalleRepository
                            .findByCategoriaHorarioAndHorario(categoriaApertura, horario);

                    // Si no hay detalles configurados, continuamos con el siguiente horario
                    if (detalles.isEmpty()) continue;

                    // Intentamos asignar al empleado a uno de los grupos de zonas configurados
                    boolean asignado = false;

                    for (CategoriaHorarioDetalle detalle : detalles) {
                        // Si no hay grupo de zona configurado, continuamos
                        if (detalle.getGrupoZona() == null) continue;

                        GrupoZona grupoZona = detalle.getGrupoZona();

                        // Verificamos si hay zonas disponibles en este grupo
                        List<Zona> zonasDisponibles = grupoZona.getZonas().stream()
                                .filter(zona -> !zonasOcupadas.contains(zona))
                                .collect(Collectors.toList());

                        if (zonasDisponibles.isEmpty()) continue;

                        // Asignamos una zona al empleado
                        Zona zonaAsignada = zonasDisponibles.get(0);
                        zonasOcupadas.add(zonaAsignada);
                        zonasAsignadas.put(empleado, zonaAsignada);

                        // Incrementamos el contador de empleados para esta zona
                        contadorEmpleadosPorZona.put(zonaAsignada, contadorEmpleadosPorZona.getOrDefault(zonaAsignada, 0) + 1);

                        // Registramos esta zona como una zona de apertura para este grupo
                        zonasAperturasPorGrupo.computeIfAbsent(grupoZona, k -> new HashSet<>()).add(zonaAsignada);

                        // Creamos la asignación con el horario original exacto
                        Periodo periodoAsignar = determinarPeriodo(horario.getInicio(), horario.getFin(), almuerzo, cena);
                        AsignacionZona asignacion = crearAsignacion(empleado, zonaAsignada, dia,
                                horario.getInicio(), horario.getFin(), periodoAsignar);

                        // Guardamos la asignación en el mapa por grupo de zona
                        if (asignacion != null) {
                            asignacionesPorGrupoZona.computeIfAbsent(grupoZona, k -> new ArrayList<>()).add(asignacion);
                            asignado = true;
                            break;  // Una vez asignado, salimos del bucle de detalles
                        }
                    }

                    if (asignado) break;  // Si ya fue asignado, pasamos al siguiente empleado
                }
            }

            // PASO 2: Asignar empleados con categoría "Cubrir" - máximo uno por grupo de zona con "Apertura"
            List<Empleado> empleadosCubrir = filtrarEmpleadosPorCategoriaHorario(empleadosDisponibles, dia, categoriaCubrir);
            List<Empleado> empleadosCubrirSobrantes = new ArrayList<>();

            for (Empleado empleado : empleadosCubrir) {
                // Si este empleado ya fue asignado, continuamos con el siguiente
                if (zonasAsignadas.containsKey(empleado)) continue;

                // Obtenemos los horarios del empleado para este día
                List<AsignacionHorario> asignacionesHorario = asignacionHorarioRepository.findByEmpleadoAndDiaDeLaSemana(empleado.getId(), dia);

                // Para cada horario, buscamos los grupos de zonas configurados para este empleado
                boolean asignadoAGrupo = false;

                for (AsignacionHorario asignacionHorario : asignacionesHorario) {
                    Horario horario = asignacionHorario.getHorario();

                    // Obtenemos los detalles de categoría para este horario específico
                    List<CategoriaHorarioDetalle> detalles = categoriaHorarioDetalleRepository
                            .findByCategoriaHorarioAndHorario(categoriaCubrir, horario);

                    // Si no hay detalles configurados, continuamos con el siguiente horario
                    if (detalles.isEmpty()) continue;

                    // Intentamos asignar al empleado a uno de los grupos de zonas configurados
                    boolean asignado = false;

                    for (CategoriaHorarioDetalle detalle : detalles) {
                        // Si no hay grupo de zona configurado, continuamos
                        if (detalle.getGrupoZona() == null) continue;

                        GrupoZona grupoZona = detalle.getGrupoZona();

                        // Verificamos si este grupo tiene zonas de apertura y aún no tiene un empleado de cubrir
                        if (!zonasAperturasPorGrupo.containsKey(grupoZona) || gruposConCubrir.contains(grupoZona)) continue;

                        // Buscamos zonas disponibles en este grupo
                        List<Zona> zonasDisponibles = grupoZona.getZonas().stream()
                                .filter(zona -> !zonasOcupadas.contains(zona))
                                .collect(Collectors.toList());

                        if (zonasDisponibles.isEmpty()) continue;

                        // Asignamos una zona al empleado
                        Zona zonaAsignada = zonasDisponibles.get(0);
                        zonasOcupadas.add(zonaAsignada);
                        zonasAsignadas.put(empleado, zonaAsignada);

                        // Incrementamos el contador de empleados para esta zona
                        contadorEmpleadosPorZona.put(zonaAsignada, contadorEmpleadosPorZona.getOrDefault(zonaAsignada, 0) + 1);

                        // Marcamos este grupo como que ya tiene un empleado de cubrir
                        gruposConCubrir.add(grupoZona);

                        // Creamos la asignación con el horario original exacto
                        Periodo periodoAsignar = determinarPeriodo(horario.getInicio(), horario.getFin(), almuerzo, cena);
                        AsignacionZona asignacion = crearAsignacion(empleado, zonaAsignada, dia,
                                horario.getInicio(), horario.getFin(), periodoAsignar);

                        // Guardamos la asignación en el mapa por grupo de zona
                        if (asignacion != null) {
                            asignacionesPorGrupoZona.computeIfAbsent(grupoZona, k -> new ArrayList<>()).add(asignacion);
                            asignado = true;
                            asignadoAGrupo = true;
                            break;  // Una vez asignado, salimos del bucle de detalles
                        }
                    }

                    if (asignado) break;  // Si ya fue asignado, pasamos al siguiente empleado
                }

                // Si este empleado de cubrir no pudo ser asignado a ningún grupo, lo agregamos a la lista de sobrantes
                if (!asignadoAGrupo && !zonasAsignadas.containsKey(empleado)) {
                    empleadosCubrirSobrantes.add(empleado);
                }
            }

            // PASO 3: Asignar al azar las personas restantes (incluidos los "Cubrir" sobrantes) en zonas disponibles
            List<Empleado> empleadosSinAsignar = empleadosDisponibles.stream()
                    .filter(empleado -> !zonasAsignadas.containsKey(empleado))
                    .collect(Collectors.toList());

            // Añadimos los empleados de cubrir sobrantes a la lista de empleados sin asignar
            empleadosSinAsignar.addAll(empleadosCubrirSobrantes);

            List<Zona> todasLasZonas = zonaRepository.findAll();

            // Primero asignamos a zonas que aún no tienen ninguna persona
            List<Zona> zonasLibres = todasLasZonas.stream()
                    .filter(zona -> !zonasOcupadas.contains(zona))
                    .collect(Collectors.toList());

            // Distribuimos equitativamente los empleados en las zonas libres
            while (!empleadosSinAsignar.isEmpty() && !zonasLibres.isEmpty()) {
                // Tomamos un empleado aleatorio de la lista
                int indiceEmpleado = (int) (Math.random() * empleadosSinAsignar.size());
                Empleado empleado = empleadosSinAsignar.get(indiceEmpleado);

                // Obtenemos los horarios del empleado para este día
                List<AsignacionHorario> asignacionesHorario = asignacionHorarioRepository.findByEmpleadoAndDiaDeLaSemana(empleado.getId(), dia);

                if (asignacionesHorario.isEmpty()) {
                    // Si no tiene horarios, lo removemos y continuamos con el siguiente
                    empleadosSinAsignar.remove(empleado);
                    continue;
                }

                // Tomamos el primer horario disponible
                AsignacionHorario asignacionHorario = asignacionesHorario.get(0);
                Horario horario = asignacionHorario.getHorario();

                // Determinamos el período para clasificación, pero NO para ajustar tiempos
                Periodo periodoAsignar = determinarPeriodo(horario.getInicio(), horario.getFin(), almuerzo, cena);

                // Asignamos una zona aleatoria de las disponibles
                int indiceZona = (int) (Math.random() * zonasLibres.size());
                Zona zonaAsignada = zonasLibres.get(indiceZona);

                // Marcamos la zona como ocupada y la eliminamos de las zonas libres
                zonasOcupadas.add(zonaAsignada);
                zonasLibres.remove(zonaAsignada);

                // Incrementamos el contador de empleados para esta zona
                contadorEmpleadosPorZona.put(zonaAsignada, contadorEmpleadosPorZona.getOrDefault(zonaAsignada, 0) + 1);

                zonasAsignadas.put(empleado, zonaAsignada);

                // IMPORTANTE: Usamos el horario original sin modificaciones
                AsignacionZona asignacion = crearAsignacion(empleado, zonaAsignada, dia,
                        horario.getInicio(), horario.getFin(), periodoAsignar);

                // Si la asignación fue exitosa, removemos al empleado de la lista de sin asignar
                if (asignacion != null) {
                    empleadosSinAsignar.remove(empleado);
                }
            }

            // PASO 4: Si todas las zonas ya tienen una persona, asignar equitativamente a los empleados sobrantes
            if (!empleadosSinAsignar.isEmpty()) {
                // Ordenamos todas las zonas por cantidad de empleados (preferimos las que tienen menos)
                List<Zona> zonasOrdenadas = todasLasZonas.stream()
                        .sorted(Comparator.comparingInt(zona -> contadorEmpleadosPorZona.getOrDefault(zona, 0)))
                        .collect(Collectors.toList());

                for (Empleado empleado : empleadosSinAsignar) {
                    // Obtenemos los horarios del empleado para este día
                    List<AsignacionHorario> asignacionesHorario = asignacionHorarioRepository.findByEmpleadoAndDiaDeLaSemana(empleado.getId(), dia);

                    if (asignacionesHorario.isEmpty()) continue;

                    // Tomamos el primer horario disponible
                    AsignacionHorario asignacionHorario = asignacionesHorario.get(0);
                    Horario horario = asignacionHorario.getHorario();

                    // Determinamos el período para clasificación, pero NO para ajustar tiempos
                    Periodo periodoAsignar = determinarPeriodo(horario.getInicio(), horario.getFin(), almuerzo, cena);

                    // Asignamos a la zona con menos empleados
                    Zona zonaAsignada = zonasOrdenadas.get(0);

                    // Incrementamos el contador de empleados para esta zona
                    contadorEmpleadosPorZona.put(zonaAsignada, contadorEmpleadosPorZona.getOrDefault(zonaAsignada, 0) + 1);

                    // Reordenamos la lista después de incrementar el contador
                    Collections.sort(zonasOrdenadas, Comparator.comparingInt(zona -> contadorEmpleadosPorZona.getOrDefault(zona, 0)));

                    zonasAsignadas.put(empleado, zonaAsignada);

                    // Creamos la asignación permitiendo múltiples empleados en la misma zona
                    AsignacionZona asignacion = crearAsignacionMultiple(empleado, zonaAsignada, dia,
                            horario.getInicio(), horario.getFin(), periodoAsignar);

                    // No necesitamos verificar si la asignación fue exitosa, ya que crearAsignacionMultiple siempre devuelve una asignación
                }
            }
        }
    }

    /**
     * Filtra empleados que tienen horarios asignados a una categoría específica
     * @param empleados Lista de empleados a filtrar
     * @param dia Día de la semana
     * @param categoria Categoría de horario a buscar
     * @return Lista de empleados que tienen al menos un horario en la categoría especificada
     */
    private List<Empleado> filtrarEmpleadosPorCategoriaHorario(List<Empleado> empleados, String dia, CategoriaHorario categoria) {
        return empleados.stream()
                .filter(empleado -> {
                    List<AsignacionHorario> asignaciones = asignacionHorarioRepository.findByEmpleadoAndDiaDeLaSemana(empleado.getId(), dia);
                    for (AsignacionHorario asignacion : asignaciones) {
                        List<CategoriaHorarioDetalle> detalles = categoriaHorarioDetalleRepository
                                .findByCategoriaHorarioAndHorario(categoria, asignacion.getHorario());
                        if (!detalles.isEmpty()) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * Determina el periodo (almuerzo o cena) al que corresponde un horario
     * @param inicio Hora de inicio del horario
     * @param fin Hora de fin del horario
     * @param almuerzo Periodo de almuerzo
     * @param cena Periodo de cena
     * @return El periodo que mejor coincide con el horario
     */
    private Periodo determinarPeriodo(LocalTime inicio, LocalTime fin, Periodo almuerzo, Periodo cena) {
        boolean coincideAlmuerzo = inicio.isBefore(almuerzo.getFin()) && fin.isAfter(almuerzo.getInicio());
        boolean coincideCena = inicio.isBefore(cena.getFin()) && fin.isAfter(cena.getInicio());

        if (coincideAlmuerzo && coincideCena) {
            // Si coincide con ambos, devolvemos el que tiene mayor solapamiento
            long minutosAlmuerzo = calcularMinutosSolapamiento(inicio, fin, almuerzo.getInicio(), almuerzo.getFin());
            long minutosCena = calcularMinutosSolapamiento(inicio, fin, cena.getInicio(), cena.getFin());
            return minutosAlmuerzo > minutosCena ? almuerzo : cena;
        } else if (coincideAlmuerzo) {
            return almuerzo;
        } else if (coincideCena) {
            return cena;
        } else {
            // Si no coincide con ninguno, devolvemos almuerzo por defecto
            return almuerzo;
        }
    }

    /**
     * Calcula los minutos de solapamiento entre dos intervalos de tiempo
     * @param inicio1 Inicio del primer intervalo
     * @param fin1 Fin del primer intervalo
     * @param inicio2 Inicio del segundo intervalo
     * @param fin2 Fin del segundo intervalo
     * @return Cantidad de minutos de solapamiento entre los intervalos
     */
    private long calcularMinutosSolapamiento(LocalTime inicio1, LocalTime fin1, LocalTime inicio2, LocalTime fin2) {
        LocalTime inicioSolapamiento = inicio1.isBefore(inicio2) ? inicio2 : inicio1;
        LocalTime finSolapamiento = fin1.isAfter(fin2) ? fin2 : fin1;

        if (inicioSolapamiento.isAfter(finSolapamiento)) {
            return 0;
        }

        return ChronoUnit.MINUTES.between(inicioSolapamiento, finSolapamiento);
    }

    /**
     * Crea y guarda una asignación de zona si no hay conflictos
     * @param empleado Empleado a asignar
     * @param zona Zona a asignar
     * @param dia Día de la semana
     * @param inicio Hora de inicio
     * @param fin Hora de fin
     * @param periodo Periodo (almuerzo o cena)
     * @return La asignación creada o null si hay conflicto
     */
    private AsignacionZona crearAsignacion(Empleado empleado, Zona zona, String dia, LocalTime inicio, LocalTime fin, Periodo periodo) {
        boolean conflicto = asignacionZonaRepository.existsByZonaAndDiaDeLaSemanaAndInicioBeforeAndFinAfter(
                zona, dia, fin, inicio);

        if (!conflicto) {
            AsignacionZona nuevaAsignacion = new AsignacionZona();
            nuevaAsignacion.setEmpleado(empleado);
            nuevaAsignacion.setZona(zona);
            nuevaAsignacion.setDiaDeLaSemana(dia);
            nuevaAsignacion.setInicio(inicio);
            nuevaAsignacion.setFin(fin);
            nuevaAsignacion.setPeriodo(periodo);

            return asignacionZonaRepository.save(nuevaAsignacion);
        }

        return null;
    }

    /**
     * Crea y guarda una asignación de zona permitiendo múltiples empleados en la misma zona
     * @param empleado Empleado a asignar
     * @param zona Zona a asignar
     * @param dia Día de la semana
     * @param inicio Hora de inicio
     * @param fin Hora de fin
     * @param periodo Periodo (almuerzo o cena)
     * @return La asignación creada
     */
    private AsignacionZona crearAsignacionMultiple(Empleado empleado, Zona zona, String dia, LocalTime inicio, LocalTime fin, Periodo periodo) {
        // Aquí no verificamos conflictos, permitimos múltiples empleados en la misma zona
        AsignacionZona nuevaAsignacion = new AsignacionZona();
        nuevaAsignacion.setEmpleado(empleado);
        nuevaAsignacion.setZona(zona);
        nuevaAsignacion.setDiaDeLaSemana(dia);
        nuevaAsignacion.setInicio(inicio);
        nuevaAsignacion.setFin(fin);
        nuevaAsignacion.setPeriodo(periodo);

        return asignacionZonaRepository.save(nuevaAsignacion);
    }

}
