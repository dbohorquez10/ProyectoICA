/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// archivo: src/modelo/entidades/Plaga.java
package modelo.entidades;

/**
 * Entidad PLAGA del modelo de dominio.
 * Mapea la tabla ADMINICA.PLAGA.
 */
public class Plaga {

    private String idPlaga;
    private String nombreCientifico;
    private String nombreComun;
    private String tipoPlaga;

    public String getIdPlaga() {
        return idPlaga;
    }

    public void setIdPlaga(String idPlaga) {
        this.idPlaga = idPlaga;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getNombreComun() {
        return nombreComun;
    }

    public void setNombreComun(String nombreComun) {
        this.nombreComun = nombreComun;
    }

    public String getTipoPlaga() {
        return tipoPlaga;
    }

    public void setTipoPlaga(String tipoPlaga) {
        this.tipoPlaga = tipoPlaga;
    }

    @Override
    public String toString() {
        // Útil para combos
        return nombreComun != null ? nombreComun : (idPlaga != null ? idPlaga : super.toString());
    }
}
