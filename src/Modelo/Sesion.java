/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import modelo.entidades.Usuario;

/**
 * "Sesión" actual de la aplicación.
 *
 * Almacena el usuario autenticado y su rol para que cualquier
 * capa (controladores, vistas, DAO) pueda consultarlo.
 */
public final class Sesion {

    private static Usuario usuarioActual;
    private static RolUsuario rolActual;

    private Sesion() {
        // clase de utilería, no instanciable
    }

    // ===== API principal =====

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
        if (usuario != null) {
            rolActual = usuario.getRol();
        } else {
            rolActual = null;
        }
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        rolActual = null;
    }

    public static boolean haySesion() {
        return usuarioActual != null;
    }

    // ===== Getters / setters simples (compatibilidad) =====

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static RolUsuario getRolActual() {
        return rolActual;
    }

    public static void setRolActual(RolUsuario rol) {
        rolActual = rol;
    }
}
