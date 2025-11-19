package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.Visita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para VISITA.
 */
public class VisitaDAO {

    private static final String OWNER      = "ADMINICA";
    private static final String TABLE      = OWNER + ".VISITA";
    private static final String SEQ        = OWNER + ".SEQ_VISITA";
    private static final String ASIG_TABLE = OWNER + ".ASIGNACION_LOTE";

    // =========================================================
    //  INSERT
    // =========================================================

    /**
     * Insertar visita usando una conexión externa (para transacciones).
     */
    public void insertarVisita(Visita v, Connection cn) throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (" +
                "ID_VISITA, ID_PRODUCTOR, ID_LOTE, ID_TECNICO, " +
                "FECHA_SOLICITUD, FECHA_VISITA, MOTIVO, ESTADO, OBSERVACIONES) " +
                "VALUES ('VIS-' || " + SEQ + ".NEXTVAL, ?, ?, ?, SYSDATE, ?, ?, ?, ?)";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
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

    /**
     * Insertar visita abriendo/cerrando su propia conexión (uso genérico).
     */
    public void insertarVisita(Visita v) throws SQLException {
        try (Connection cn = ConexionBD.getAppConnection()) {
            insertarVisita(v, cn);
        }
    }

    // =========================================================
    //  UPDATE
    // =========================================================

    /** Update usando conexión externa (para transacciones). */
    public void actualizarVisita(Visita v, Connection cn) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET " +
                "ID_TECNICO = ?, " +
                "FECHA_VISITA = ?, " +
                "MOTIVO = ?, " +
                "ESTADO = ?, " +
                "OBSERVACIONES = ? " +
                "WHERE ID_VISITA = ?";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
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

    /** Update “normal” abriendo su propia conexión. */
    public void actualizarVisita(Visita v) throws SQLException {
        try (Connection cn = ConexionBD.getAppConnection()) {
            actualizarVisita(v, cn);
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

    // =========================================================
    //  MARCAR REALIZADA POR INSPECCIÓN
    // =========================================================

    /**
     * Usa la MISMA conexión que viene del controlador
     * (misma transacción que la inspección).
     */
    public void marcarRealizadaPorInspeccion(String idLote,
                                             String idTecnico,
                                             Connection cn) throws SQLException {

        String sqlVisita =
                "UPDATE " + TABLE + " " +
                "   SET ESTADO = 'REALIZADA', " +
                "       ID_TECNICO = NULL " +
                " WHERE ID_LOTE = ? " +
                "   AND ID_TECNICO = ? " +
                "   AND ESTADO = 'ASIGNADA'";

        String sqlAsig =
                "DELETE FROM " + ASIG_TABLE + " " +
                " WHERE ID_LOTE = ? " +
                "   AND ID_TECNICO = ?";

        try (PreparedStatement ps1 = cn.prepareStatement(sqlVisita);
             PreparedStatement ps2 = cn.prepareStatement(sqlAsig)) {

            ps1.setString(1, idLote);
            ps1.setString(2, idTecnico);
            ps1.executeUpdate();

            ps2.setString(1, idLote);
            ps2.setString(2, idTecnico);
            ps2.executeUpdate();
        }
    }

    /**
     * Versión simple que abre su propia conexión (por si la necesitas en otro lado).
     */
    public void marcarRealizadaPorInspeccion(String idLote, String idTecnico) throws SQLException {
        try (Connection cn = ConexionBD.getConexionActual()) {
            marcarRealizadaPorInspeccion(idLote, idTecnico, cn);
        }
    }
}
