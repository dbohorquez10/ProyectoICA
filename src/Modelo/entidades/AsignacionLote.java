/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.entidades;

import java.time.LocalDate;

/**
 * Entidad que representa la tabla ASIGNACION_LOTE.
 * <p>
 * ASIGNACION_LOTE:
 *   - ID_ASIGNACION    VARCHAR2(20) PK (ASG-#)
 *   - ID_TECNICO       VARCHAR2(20) FK → TECNICO
 *   - ID_LOTE          VARCHAR2(20) FK → LOTE
 *   - FECHA_ASIGNACION DATE
 */
public class AsignacionLote {

    /** Identificador de la asignación (ASG-#). */
    private String idAsignacion;

    /** Técnico asignado al lote. */
    private String idTecnico;

    /** Lote asignado al técnico. */
    private String idLote;

    /** Fecha en que se realizó la asignación. */
    private LocalDate fechaAsignacion;

    // GETTERS / SETTERS -------------------------------------------------------

    public String getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(String idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public String getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(String idTecnico) {
        this.idTecnico = idTecnico;
    }

    public String getIdLote() {
        return idLote;
    }

    public void setIdLote(String idLote) {
        this.idLote = idLote;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}
