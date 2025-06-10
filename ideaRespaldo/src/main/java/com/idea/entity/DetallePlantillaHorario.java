package com.idea.entity;

import jakarta.persistence.*;

@Entity
public class DetallePlantillaHorario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plantilla_horario_id")
    private PlantillaHorario plantillaHorario;

    @ManyToOne
    @JoinColumn(name = "horario_id")
    private Horario horario;

    private String diaDeLaSemana;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlantillaHorario getPlantillaHorario() {
        return plantillaHorario;
    }

    public void setPlantillaHorario(PlantillaHorario plantillaHorario) {
        this.plantillaHorario = plantillaHorario;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public String getDiaDeLaSemana() {
        return diaDeLaSemana;
    }

    public void setDiaDeLaSemana(String diaDeLaSemana) {
        this.diaDeLaSemana = diaDeLaSemana;
    }

    public DetallePlantillaHorario(Long id, PlantillaHorario plantillaHorario, Horario horario, String diaDeLaSemana) {
        this.id = id;
        this.plantillaHorario = plantillaHorario;
        this.horario = horario;
        this.diaDeLaSemana = diaDeLaSemana;
    }


    public DetallePlantillaHorario() {
    }
}
