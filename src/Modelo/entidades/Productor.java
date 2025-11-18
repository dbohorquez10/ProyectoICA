/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.entidades;

/**
 * Entidad PRODUCTOR del modelo de dominio.
 *
 * Mapea la tabla PRODUCTOR y, además, trae el estado ACTIVO
 * desde la tabla USUARIO asociada.
 */
public class Productor {

    private String idProductor;
    private int usuarioId;

    private String nombreCompleto;
    private String identificacion;
    private String departamentoResidencia;
    private String direccion;
    private String telefono;
    private String correoElectronico;

    /**
     * Estado de la cuenta del usuario asociado.
     * true  -> ACTIVO (USUARIO.ACTIVO = 'S')
     * false -> INACTIVO (USUARIO.ACTIVO = 'N')
     */
    private boolean activo;

    // ==================== Getters / Setters ====================

    public String getIdProductor() {
        return idProductor;
    }

    public void setIdProductor(String idProductor) {
        this.idProductor = idProductor;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getDepartamentoResidencia() {
        return departamentoResidencia;
    }

    public void setDepartamentoResidencia(String departamentoResidencia) {
        this.departamentoResidencia = departamentoResidencia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
