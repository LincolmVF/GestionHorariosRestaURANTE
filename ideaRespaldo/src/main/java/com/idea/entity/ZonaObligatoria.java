package com.idea.entity;
import jakarta.persistence.*;
@Entity
public class ZonaObligatoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "zona_id", nullable = false)
    private Zona zona;

    private String diaDeLaSemana; // Ejemplo: "Lunes", "Martes"

    public ZonaObligatoria(Long id, Zona zona, String diaDeLaSemana) {
        this.id = id;
        this.zona = zona;
        this.diaDeLaSemana = diaDeLaSemana;
    }
    public ZonaObligatoria() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
