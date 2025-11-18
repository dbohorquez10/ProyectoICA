/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.entidades;

import java.time.LocalDate;

/**
 * Entidad que representa la tabla INSPECCION.
 * <p>
 * INSPECCION:
 *   - ID_INSPECCION          VARCHAR2(20) PK (INS-#)
 *   - ID_LOTE                VARCHAR2(20) FK → LOTE
 *   - ID_TECNICO             VARCHAR2(20) FK → TECNICO
 *   - FECHA_INSPECCION       DATE
 *   - TOTAL_PLANTAS          NUMBER(6)
 *   - PLANTAS_AFECTADAS      NUMBER(6)
 *   - ESTADO_FENOLOGICO      VARCHAR2(100)
 *   - PORCENTAJE_INFESTACION NUMBER(5,2)
 *   - OBSERVACIONES          VARCHAR2(500)
 */
public class Inspeccion {

    /** Identificador de la inspección (INS-#). */
    private String idInspeccion;

    /** Lote inspeccionado. */
    private String idLote;

    /** Técnico que realizó la inspección. */
    private String idTecnico;

    /** Fecha en que se realizó la inspección. */
    private LocalDate fechaInspeccion;

    /** Número total de plantas evaluadas. */
    private int totalPlantas;

    /** Número de plantas afectadas. */
    private int plantasAfectadas;

    /** Estado fenológico del cultivo inspeccionado. */
    private String estadoFenologico;

    /** Porcentaje de infestación calculado. */
private double porcentajeInfestacion;  // siempre habrá un valor


    /** Observaciones generales de la inspección. */
    private String observaciones;

    // GETTERS / SETTERS -------------------------------------------------------

    public String getIdInspeccion() {
        return idInspeccion;
    }

    public void setIdInspeccion(String idInspeccion) {
        this.idInspeccion = idInspeccion;
    }

    public String getIdLote() {
        return idLote;
    }

    public void setIdLote(String idLote) {
        this.idLote = idLote;
    }

    public String getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(String idTecnico) {
        this.idTecnico = idTecnico;
    }

    public LocalDate getFechaInspeccion() {
        return fechaInspeccion;
    }

    public void setFechaInspeccion(LocalDate fechaInspeccion) {
        this.fechaInspeccion = fechaInspeccion;
    }

    public int getTotalPlantas() {
        return totalPlantas;
    }

    public void setTotalPlantas(int totalPlantas) {
        this.totalPlantas = totalPlantas;
    }

    public int getPlantasAfectadas() {
        return plantasAfectadas;
    }

    public void setPlantasAfectadas(int plantasAfectadas) {
        this.plantasAfectadas = plantasAfectadas;
    }

    public String getEstadoFenologico() {
        return estadoFenologico;
    }

    public void setEstadoFenologico(String estadoFenologico) {
        this.estadoFenologico = estadoFenologico;
    }

    public double getPorcentajeInfestacion() {
    return porcentajeInfestacion;
}

public void setPorcentajeInfestacion(double porcentajeInfestacion) {
    this.porcentajeInfestacion = porcentajeInfestacion;
}


    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
