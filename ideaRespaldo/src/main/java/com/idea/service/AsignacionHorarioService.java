package com.idea.service;

import com.idea.entity.AsignacionResumenDTO;
import com.idea.entity.AsignacionHorario;
import com.idea.entity.Empleado;
import com.idea.repository.AsignacionHorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AsignacionHorarioService {
    @Autowired
    private AsignacionHorarioRepository asignacionHorarioRepository;

    public List<AsignacionHorario> findAll() {
        return asignacionHorarioRepository.findAll();
    }

    public Optional<AsignacionHorario> findById(Long id) {
        return asignacionHorarioRepository.findById(id);
    }

    public AsignacionHorario save(AsignacionHorario asignacionHorario) {
        return asignacionHorarioRepository.save(asignacionHorario);
    }

    public void deleteById(Long id) {
        asignacionHorarioRepository.deleteById(id);
    }

    public void deleteByEmpleado(Empleado empleado) {
        asignacionHorarioRepository.deleteByEmpleado(empleado);
    }

    public List<AsignacionHorario> obtenerHorarioSemanal(Long empleadoId) {
        return asignacionHorarioRepository.findByEmpleadoId(empleadoId);
    }

    public Map<String, Long> obtenerEstadisticasPorDia(String diaDeLaSemana) {
        Map<String, Long> estadisticas = new HashMap<>();

        Long almuerzo = asignacionHorarioRepository.contarAsignacionesPorDiaYPeriodo(diaDeLaSemana, "Almuerzo");
        System.out.println("Cantidad de asignaciones para Almuerzo en " + diaDeLaSemana + ": " + almuerzo);

        Long cena = asignacionHorarioRepository.contarAsignacionesPorDiaYPeriodo(diaDeLaSemana, "Cena");
        Long ambos = asignacionHorarioRepository.contarAsignacionesPorDiaYPeriodo(diaDeLaSemana, "Ambos");

        estadisticas.put("Almuerzo", almuerzo);
        estadisticas.put("Cena", cena);
        estadisticas.put("Ambos", ambos);

        return estadisticas;
    }

    public List<Map<String, Object>> obtenerConteoAsignacionesPorDiaYPeriodo() {
        List<Object[]> resultados = asignacionHorarioRepository.contarAsignacionesPorDiaYPeriodo();
        List<Map<String, Object>> conteo = new ArrayList<>();

        for (Object[] fila : resultados) {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("diaDeLaSemana", fila[0]);
            mapa.put("periodo", fila[1]);
            mapa.put("cantidad", fila[2]);
            conteo.add(mapa);
        }

        return conteo;
    }


    public List<AsignacionResumenDTO> obtenerResumenAsignaciones() {
        List<Object[]> resultados = asignacionHorarioRepository.contarAsignacionesPorPeriodoYDia();
        List<AsignacionResumenDTO> resumen = new ArrayList<>();

        for (Object[] resultado : resultados) {
            String periodo = (String) resultado[0];
            String diaDeLaSemana = (String) resultado[1];
            Long cantidad = ((Number) resultado[2]).longValue();

            resumen.add(new AsignacionResumenDTO(periodo, diaDeLaSemana, cantidad));
        }

        return resumen;
    }

    public Map<String, Map<String, Long>> obtenerMatrizAsignaciones() {
        List<AsignacionResumenDTO> resumen = obtenerResumenAsignaciones();
        Map<String, Map<String, Long>> matriz = new HashMap<>();

        // Inicializar la matriz
        for (AsignacionResumenDTO dto : resumen) {
            if (!matriz.containsKey(dto.getPeriodo())) {
                matriz.put(dto.getPeriodo(), new HashMap<>());
            }
        }

        // Llenar la matriz con los valores
        for (AsignacionResumenDTO dto : resumen) {
            matriz.get(dto.getPeriodo()).put(dto.getDiaDeLaSemana(), dto.getCantidad());
        }

        return matriz;
    }

    public List<String> obtenerDiasSemana() {
        List<String> dias = new ArrayList<>();
        dias.add("Lunes");
        dias.add("Martes");
        dias.add("Miércoles");
        dias.add("Jueves");
        dias.add("Viernes");
        dias.add("Sábado");
        dias.add("Domingo");
        return dias;
    }
}




