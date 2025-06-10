package com.idea.entity;

import jakarta.persistence.*;

@Entity
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "plantilla_horario_id")
    private PlantillaHorario plantillaHorario;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public PlantillaHorario getPlantillaHorario() {
        return plantillaHorario;
    }

    public void setPlantillaHorario(PlantillaHorario plantillaHorario) {
        this.plantillaHorario = plantillaHorario;
    }


    public Empleado(Long id, String nombre, PlantillaHorario plantillaHorario) {
        this.id = id;
        this.nombre = nombre;
        this.plantillaHorario = plantillaHorario;
    }


    public Empleado() {
    }
}
