package com.idea.entity;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class AsignacionZona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;

    private String diaDeLaSemana; // Ejemplo: "Lunes", "Martes"


    @ManyToOne
    @JoinColumn(name = "periodo_id", nullable = false)
    private Periodo periodo;

    @Column(name = "inicio", nullable = false)
    private LocalTime inicio;

    @Column(name = "fin", nullable = false)
    private LocalTime fin;

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

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public String getDiaDeLaSemana() {
        return diaDeLaSemana;
    }

    public void setDiaDeLaSemana(String diaDeLaSemana) {
        this.diaDeLaSemana = diaDeLaSemana;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalTime inicio) {
        this.inicio = inicio;
    }

    public LocalTime getFin() {
        return fin;
    }

    public void setFin(LocalTime fin) {
        this.fin = fin;
    }

    public AsignacionZona(Long id, Empleado empleado, Zona zona, String diaDeLaSemana, Periodo periodo, LocalTime inicio, LocalTime fin) {
        this.id = id;
        this.empleado = empleado;
        this.zona = zona;
        this.diaDeLaSemana = diaDeLaSemana;
        this.periodo = periodo;
        this.inicio = inicio;
        this.fin = fin;
    }

    public AsignacionZona() {
    }
}
