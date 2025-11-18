/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// archivo: src/modelo/dao/CultivoPlagaDAO.java
package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.Plaga;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla de unión ADMINICA.CULTIVO_PLAGA (muchos a muchos).
 * Maneja únicamente relaciones entre cultivos y plagas.
 */
public class PlagaCultivoDAO {

    private static final String OWNER = "ADMINICA";

    /**
     * Devuelve la lista de IDs de cultivo relacionados con una plaga.
     */
    public List<String> listarCultivosDePlaga(String idPlaga) throws SQLException {
        List<String> ids = new ArrayList<>();

        String sql = "SELECT ID_CULTIVO " +
                     "  FROM " + OWNER + ".CULTIVO_PLAGA " +
                     " WHERE ID_PLAGA = ?";

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, idPlaga);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("ID_CULTIVO"));
                }
            }
        }
        return ids;
    }

    /**
     * Devuelve las PLAGAS asociadas a un cultivo concreto.
     * Se usa para que productor / técnico puedan ver qué plagas
     * afectan el cultivo seleccionado.
     */
    public List<Plaga> listarPlagasDeCultivo(String idCultivo) throws SQLException {
        List<Plaga> lista = new ArrayList<>();

        String sql =
                "SELECT p.ID_PLAGA, p.NOMBRE_CIENTIFICO, p.NOMBRE_COMUN, p.TIPO_PLAGA " +
                "  FROM " + OWNER + ".CULTIVO_PLAGA cp " +
                "  JOIN " + OWNER + ".PLAGA p ON p.ID_PLAGA = cp.ID_PLAGA " +
                " WHERE cp.ID_CULTIVO = ? " +
                " ORDER BY p.NOMBRE_COMUN";

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, idCultivo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Plaga p = new Plaga();
                    p.setIdPlaga(rs.getString("ID_PLAGA"));
                    p.setNombreCientifico(rs.getString("NOMBRE_CIENTIFICO"));
                    p.setNombreComun(rs.getString("NOMBRE_COMUN"));
                    p.setTipoPlaga(rs.getString("TIPO_PLAGA"));
                    lista.add(p);
                }
            }
        }

        return lista;
    }

    /**
     * Reemplaza completamente las relaciones de una plaga:
     *  - borra todas las filas anteriores de esa plaga
     *  - inserta las nuevas combinaciones (ID_CULTIVO, ID_PLAGA)
     * (usado en el diálogo de "Cultivos afectados..." para ADMIN).
     */
    public void guardarCultivosDePlaga(String idPlaga,
                                       List<String> idsCultivo) throws SQLException {

        String sqlDelete = "DELETE FROM " + OWNER + ".CULTIVO_PLAGA " +
                           " WHERE ID_PLAGA = ?";

        String sqlInsert = "INSERT INTO " + OWNER + ".CULTIVO_PLAGA " +
                           " (ID_CULTIVO, ID_PLAGA) VALUES (?, ?)";

        Connection cn = null;
        try {
            cn = ConexionBD.getConexionActual();
            cn.setAutoCommit(false);

            // 1) Borrar relaciones anteriores
            try (PreparedStatement psDel = cn.prepareStatement(sqlDelete)) {
                psDel.setString(1, idPlaga);
                psDel.executeUpdate();
            }

            // 2) Insertar las nuevas (si hay)
            if (idsCultivo != null && !idsCultivo.isEmpty()) {
                try (PreparedStatement psIns = cn.prepareStatement(sqlInsert)) {
                    for (String idCultivo : idsCultivo) {
                        psIns.setString(1, idCultivo);
                        psIns.setString(2, idPlaga);
                        psIns.addBatch();
                    }
                    psIns.executeBatch();
                }
            }

            cn.commit();
        } catch (SQLException ex) {
            if (cn != null) {
                try { cn.rollback(); } catch (Exception ignore) {}
            }
            throw ex;
        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (Exception ignore) {}
            }
        }
    }
}

