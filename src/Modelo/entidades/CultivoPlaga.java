/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.entidades;

/**
 * Entidad que representa la tabla intermedia CULTIVO_PLAGA.
 * <p>
 * CULTIVO_PLAGA:
 *   - ID_CULTIVO VARCHAR2(20) FK → CULTIVO
 *   - ID_PLAGA   VARCHAR2(20) FK → PLAGA
 *   PRIMARY KEY (ID_CULTIVO, ID_PLAGA)
 */
public class CultivoPlaga {

    /** Identificador del cultivo. */
    private String idCultivo;

    /** Identificador de la plaga. */
    private String idPlaga;

    // GETTERS / SETTERS -------------------------------------------------------

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
}

