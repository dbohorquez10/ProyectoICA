/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.Cultivo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos para CULTIVO.
 * Las tablas viven en ADMINICA.
 */
public class CultivoDAO {

    private static final String OWNER = "ADMINICA";

    // =========================================================
    //  REGISTRO (usa el procedimiento registrar_cultivo)
    // =========================================================

    public void registrarCultivo(String nombreCientifico,
                                 String nombreComun,
                                 String cicloCultivo) throws Exception {

        Connection cn = null;
        CallableStatement cs = null;

        try {
            // Dueño del esquema
            cn = ConexionBD.getAppConnection();

            cs = cn.prepareCall("{ call registrar_cultivo(?,?,?) }");
            cs.setString(1, nombreCientifico);
            cs.setString(2, nombreComun);
            cs.setString(3, cicloCultivo);

            cs.execute();

        } finally {
            if (cs != null) try { cs.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }
    }

    // =========================================================
    //  LISTAR CULTIVOS (método que usa LoteForm)
    // =========================================================

    /**
     * Devuelve TODOS los cultivos de ADMINICA.CULTIVO,
     * ordenados por nombre común.
     *
     * Lo usa LoteForm para llenar el combo de cultivos.
     */
    public List<Cultivo> listarCultivos() throws Exception {
        List<Cultivo> lista = new ArrayList<>();

        String sql = "SELECT ID_CULTIVO, NOMBRE_CIENTIFICO, " +
                     "       NOMBRE_COMUN, CICLO_CULTIVO " +
                     "  FROM " + OWNER + ".CULTIVO " +
                     " ORDER BY NOMBRE_COMUN";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = ConexionBD.getConnection();   // conexión según el rol
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cultivo c = new Cultivo();
                c.setIdCultivo(rs.getString("ID_CULTIVO"));
                c.setNombreCientifico(rs.getString("NOMBRE_CIENTIFICO"));
                c.setNombreComun(rs.getString("NOMBRE_COMUN"));
                c.setCicloCultivo(rs.getString("CICLO_CULTIVO"));
                lista.add(c);
            }

        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }

        return lista;
    }

    // =========================================================
    //  (Opcionales) buscar / actualizar / eliminar
    //  — déjalos si te sirven en otros formularios
    // =========================================================

    public Cultivo buscarPorId(String idCultivo) throws Exception {
        String sql = "SELECT ID_CULTIVO, NOMBRE_CIENTIFICO, " +
                     "       NOMBRE_COMUN, CICLO_CULTIVO " +
                     "  FROM " + OWNER + ".CULTIVO " +
                     " WHERE ID_CULTIVO = ?";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = ConexionBD.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setString(1, idCultivo);
            rs = ps.executeQuery();

            if (rs.next()) {
                Cultivo c = new Cultivo();
                c.setIdCultivo(rs.getString("ID_CULTIVO"));
                c.setNombreCientifico(rs.getString("NOMBRE_CIENTIFICO"));
                c.setNombreComun(rs.getString("NOMBRE_COMUN"));
                c.setCicloCultivo(rs.getString("CICLO_CULTIVO"));
                return c;
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }

        return null;
    }

    public void actualizarCultivo(Cultivo c) throws Exception {
        String sql = "UPDATE " + OWNER + ".CULTIVO " +
                     "   SET NOMBRE_CIENTIFICO = ?, " +
                     "       NOMBRE_COMUN      = ?, " +
                     "       CICLO_CULTIVO     = ? " +
                     " WHERE ID_CULTIVO       = ?";

        Connection cn = null;
        PreparedStatement ps = null;

        try {
            cn = ConexionBD.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setString(1, c.getNombreCientifico());
            ps.setString(2, c.getNombreComun());
            ps.setString(3, c.getCicloCultivo());
            ps.setString(4, c.getIdCultivo());
            ps.executeUpdate();

        } finally {
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }
    }

    public void eliminarCultivo(String idCultivo) throws Exception {
        String sql = "DELETE FROM " + OWNER + ".CULTIVO " +
                     " WHERE ID_CULTIVO = ?";

        Connection cn = null;
        PreparedStatement ps = null;

        try {
            cn = ConexionBD.getConnection();
            ps = cn.prepareStatement(sql);
            ps.setString(1, idCultivo);
            ps.executeUpdate();

        } finally {
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (cn != null) try { cn.close(); } catch (Exception ignored) {}
        }
    }
}
