/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Roles de usuario soportados por la aplicación.
 *
 * Coinciden con el campo ROL de la tabla USUARIO:
 *  - ADMIN
 *  - TECNICO
 *  - PRODUCTOR
 */
public enum RolUsuario {
    ADMIN,
    TECNICO,
    PRODUCTOR;

    /**
     * Convierte una cadena de la BD en un valor del enum.
     * Es tolerante con mayúsculas/minúsculas y algunos alias.
     */
    public static RolUsuario fromDb(String s) {
        if (s == null) return null;
        s = s.trim().toUpperCase();

        if ("ADMIN".equals(s) || "ADMIN_ICA".equals(s)) {
            return ADMIN;
        } else if ("TECNICO".equals(s) || "TECNICO_ICA".equals(s) || "ASISTENTE_TECNICO".equals(s)) {
            return TECNICO;
        } else if ("PRODUCTOR".equals(s)) {
            return PRODUCTOR;
        }
        return null;
    }

    /**
     * Alias para compatibilidad con fromString(..) que ya usabas.
     */
    public static RolUsuario fromString(String s) {
        return fromDb(s);
    }

    /**
     * Devuelve el valor que se debe guardar en la columna ROL de la tabla USUARIO.
     */
    public String toDbValue() {
        switch (this) {
            case ADMIN:
                return "ADMIN";
            case TECNICO:
                return "TECNICO";
            case PRODUCTOR:
                return "PRODUCTOR";
            default:
                return null;
        }
    }
}
