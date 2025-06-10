package com.idea.service;

import com.idea.entity.AsignacionHorario;
import com.idea.entity.DetallePlantillaHorario;
import com.idea.entity.Empleado;
import com.idea.repository.AsignacionHorarioRepository;
import com.idea.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private AsignacionHorarioService asignacionHorarioService;

    @Autowired
    private DetallePlantillaService detallePlantillaHorarioService;

    public List<Empleado> findAll() {
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> findById(Long id) {
        return empleadoRepository.findById(id);
    }

    @Transactional
    public Empleado save(Empleado empleado) {
        Empleado savedEmpleado = empleadoRepository.save(empleado);
        generarAsignaciones(savedEmpleado);
        return savedEmpleado;
    }

    @Transactional
    public Empleado update(Empleado empleado) {
        Empleado empleadoExistente = empleadoRepository.findById(empleado.getId()).orElse(null);

        if (empleadoExistente != null) {
            empleadoExistente.setNombre(empleado.getNombre());
            empleadoExistente.setPlantillaHorario(empleado.getPlantillaHorario());

            empleadoRepository.save(empleadoExistente);

            // Eliminar horarios antiguos y regenerar nuevos
            asignacionHorarioService.deleteByEmpleado(empleadoExistente);
            generarAsignaciones(empleadoExistente);
        }
        return empleadoExistente;
    }

    public void deleteById(Long id) {
        empleadoRepository.deleteById(id);
    }

    private void generarAsignaciones(Empleado empleado) {
        if (empleado.getPlantillaHorario() != null) {
            List<DetallePlantillaHorario> detalles = detallePlantillaHorarioService.findByPlantillaHorario(empleado.getPlantillaHorario());

            for (DetallePlantillaHorario detalle : detalles) {
                AsignacionHorario asignacion = new AsignacionHorario();
                asignacion.setEmpleado(empleado);
                asignacion.setHorario(detalle.getHorario());
                asignacion.setDiaDeLaSemana(detalle.getDiaDeLaSemana());
                asignacion.setPeriodo(detalle.getHorario().getPeriodo());
                asignacionHorarioService.save(asignacion);
            }
        }
    }


@Autowired
private AsignacionHorarioRepository asignacionHorarioRepository;

    public void eliminarAsignacionesPorEmpleadoId(Long empleadoId) {
        asignacionHorarioRepository.eliminarPorEmpleadoId(empleadoId);
    }
}
