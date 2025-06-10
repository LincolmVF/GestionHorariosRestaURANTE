package com.idea.entity;
import jakarta.persistence.*;
@Entity
public class AsignacionHorario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "horario_id")
    private Horario horario;

    private String diaDeLaSemana;
    private String periodo;

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
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

    public AsignacionHorario(Long id, Empleado empleado, Horario horario, String diaDeLaSemana, String periodo) {
        this.id = id;
        this.empleado = empleado;
        this.horario = horario;
        this.diaDeLaSemana = diaDeLaSemana;
        this.periodo = periodo;
    }

    public AsignacionHorario() {
    }
}
