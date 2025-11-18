/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.entidades;

import java.time.LocalDate;

/**
 * Entidad que representa la tabla VISITA.
 *
 * VISITA:
 *   - ID_VISITA       VARCHAR2(20) PK (VIS-#)
 *   - ID_PRODUCTOR    VARCHAR2(20) FK → PRODUCTOR
 *   - ID_LOTE         VARCHAR2(20) FK → LOTE
 *   - ID_TECNICO      VARCHAR2(20) FK → TECNICO (puede ser null)
 *   - FECHA_SOLICITUD DATE
 *   - FECHA_VISITA    DATE
 *   - MOTIVO          VARCHAR2(200)
 *   - ESTADO          VARCHAR2(30)
 *   - OBSERVACIONES   VARCHAR2(500)
 */
public class Visita {

    private String idVisita;
    private String idProductor;
    private String idLote;
    private String idTecnico;
    private LocalDate fechaSolicitud;
    private LocalDate fechaVisita;
    private String motivo;
    private String estado;
    private String observaciones;

    public String getIdVisita() {
        return idVisita;
    }

    public void setIdVisita(String idVisita) {
        this.idVisita = idVisita;
    }

    public String getIdProductor() {
        return idProductor;
    }

    public void setIdProductor(String idProductor) {
        this.idProductor = idProductor;
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

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaVisita() {
        return fechaVisita;
    }

    public void setFechaVisita(LocalDate fechaVisita) {
        this.fechaVisita = fechaVisita;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
