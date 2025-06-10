package com.idea.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class GrupoZona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ejemplo: "Zona Norte", "Zona Sur"

    @OneToMany(mappedBy = "grupoZona", cascade = CascadeType.ALL)
    private List<Zona> zonas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    public void setZonas(List<Zona> zonas) {
        this.zonas = zonas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public GrupoZona(Long id, String nombre, List<Zona> zonas) {
        this.id = id;
        this.nombre = nombre;
        this.zonas = zonas;
    }

    public GrupoZona() {
    }
}
