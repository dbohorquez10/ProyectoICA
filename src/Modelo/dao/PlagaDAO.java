/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// archivo: src/modelo/dao/PlagaDAO.java
package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.Plaga;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Plaga.
 *
 * Todas las tablas viven en ADMINICA, pero accedemos según el rol
 * usando ConexionBD.getConexionActual() para SELECT
 * y ConexionBD.getConexionAppOwner() cuando necesitamos el dueño.
 */
public class PlagaDAO {

    private static final String OWNER = "ADMINICA";

    /**
     * Inserta una plaga usando el procedimiento ADMINICA.registrar_plaga.
     */
    public void registrarPlaga(Plaga plaga) throws SQLException {
        String sql = "{ call " + OWNER + ".registrar_plaga(?,?,?) }";

        try (Connection cn = ConexionBD.getConexionAppOwner();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setString(1, plaga.getNombreCientifico());
            cs.setString(2, plaga.getNombreComun());
            cs.setString(3, plaga.getTipoPlaga());
            cs.execute();
        }
    }

    /**
     * Lista todas las plagas.
     */
    public List<Plaga> listarTodas() throws SQLException {
        String sql = "SELECT ID_PLAGA, NOMBRE_CIENTIFICO, NOMBRE_COMUN, TIPO_PLAGA " +
                     "  FROM " + OWNER + ".PLAGA " +
                     " ORDER BY NOMBRE_COMUN";

        List<Plaga> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Plaga p = new Plaga();
                p.setIdPlaga(rs.getString("ID_PLAGA"));
                p.setNombreCientifico(rs.getString("NOMBRE_CIENTIFICO"));
                p.setNombreComun(rs.getString("NOMBRE_COMUN"));
                p.setTipoPlaga(rs.getString("TIPO_PLAGA"));
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Actualiza una plaga existente.
     */
    public void actualizarPlaga(Plaga plaga) throws SQLException {
        String sql = "UPDATE " + OWNER + ".PLAGA " +
                     "   SET NOMBRE_CIENTIFICO = ?, " +
                     "       NOMBRE_COMUN      = ?, " +
                     "       TIPO_PLAGA        = ? " +
                     " WHERE ID_PLAGA          = ?";

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, plaga.getNombreCientifico());
            ps.setString(2, plaga.getNombreComun());
            ps.setString(3, plaga.getTipoPlaga());
            ps.setString(4, plaga.getIdPlaga());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina una plaga por ID.
     */
    public void eliminarPlaga(String idPlaga) throws SQLException {
        String sql = "DELETE FROM " + OWNER + ".PLAGA WHERE ID_PLAGA = ?";

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, idPlaga);
            ps.executeUpdate();
        }
    }
}
