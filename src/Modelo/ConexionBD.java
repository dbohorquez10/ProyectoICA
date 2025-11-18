/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static modelo.RolUsuario.ADMIN;
import static modelo.RolUsuario.PRODUCTOR;
import static modelo.RolUsuario.TECNICO;

/**
 * Gestor centralizado de conexiones JDBC a Oracle.
 * <p>
 * - Usa el usuario dueño del esquema (ADMINICA) para operaciones
 *   generales: login, creación de usuarios, etc.
 * - Usa diferentes usuarios físicos (ADMIN_ICA, TECNICO_ICA, PRODUCTOR)
 *   según el rol del usuario autenticado.
 *
 * El script de BD que tienes crea los roles y usuarios:
 *   ROL_ADMIN     → usuario ADMIN_ICA / admin_ica123
 *   ROL_TECNICO   → usuario TECNICO_ICA / tecnico123
 *   ROL_PRODUCTOR → usuario PRODUCTOR / productor123
 */
public class ConexionBD {

    // =========================================================================
    //  PARÁMETROS DE CONEXIÓN
    // =========================================================================

    /** URL local por defecto (ajusta el SID / servicio según tu BD). */
    private static final String URL_LOCAL  = "jdbc:oracle:thin:@localhost:1521:XE";

    /** URL remota opcional (si trabajas contra un servidor diferente). */
    private static final String URL_REMOTA = "jdbc:oracle:thin:@192.168.254.215:1521:orcl";

    /** URL realmente utilizada. Cambia a URL_REMOTA si lo necesitas. */
    private static final String URL = URL_LOCAL;

    /** Dueño del esquema con todas las tablas, secuencias, triggers, etc. */
    private static final String USER_APP_OWNER = "ADMINICA";
    private static final String PASS_APP_OWNER = "adminica123";

    /** Usuario físico asociado al rol ADMIN. */
    private static final String USER_ADMIN_ICA = "ADMIN_ICA";
    private static final String PASS_ADMIN_ICA = "admin_ica123";

    /** Usuario físico asociado al rol TECNICO. */
    private static final String USER_TECNICO_ICA = "TECNICO_ICA";
    private static final String PASS_TECNICO_ICA = "tecnico123";

    /** Usuario físico asociado al rol PRODUCTOR. */
    private static final String USER_PRODUCTOR = "PRODUCTOR";
    private static final String PASS_PRODUCTOR = "productor123";

    // =========================================================================
    //  CARGA DEL DRIVER JDBC
    // =========================================================================

    static {
        try {
            // Driver JDBC de Oracle (ojdbc8.jar u otro) debe estar en el classpath.
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver JDBC de Oracle", e);
        }
    }

    // =========================================================================
    //  MÉTODOS PÚBLICOS DE CONEXIÓN (TUS ORIGINALES)
    // =========================================================================

    /**
     * Obtiene una conexión usando el usuario dueño del esquema (ADMINICA).
     * <p>
     * Útil para:
     * - ejecución de procedimientos que se definieron en ese esquema,
     * - login y registro de nuevos usuarios,
     * - tareas de administración global.
     *
     * @return conexión JDBC lista para usar
     * @throws SQLException si falla la conexión
     */
    public static Connection getConexionAppOwner() throws SQLException {
        return DriverManager.getConnection(URL, USER_APP_OWNER, PASS_APP_OWNER);
    }

    /**
     * Obtiene una conexión usando el usuario físico asociado a un rol lógico.
     *
     * @param rol rol lógico de la aplicación (ADMIN, TECNICO o PRODUCTOR)
     * @return conexión JDBC autenticada como el usuario de ese rol
     * @throws SQLException si falla la conexión
     */
    public static Connection getConexionPorRol(RolUsuario rol) throws SQLException {
        if (rol == null) {
            // Si no se indica un rol, se usa por defecto el dueño del esquema.
            return getConexionAppOwner();
        }

        switch (rol) {
            case ADMIN:
                return DriverManager.getConnection(URL, USER_ADMIN_ICA, PASS_ADMIN_ICA);
            case TECNICO:
                return DriverManager.getConnection(URL, USER_TECNICO_ICA, PASS_TECNICO_ICA);
            case PRODUCTOR:
                return DriverManager.getConnection(URL, USER_PRODUCTOR, PASS_PRODUCTOR);
            default:
                // Por seguridad, cualquier caso raro vuelve al dueño del esquema.
                return getConexionAppOwner();
        }
    }

    /**
     * Devuelve una conexión dependiendo del usuario actualmente autenticado.
     * <p>
     * - Si hay sesión: se conecta como el usuario físico de su rol
     *   (ADMIN_ICA, TECNICO_ICA, PRODUCTOR).
     * - Si no hay sesión: se conecta como ADMINICA.
     *
     * @return conexión JDBC de acuerdo al contexto de sesión
     * @throws SQLException si falla la conexión
     */
    public static Connection getConexionActual() throws SQLException {
        if (!Sesion.haySesion()) {
            return getConexionAppOwner();
        }
        RolUsuario rol = Sesion.getRolActual();
        return getConexionPorRol(rol);
    }

    // =========================================================================
    //  ALIAS PARA NO ROMPER DAOs QUE USAN OTROS NOMBRES
    // =========================================================================

    /**
     * Alias de compatibilidad.
     * Equivalente a getConexionAppOwner().
     */
    public static Connection getAppConnection() throws SQLException {
        return getConexionAppOwner();
    }

    /**
     * Alias de compatibilidad.
     * Equivalente a getConexionActual().
     */
    public static Connection getConnection() throws SQLException {
        return getConexionActual();
    }
}

