/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * DAO para VISITA.
 */
package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.Visita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitaDAO {

    private static final String OWNER = "ADMINICA";
    private static final String TABLE = OWNER + ".VISITA";
    // OJO: ajusta el nombre de la secuencia al real en tu BD
    private static final String SEQ   = OWNER + ".SEQ_VISITA";

    // =========================================================
    //  INSERT
    // =========================================================

    public void insertarVisita(Visita v) throws SQLException {

        String sql = "INSERT INTO " + TABLE + " (" +
                "ID_VISITA, ID_PRODUCTOR, ID_LOTE, ID_TECNICO, " +
                "FECHA_SOLICITUD, FECHA_VISITA, MOTIVO, ESTADO, OBSERVACIONES) " +
                "VALUES ('VIS-' || " + SEQ + ".NEXTVAL, ?, ?, ?, SYSDATE, ?, ?, ?, ?)";

        // Para evitar problemas de permisos sobre la secuencia / triggers,
        // uso la conexión del dueño del esquema (ADMINICA)
        try (Connection cn = ConexionBD.getAppConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, v.getIdProductor());
            ps.setString(2, v.getIdLote());
            ps.setString(3, v.getIdTecnico());

            if (v.getFechaVisita() != null) {
                ps.setDate(4, Date.valueOf(v.getFechaVisita()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, v.getMotivo());
            ps.setString(6, v.getEstado());
            ps.setString(7, v.getObservaciones());

            ps.executeUpdate();
        }
    }

    // =========================================================
    //  UPDATE
    // =========================================================

    public void actualizarVisita(Visita v) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET " +
                "ID_TECNICO = ?, " +
                "FECHA_VISITA = ?, " +
                "MOTIVO = ?, " +
                "ESTADO = ?, " +
                "OBSERVACIONES = ? " +
                "WHERE ID_VISITA = ?";

        try (Connection cn = ConexionBD.getAppConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, v.getIdTecnico());

            if (v.getFechaVisita() != null) {
                ps.setDate(2, Date.valueOf(v.getFechaVisita()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            ps.setString(3, v.getMotivo());
            ps.setString(4, v.getEstado());
            ps.setString(5, v.getObservaciones());
            ps.setString(6, v.getIdVisita());

            ps.executeUpdate();
        }
    }

    // =========================================================
    //  DELETE
    // =========================================================

    public void eliminarVisita(String idVisita) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE ID_VISITA = ?";

        try (Connection cn = ConexionBD.getAppConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, idVisita);
            ps.executeUpdate();
        }
    }

    // =========================================================
    //  LISTADOS
    // =========================================================

    public List<Visita> listarTodas() throws SQLException {
        String sql = "SELECT ID_VISITA, ID_PRODUCTOR, ID_LOTE, ID_TECNICO, " +
                "FECHA_SOLICITUD, FECHA_VISITA, MOTIVO, ESTADO, OBSERVACIONES " +
                "FROM " + TABLE + " ORDER BY FECHA_SOLICITUD DESC";

        List<Visita> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.getAppConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Visita> listarPorProductor(String idProductor) throws SQLException {
        String sql = "SELECT ID_VISITA, ID_PRODUCTOR, ID_LOTE, ID_TECNICO, " +
                "FECHA_SOLICITUD, FECHA_VISITA, MOTIVO, ESTADO, OBSERVACIONES " +
                "FROM " + TABLE + " WHERE ID_PRODUCTOR = ? " +
                "ORDER BY FECHA_SOLICITUD DESC";

        List<Visita> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.getAppConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, idProductor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    public List<Visita> listarPorTecnico(String idTecnico) throws SQLException {
        String sql = "SELECT ID_VISITA, ID_PRODUCTOR, ID_LOTE, ID_TECNICO, " +
                "FECHA_SOLICITUD, FECHA_VISITA, MOTIVO, ESTADO, OBSERVACIONES " +
                "FROM " + TABLE + " WHERE ID_TECNICO = ? " +
                "ORDER BY FECHA_SOLICITUD DESC";

        List<Visita> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.getAppConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, idTecnico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    // =========================================================
    //  MAPEO
    // =========================================================

    private Visita mapRow(ResultSet rs) throws SQLException {
        Visita v = new Visita();
        v.setIdVisita(rs.getString("ID_VISITA"));
        v.setIdProductor(rs.getString("ID_PRODUCTOR"));
        v.setIdLote(rs.getString("ID_LOTE"));
        v.setIdTecnico(rs.getString("ID_TECNICO"));

        Date fs = rs.getDate("FECHA_SOLICITUD");
        if (fs != null) v.setFechaSolicitud(fs.toLocalDate());

        Date fv = rs.getDate("FECHA_VISITA");
        if (fv != null) v.setFechaVisita(fv.toLocalDate());

        v.setMotivo(rs.getString("MOTIVO"));
        v.setEstado(rs.getString("ESTADO"));
        v.setObservaciones(rs.getString("OBSERVACIONES"));
        return v;
    }
        /**
     * Marca como REALIZADA las visitas ASIGNADAS para el lote y técnico indicados
     * y desasigna al técnico (ID_TECNICO = NULL).
     * Se llama cuando el técnico registra una inspección.
     */
    public void marcarRealizadaPorInspeccion(String idLote, String idTecnico) throws SQLException {
    String sql =
            "UPDATE " + OWNER + ".VISITA " +
            "   SET ESTADO = 'REALIZADA', " +
            "       ID_TECNICO = NULL " +   // lo desasignamos para el trigger de 5 lotes
            " WHERE ID_LOTE = ? " +
            "   AND ID_TECNICO = ? " +
            "   AND ESTADO = 'ASIGNADA'";

    try (Connection cn = ConexionBD.getConexionActual();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, idLote);
        ps.setString(2, idTecnico);
        ps.executeUpdate();
    }
}


}


