package com.idea.service;

import com.idea.entity.PlantillaHorario;
import com.idea.repository.DetallePlantillaRepository;
import com.idea.repository.PlantillaHorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class PlantillaHorarioService {
    @Autowired
    private PlantillaHorarioRepository plantillaHorarioRepository;
    @Autowired
    private DetallePlantillaRepository plantillaDetalleRepository;

    public List<PlantillaHorario> findAll() {
        return plantillaHorarioRepository.findAll();
    }

    public Optional<PlantillaHorario> findById(Long id) {
        return plantillaHorarioRepository.findById(id);
    }

    public PlantillaHorario save(PlantillaHorario plantillaHorario) {
        return plantillaHorarioRepository.save(plantillaHorario);
    }

    public void deleteById(Long id) {
        plantillaHorarioRepository.deleteById(id);
    }

    // Nuevo método para obtener el resumen de horarios por plantilla
    public Map<String, Object> obtenerResumenPlantilla(Long plantillaId) {
        Map<String, Object> resultado = new HashMap<>();

        // Obtener la información básica de la plantilla
        Optional<PlantillaHorario> plantillaOpt = plantillaHorarioRepository.findById(plantillaId);
        if (!plantillaOpt.isPresent()) {
            resultado.put("success", false);
            resultado.put("message", "Plantilla no encontrada");
            return resultado;
        }

        PlantillaHorario plantilla = plantillaOpt.get();
        resultado.put("plantilla", Map.of(
                "id", plantilla.getId(),
                "nombre", plantilla.getNombre()
        ));

        // Obtener los detalles de horario directamente desde el repositorio
        List<Map<String, Object>> detalles = plantillaDetalleRepository
                .obtenerResumenHorariosPorPlantilla(plantillaId);

        // Organizar los detalles por día de la semana
        List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        Map<String, List<Map<String, Object>>> detallesPorDia = new LinkedHashMap<>();

        // Inicializar el mapa con listas vacías para cada día
        for (String dia : diasSemana) {
            detallesPorDia.put(dia, new ArrayList<>());
        }

        // Agrupar los detalles por día
        for (Map<String, Object> detalle : detalles) {
            String dia = (String) detalle.get("dia");
            detallesPorDia.get(dia).add(detalle);
        }

        resultado.put("detallesPorDia", detallesPorDia);
        resultado.put("success", true);

        return resultado;
    }
}