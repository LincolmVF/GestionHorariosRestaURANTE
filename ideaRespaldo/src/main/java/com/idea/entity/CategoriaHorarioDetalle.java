package com.idea.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria_horario_detalle")
public class CategoriaHorarioDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "categoria_horario_id", nullable = false)
    private CategoriaHorario categoriaHorario;

    @ManyToOne
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;

    @ManyToOne
    @JoinColumn(name = "grupo_zona_id")
    private GrupoZona grupoZona; // 🔹 Nuevo campo para asociar el grupo de zonas

    // Constructor vacío
    public CategoriaHorarioDetalle() {}

    // Constructor con parámetros
    public CategoriaHorarioDetalle(CategoriaHorario categoriaHorario, Horario horario, GrupoZona grupoZona) {
        this.categoriaHorario = categoriaHorario;
        this.horario = horario;
        this.grupoZona = grupoZona;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoriaHorario getCategoriaHorario() {
        return categoriaHorario;
    }

    public void setCategoriaHorario(CategoriaHorario categoriaHorario) {
        this.categoriaHorario = categoriaHorario;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public GrupoZona getGrupoZona() {
        return grupoZona;
    }

    public void setGrupoZona(GrupoZona grupoZona) {
        this.grupoZona = grupoZona;
    }
}
