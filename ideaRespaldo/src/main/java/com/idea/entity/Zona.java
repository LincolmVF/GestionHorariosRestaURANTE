package com.idea.entity;

import jakarta.persistence.*;

@Entity
public class Zona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ejemplo: "Zona A", "Zona B"



    @ManyToOne
    @JoinColumn(name = "grupo_zona_id")
    private GrupoZona grupoZona;

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


    public GrupoZona getGrupoZona() {
        return grupoZona;
    }

    public void setGrupoZona(GrupoZona grupoZona) {
        this.grupoZona = grupoZona;
    }

    public Zona(Long id, String nombre, GrupoZona grupoZona) {
        this.id = id;
        this.nombre = nombre;
        this.grupoZona = grupoZona;
    }

    public Zona() {
    }
}
