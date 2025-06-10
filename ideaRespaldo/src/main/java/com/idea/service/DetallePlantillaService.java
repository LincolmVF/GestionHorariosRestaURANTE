package com.idea.service;


import com.idea.entity.DetallePlantillaHorario;
import com.idea.entity.PlantillaHorario;
import com.idea.repository.DetallePlantillaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DetallePlantillaService {
    @Autowired
    private DetallePlantillaRepository detallePlantillaHorarioRepository;

    public List<DetallePlantillaHorario> findAll() {
        return detallePlantillaHorarioRepository.findAll();
    }

    public Optional<DetallePlantillaHorario> findById(Long id) {
        return detallePlantillaHorarioRepository.findById(id);
    }

    public DetallePlantillaHorario save(DetallePlantillaHorario detallePlantillaHorario) {
        return detallePlantillaHorarioRepository.save(detallePlantillaHorario);
    }

    public void deleteById(Long id) {
        detallePlantillaHorarioRepository.deleteById(id);
    }

    public List<DetallePlantillaHorario> findByPlantillaHorario(PlantillaHorario plantillaHorario) {
        return detallePlantillaHorarioRepository.findByPlantillaHorario(plantillaHorario);
    }




}