/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.Tecnico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos para técnicos ICA.
 *
 * Las tablas viven en ADMINICA, así que cuando estamos conectados
 * como ADMIN_ICA / TECNICO_ICA usamos ADMINICA.TABLA.
 */
public class TecnicoDAO {

    private static final String OWNER = "ADMINICA";

    // =========================================================
    //  REGISTRO (procedimiento almacenado)
    // =========================================================

    /**
     * Registra un técnico nuevo.
     *
     * Flujo:
     *  1) Se crea el registro en ADMINICA.USUARIO (ROL = 'TECNICO').
     *  2) Se llama a ADMINICA.REGISTRAR_TECNICO con el ID de ese usuario.
     *
     * El procedimiento REGISTRAR_TECNICO DEBE tener esta firma:
     *
     *   PROCEDURE registrar_tecnico(
     *       p_nombre_completo     IN  TECNICO.NOMBRE_COMPLETO%TYPE,
     *       p_identificacion      IN  TECNICO.IDENTIFICACION%TYPE,
     *       p_tarjeta_profesional IN  TECNICO.TARJETA_PROFESIONAL%TYPE,
     *       p_direccion           IN  TECNICO.DIRECCION%TYPE,
     *       p_departamento        IN  TECNICO.DEPARTAMENTO_RESIDENCIA%TYPE,
     *       p_telefono            IN  TECNICO.TELEFONO%TYPE,
     *       p_correo              IN  TECNICO.CORREO_ELECTRONICO%TYPE,
     *       p_usuario_id          IN  TECNICO.USUARIO_ID%TYPE,
     *       p_id_generado         OUT TECNICO.ID_TECNICO%TYPE
     *   );
     */
    public void registrarTecnico(String username,
                                 String password,
                                 String nombre,
                                 String identificacion,
                                 String tarjetaProfesional,
                                 String direccion,
                                 String departamento,
                                 String telefono,
                                 String correo) throws Exception {

        Connection cn = null;
        PreparedStatement ps = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            // Conexión como dueño del esquema (ADMINICA)
            cn = ConexionBD.getAppConnection();
            cn.setAutoCommit(false);

            // 1) Obtener siguiente ID de usuario (ADMINICA.SEQ_USUARIO)
            int usuarioId;
            String sqlSeq = "SELECT " + OWNER + ".SEQ_USUARIO.NEXTVAL AS ID FROM dual";
            ps = cn.prepareStatement(sqlSeq);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new Exception("No se pudo obtener el siguiente valor de SEQ_USUARIO.");
            }
            usuarioId = rs.getInt("ID");
            rs.close(); rs = null;
            ps.close(); ps = null;

            // 2) Insertar en ADMINICA.USUARIO
            String sqlUser =
                    "INSERT INTO " + OWNER + ".USUARIO " +
                    " (ID, USERNAME, PASSWORD_HASH, NOMBRE, ROL, ACTIVO) " +
                    "VALUES (?,?,?,?,?,?)";
            ps = cn.prepareStatement(sqlUser);
            ps.setInt(1, usuarioId);
            ps.setString(2, username);
            ps.setString(3, password); // si luego quieres hash, se cambia aquí
            ps.setString(4, nombre);
            ps.setString(5, "TECNICO");
            ps.setString(6, "N");
            ps.executeUpdate();
            ps.close(); ps = null;

            // 3) Llamar al procedimiento ADMINICA.REGISTRAR_TECNICO
            cs = cn.prepareCall("{ call " + OWNER + ".REGISTRAR_TECNICO(?,?,?,?,?,?,?,?,?) }");
            cs.setString(1, nombre);
            cs.setString(2, identificacion);
            cs.setString(3, tarjetaProfesional);
            cs.setString(4, direccion);
            cs.setString(5, departamento);
            cs.setString(6, telefono);
            cs.setString(7, correo);
            cs.setInt(8, usuarioId);
            cs.registerOutParameter(9, Types.VARCHAR); // p_id_generado
            cs.execute();

            // Si quieres ver el ID generado:
            // String idTecnico = cs.getString(9);

            cn.commit();

        } catch (Exception ex) {
            if (cn != null) {
                try { cn.rollback(); } catch (Exception ignored) {}
            }
            throw ex;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cs != null) try { cs.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }
    }

    // =========================================================
    //  LISTAS
    // =========================================================

    /**
     * Lista TODOS los técnicos (activos e inactivos),
     * junto con el estado ACTIVO del usuario.
     */
    public List<Tecnico> listarTecnicos() throws Exception {
        List<Tecnico> resultado = new ArrayList<>();

        String sql = "SELECT t.ID_TECNICO, " +
                     "       t.USUARIO_ID, " +
                     "       t.NOMBRE_COMPLETO, " +
                     "       t.IDENTIFICACION, " +
                     "       t.TARJETA_PROFESIONAL, " +
                     "       t.DEPARTAMENTO_RESIDENCIA, " +
                     "       t.DIRECCION, " +
                     "       t.TELEFONO, " +
                     "       t.CORREO_ELECTRONICO, " +
                     "       t.REGISTRO_ICA_ID, " +
                     "       u.ACTIVO " +
                     "  FROM " + OWNER + ".TECNICO t " +
                     "  JOIN " + OWNER + ".USUARIO u ON u.ID = t.USUARIO_ID " +
                     " ORDER BY t.NOMBRE_COMPLETO";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = ConexionBD.getConnection();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Tecnico t = new Tecnico();
                t.setIdTecnico(rs.getString("ID_TECNICO"));
                t.setUsuarioId(rs.getInt("USUARIO_ID"));
                t.setNombreCompleto(rs.getString("NOMBRE_COMPLETO"));
                t.setIdentificacion(rs.getString("IDENTIFICACION"));
                t.setTarjetaProfesional(rs.getString("TARJETA_PROFESIONAL"));
                t.setDepartamentoResidencia(rs.getString("DEPARTAMENTO_RESIDENCIA"));
                t.setDireccion(rs.getString("DIRECCION"));
                t.setTelefono(rs.getString("TELEFONO"));
                t.setCorreoElectronico(rs.getString("CORREO_ELECTRONICO"));
                t.setRegistroIcaId(rs.getString("REGISTRO_ICA_ID"));

                String activo = rs.getString("ACTIVO");
                t.setActivo("S".equalsIgnoreCase(activo));

                resultado.add(t);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }

        return resultado;
    }

    /**
     * Busca un técnico por el ID del usuario (ADMINICA.USUARIO.ID).
     * Lo usan pantallas como inspecciones y visitas.
     */
    // dentro de TecnicoDAO
public Tecnico buscarPorUsuarioId(int usuarioId) throws Exception {
    String sql = "SELECT t.ID_TECNICO, " +
                 "       t.USUARIO_ID, " +
                 "       t.NOMBRE_COMPLETO, " +
                 "       t.IDENTIFICACION, " +
                 "       t.TARJETA_PROFESIONAL, " +
                 "       t.DEPARTAMENTO_RESIDENCIA, " +
                 "       t.DIRECCION, " +
                 "       t.TELEFONO, " +
                 "       t.CORREO_ELECTRONICO, " +
                 "       t.REGISTRO_ICA_ID, " +
                 "       u.ACTIVO " +
                 "  FROM " + OWNER + ".TECNICO t " +
                 "  JOIN " + OWNER + ".USUARIO u ON u.ID = t.USUARIO_ID " +
                 " WHERE t.USUARIO_ID = ?";

    Connection cn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        // ANTES: getConnection()
        cn = ConexionBD.getAppConnection();

        ps = cn.prepareStatement(sql);
        ps.setInt(1, usuarioId);
        rs = ps.executeQuery();

        if (rs.next()) {
            Tecnico t = new Tecnico();
            t.setIdTecnico(rs.getString("ID_TECNICO"));
            t.setUsuarioId(rs.getInt("USUARIO_ID"));
            t.setNombreCompleto(rs.getString("NOMBRE_COMPLETO"));
            t.setIdentificacion(rs.getString("IDENTIFICACION"));
            t.setTarjetaProfesional(rs.getString("TARJETA_PROFESIONAL"));
            t.setDepartamentoResidencia(rs.getString("DEPARTAMENTO_RESIDENCIA"));
            t.setDireccion(rs.getString("DIRECCION"));
            t.setTelefono(rs.getString("TELEFONO"));
            t.setCorreoElectronico(rs.getString("CORREO_ELECTRONICO"));
            t.setRegistroIcaId(rs.getString("REGISTRO_ICA_ID"));
            t.setActivo("S".equalsIgnoreCase(rs.getString("ACTIVO")));
            return t;
        }
    } finally {
        if (rs != null) try { rs.close(); } catch (Exception ignored) {}
        if (ps != null) try { ps.close(); } catch (Exception ignored) {}
        if (cn != null) try { cn.close(); } catch (Exception ignored) {}
    }
    return null;
}

    // =========================================================
    //  ACTIVAR / DESACTIVAR / REGISTRO ICA
    // =========================================================

    /**
     * Cambia el estado ACTIVO en ADMINICA.USUARIO para un técnico.
     */
    public void actualizarActivo(String idTecnico, boolean activo) throws Exception {
        String sql = "UPDATE " + OWNER + ".USUARIO " +
                     "   SET ACTIVO = ? " +
                     " WHERE ID = (SELECT USUARIO_ID " +
                     "               FROM " + OWNER + ".TECNICO " +
                     "              WHERE ID_TECNICO = ?)";

        Connection cn = null;
        PreparedStatement ps = null;

        try {
            cn = ConexionBD.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setString(1, activo ? "S" : "N");
            ps.setString(2, idTecnico);
            ps.executeUpdate();
        } finally {
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * Actualiza el REGISTRO_ICA_ID del técnico (para la pantalla de aprobación).
     */
    public void actualizarRegistroIca(String idTecnico, String registroIca) throws Exception {
        String sql = "UPDATE " + OWNER + ".TECNICO " +
                     "   SET REGISTRO_ICA_ID = ? " +
                     " WHERE ID_TECNICO = ?";

        Connection cn = null;
        PreparedStatement ps = null;

        try {
            cn = ConexionBD.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setString(1, registroIca);
            ps.setString(2, idTecnico);
            ps.executeUpdate();
        } finally {
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }
    }
}
