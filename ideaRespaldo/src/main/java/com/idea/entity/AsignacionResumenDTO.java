package com.idea.entity;

public class AsignacionResumenDTO {
    private String periodo;
    private String diaDeLaSemana;
    private Long cantidad;

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getDiaDeLaSemana() {
        return diaDeLaSemana;
    }

    public void setDiaDeLaSemana(String diaDeLaSemana) {
        this.diaDeLaSemana = diaDeLaSemana;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public AsignacionResumenDTO(String periodo, String diaDeLaSemana, Long cantidad) {
        this.periodo = periodo;
        this.diaDeLaSemana = diaDeLaSemana;
        this.cantidad = cantidad;
    }
    public AsignacionResumenDTO() {}
}
