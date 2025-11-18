/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.Productor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos para productores.
 *
 * Las tablas viven en ADMINICA, así que cuando estamos conectados
 * como ADMIN_ICA / PRODUCTOR, usamos ADMINICA.TABLA.
 */
public class ProductorDAO {

    private static final String OWNER = "ADMINICA";

    // =========================================================
    //  REGISTRO (procedimiento almacenado)
    // =========================================================

    public void registrarProductor(String username,
                                   String password,
                                   String nombre,
                                   String identificacion,
                                   String direccion,
                                   String departamento,
                                   String telefono,
                                   String correo) throws Exception {

        Connection cn = null;
        CallableStatement cs = null;

        try {
            // Dueño del esquema
            cn = ConexionBD.getAppConnection();

            cs = cn.prepareCall("{ call registrar_productor(?,?,?,?,?,?,?,?) }");
            cs.setString(1, username);
            cs.setString(2, password);
            cs.setString(3, nombre);
            cs.setString(4, identificacion);
            cs.setString(5, direccion);
            cs.setString(6, departamento);
            cs.setString(7, telefono);
            cs.setString(8, correo);

            cs.execute();

        } finally {
            if (cs != null) try { cs.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }
    }

    // =========================================================
    //  LISTAS
    // =========================================================

    /**
     * Lista TODOS los productores (activos e inactivos),
     * junto con el estado ACTIVO del usuario.
     */
    public List<Productor> listarProductores() throws Exception {
        List<Productor> resultado = new ArrayList<>();

        String sql = "SELECT p.ID_PRODUCTOR, " +
                     "       p.USUARIO_ID, " +
                     "       p.NOMBRE_COMPLETO, " +
                     "       p.IDENTIFICACION, " +
                     "       p.DEPARTAMENTO_RESIDENCIA, " +
                     "       p.DIRECCION, " +
                     "       p.TELEFONO, " +
                     "       p.CORREO_ELECTRONICO, " +
                     "       u.ACTIVO " +
                     "  FROM " + OWNER + ".PRODUCTOR p " +
                     "  JOIN " + OWNER + ".USUARIO u ON u.ID = p.USUARIO_ID " +
                     " ORDER BY p.NOMBRE_COMPLETO";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = ConexionBD.getConnection();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Productor p = new Productor();
                p.setIdProductor(rs.getString("ID_PRODUCTOR"));
                p.setUsuarioId(rs.getInt("USUARIO_ID"));
                p.setNombreCompleto(rs.getString("NOMBRE_COMPLETO"));
                p.setIdentificacion(rs.getString("IDENTIFICACION"));
                p.setDepartamentoResidencia(rs.getString("DEPARTAMENTO_RESIDENCIA"));
                p.setDireccion(rs.getString("DIRECCION"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setCorreoElectronico(rs.getString("CORREO_ELECTRONICO"));

                String activo = rs.getString("ACTIVO");
                p.setActivo("S".equalsIgnoreCase(activo));

                resultado.add(p);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }

        return resultado;
    }

    /**
     * Lista SOLO productores con usuario ACTIVO (para combos).
     */
    public List<Productor> listarProductoresActivos() throws Exception {
        List<Productor> resultado = new ArrayList<>();

        String sql = "SELECT p.ID_PRODUCTOR, " +
                     "       p.USUARIO_ID, " +
                     "       p.NOMBRE_COMPLETO, " +
                     "       p.IDENTIFICACION, " +
                     "       p.DEPARTAMENTO_RESIDENCIA, " +
                     "       p.DIRECCION, " +
                     "       p.TELEFONO, " +
                     "       p.CORREO_ELECTRONICO, " +
                     "       u.ACTIVO " +
                     "  FROM " + OWNER + ".PRODUCTOR p " +
                     "  JOIN " + OWNER + ".USUARIO u ON u.ID = p.USUARIO_ID " +
                     " WHERE u.ACTIVO = 'S' " +
                     " ORDER BY p.NOMBRE_COMPLETO";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = ConexionBD.getConnection();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Productor p = new Productor();
                p.setIdProductor(rs.getString("ID_PRODUCTOR"));
                p.setUsuarioId(rs.getInt("USUARIO_ID"));
                p.setNombreCompleto(rs.getString("NOMBRE_COMPLETO"));
                p.setIdentificacion(rs.getString("IDENTIFICACION"));
                p.setDepartamentoResidencia(rs.getString("DEPARTAMENTO_RESIDENCIA"));
                p.setDireccion(rs.getString("DIRECCION"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setCorreoElectronico(rs.getString("CORREO_ELECTRONICO"));
                p.setActivo(true);
                resultado.add(p);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }

        return resultado;
    }

    /**
     * Busca un productor por el ID del usuario (ADMINICA.USUARIO.ID).
     */
    public Productor buscarPorUsuarioId(int usuarioId) throws Exception {
        String sql = "SELECT p.ID_PRODUCTOR, " +
                     "       p.USUARIO_ID, " +
                     "       p.NOMBRE_COMPLETO, " +
                     "       p.IDENTIFICACION, " +
                     "       p.DEPARTAMENTO_RESIDENCIA, " +
                     "       p.DIRECCION, " +
                     "       p.TELEFONO, " +
                     "       p.CORREO_ELECTRONICO, " +
                     "       u.ACTIVO " +
                     "  FROM " + OWNER + ".PRODUCTOR p " +
                     "  JOIN " + OWNER + ".USUARIO u ON u.ID = p.USUARIO_ID " +
                     " WHERE p.USUARIO_ID = ?";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = ConexionBD.getAppConnection();

            ps = cn.prepareStatement(sql);
            ps.setInt(1, usuarioId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Productor p = new Productor();
                p.setIdProductor(rs.getString("ID_PRODUCTOR"));
                p.setUsuarioId(rs.getInt("USUARIO_ID"));
                p.setNombreCompleto(rs.getString("NOMBRE_COMPLETO"));
                p.setIdentificacion(rs.getString("IDENTIFICACION"));
                p.setDepartamentoResidencia(rs.getString("DEPARTAMENTO_RESIDENCIA"));
                p.setDireccion(rs.getString("DIRECCION"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setCorreoElectronico(rs.getString("CORREO_ELECTRONICO"));
                p.setActivo("S".equalsIgnoreCase(rs.getString("ACTIVO")));
                return p;
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }
        return null;
    }

    // =========================================================
    //  ACTIVAR / DESACTIVAR
    // =========================================================

    public void actualizarActivo(String idProductor, boolean activo) throws Exception {
        String sql = "UPDATE " + OWNER + ".USUARIO " +
                     "   SET ACTIVO = ? " +
                     " WHERE ID = (SELECT USUARIO_ID " +
                     "               FROM " + OWNER + ".PRODUCTOR " +
                     "              WHERE ID_PRODUCTOR = ?)";

        Connection cn = null;
        PreparedStatement ps = null;

        try {
            cn = ConexionBD.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setString(1, activo ? "S" : "N");
            ps.setString(2, idProductor);
            ps.executeUpdate();
        } finally {
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }
    }
}



