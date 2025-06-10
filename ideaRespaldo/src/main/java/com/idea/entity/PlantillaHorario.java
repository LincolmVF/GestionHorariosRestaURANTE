package com.idea.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class PlantillaHorario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @OneToMany(mappedBy = "plantillaHorario", cascade = CascadeType.ALL)
    private List<DetallePlantillaHorario> detalles;


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

    public List<DetallePlantillaHorario> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePlantillaHorario> detalles) {
        this.detalles = detalles;
    }


    public PlantillaHorario(Long id, String nombre, List<DetallePlantillaHorario> detalles) {
        this.id = id;
        this.nombre = nombre;
        this.detalles = detalles;
    }


    public PlantillaHorario() {
    }
}
