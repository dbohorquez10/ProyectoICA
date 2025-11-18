/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.entidades;

/**
 * Entidad que representa la tabla REPORTE_PLAGA.
 * <p>
 * REPORTE_PLAGA:
 *   - ID_REPORTE            VARCHAR2(20) PK (REP-#)
 *   - ID_CULTIVO            VARCHAR2(20) FK → CULTIVO
 *   - ID_PLAGA              VARCHAR2(20) FK → PLAGA
 *   - PERIODO_REPORTE       VARCHAR2(20)
 *   - NIVEL_INFESTACION     NUMBER(5,2)
 *   - PORCENTAJE_AFECTACION NUMBER(5,2)
 *   - ALERTAS_ACTIVAS       VARCHAR2(3)
 */
public class ReportePlaga {

    /** Identificador del reporte de plaga (REP-#). */
    private String idReporte;

    /** Cultivo al que corresponde el reporte. */
    private String idCultivo;

    /** Plaga reportada. */
    private String idPlaga;

    /** Periodo del reporte (por ejemplo "2025-01", "2025Q1"). */
    private String periodoReporte;

    /** Nivel de infestación (valor numérico). */
    private double nivelInfestacion;

    /** Porcentaje de área afectada. */
    private double porcentajeAfectacion;

    /** Indicador de si hay alertas activas ("SI", "NO", etc.). */
    private String alertasActivas;

    // GETTERS / SETTERS -------------------------------------------------------

    public String getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(String idReporte) {
        this.idReporte = idReporte;
    }

    public String getIdCultivo() {
        return idCultivo;
    }

    public void setIdCultivo(String idCultivo) {
        this.idCultivo = idCultivo;
    }

    public String getIdPlaga() {
        return idPlaga;
    }

    public void setIdPlaga(String idPlaga) {
        this.idPlaga = idPlaga;
    }

    public String getPeriodoReporte() {
        return periodoReporte;
    }

    public void setPeriodoReporte(String periodoReporte) {
        this.periodoReporte = periodoReporte;
    }

    public double getNivelInfestacion() {
        return nivelInfestacion;
    }

    public void setNivelInfestacion(double nivelInfestacion) {
        this.nivelInfestacion = nivelInfestacion;
    }

    public double getPorcentajeAfectacion() {
        return porcentajeAfectacion;
    }

    public void setPorcentajeAfectacion(double porcentajeAfectacion) {
        this.porcentajeAfectacion = porcentajeAfectacion;
    }

    public String getAlertasActivas() {
        return alertasActivas;
    }

    public void setAlertasActivas(String alertasActivas) {
        this.alertasActivas = alertasActivas;
    }
}
