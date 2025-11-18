/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.entidades;

import java.time.LocalDate;

/**
 * Entidad que representa la tabla ALERTA_INFESTACION.
 * <p>
 * ALERTA_INFESTACION:
 *   - ID_ALERTA      VARCHAR2(20) PK (ALR-#)
 *   - ID_INSPECCION  VARCHAR2(20) FK → INSPECCION
 *   - NIVEL_CRITICO  NUMBER(5,2)
 *   - FECHA_ALERTA   DATE
 *   - ESTADO_ALERTA  VARCHAR2(20)
 *   - MENSAJE_ALERTA VARCHAR2(250)
 */
public class AlertaInfestacion {

    /** Identificador de la alerta (ALR-#). */
    private String idAlerta;

    /** Inspección que generó la alerta. */
    private String idInspeccion;

    /** Nivel crítico de infestación. */
    private double nivelCritico;

    /** Fecha en que se generó o registró la alerta. */
    private LocalDate fechaAlerta;

    /** Estado actual de la alerta (ACTIVA, CERRADA, etc.). */
    private String estadoAlerta;

    /** Mensaje descriptivo de la alerta. */
    private String mensajeAlerta;

    // GETTERS / SETTERS -------------------------------------------------------

    public String getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(String idAlerta) {
        this.idAlerta = idAlerta;
    }

    public String getIdInspeccion() {
        return idInspeccion;
    }

    public void setIdInspeccion(String idInspeccion) {
        this.idInspeccion = idInspeccion;
    }

    public double getNivelCritico() {
        return nivelCritico;
    }

    public void setNivelCritico(double nivelCritico) {
        this.nivelCritico = nivelCritico;
    }

    public LocalDate getFechaAlerta() {
        return fechaAlerta;
    }

    public void setFechaAlerta(LocalDate fechaAlerta) {
        this.fechaAlerta = fechaAlerta;
    }

    public String getEstadoAlerta() {
        return estadoAlerta;
    }

    public void setEstadoAlerta(String estadoAlerta) {
        this.estadoAlerta = estadoAlerta;
    }

    public String getMensajeAlerta() {
        return mensajeAlerta;
    }

    public void setMensajeAlerta(String mensajeAlerta) {
        this.mensajeAlerta = mensajeAlerta;
    }
}
