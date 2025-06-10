package com.idea.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria_horario")
public class CategoriaHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    // Constructor vacío
    public CategoriaHorario() {}

    // Constructor con parámetros
    public CategoriaHorario(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters
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
}
