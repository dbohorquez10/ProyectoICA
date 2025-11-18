package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.Inspeccion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InspeccionDAO {

    private static final String OWNER = "ADMINICA";

    // =============== INSERT ==================
    public void insertarInspeccion(Inspeccion i) throws Exception {

        String sql =
                "INSERT INTO " + OWNER + ".INSPECCION (" +
                "    ID_INSPECCION, " +
                "    ID_LOTE, " +
                "    ID_TECNICO, " +
                "    FECHA_INSPECCION, " +
                "    TOTAL_PLANTAS, " +
                "    PLANTAS_AFECTADAS, " +
                "    ESTADO_FENOLOGICO, " +
                "    PORCENTAJE_INFESTACION, " +
                "    OBSERVACIONES" +
                ") VALUES (" +
                "    'INS-' || " + OWNER + ".SEQ_INSPECCION.NEXTVAL, " +
                "    ?, ?, SYSDATE, ?, ?, ?, ?, ?" +
                ")";

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, i.getIdLote());
            ps.setString(2, i.getIdTecnico());
            ps.setInt(3, i.getTotalPlantas());
            ps.setInt(4, i.getPlantasAfectadas());
            ps.setString(5, i.getEstadoFenologico());
            ps.setDouble(6, i.getPorcentajeInfestacion());
            ps.setString(7, i.getObservaciones());

            ps.executeUpdate();
        }
    }

    // =============== UPDATE ==================
    public void actualizarInspeccion(Inspeccion i) throws Exception {

        String sql =
                "UPDATE " + OWNER + ".INSPECCION SET " +
                "    ID_LOTE               = ?, " +
                "    ID_TECNICO            = ?, " +
                "    FECHA_INSPECCION      = ?, " +
                "    TOTAL_PLANTAS         = ?, " +
                "    PLANTAS_AFECTADAS     = ?, " +
                "    ESTADO_FENOLOGICO     = ?, " +
                "    PORCENTAJE_INFESTACION= ?, " +
                "    OBSERVACIONES         = ? " +
                "WHERE ID_INSPECCION = ?";

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, i.getIdLote());
            ps.setString(2, i.getIdTecnico());
            ps.setDate(3, Date.valueOf(i.getFechaInspeccion()));
            ps.setInt(4, i.getTotalPlantas());
            ps.setInt(5, i.getPlantasAfectadas());
            ps.setString(6, i.getEstadoFenologico());
            ps.setDouble(7, i.getPorcentajeInfestacion());
            ps.setString(8, i.getObservaciones());
            ps.setString(9, i.getIdInspeccion());

            ps.executeUpdate();
        }
    }

    // =============== LISTAS ==================
    public List<Inspeccion> listarTodas() throws Exception {
        String sql =
                "SELECT ID_INSPECCION, ID_LOTE, ID_TECNICO, FECHA_INSPECCION, " +
                "       TOTAL_PLANTAS, PLANTAS_AFECTADAS, ESTADO_FENOLOGICO, " +
                "       PORCENTAJE_INFESTACION, OBSERVACIONES " +
                "FROM " + OWNER + ".INSPECCION " +
                "ORDER BY FECHA_INSPECCION DESC";

        List<Inspeccion> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Inspeccion> listarPorTecnico(String idTecnico) throws Exception {
        String sql =
                "SELECT ID_INSPECCION, ID_LOTE, ID_TECNICO, FECHA_INSPECCION, " +
                "       TOTAL_PLANTAS, PLANTAS_AFECTADAS, ESTADO_FENOLOGICO, " +
                "       PORCENTAJE_INFESTACION, OBSERVACIONES " +
                "FROM " + OWNER + ".INSPECCION " +
                "WHERE ID_TECNICO = ? " +
                "ORDER BY FECHA_INSPECCION DESC";

        List<Inspeccion> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.getConexionActual();
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

    public List<Inspeccion> listarPorProductor(String idProductor) throws Exception {
        String sql =
                "SELECT i.ID_INSPECCION, i.ID_LOTE, i.ID_TECNICO, i.FECHA_INSPECCION, " +
                "       i.TOTAL_PLANTAS, i.PLANTAS_AFECTADAS, i.ESTADO_FENOLOGICO, " +
                "       i.PORCENTAJE_INFESTACION, i.OBSERVACIONES " +
                "FROM " + OWNER + ".INSPECCION i " +
                "JOIN " + OWNER + ".LOTE l ON l.ID_LOTE = i.ID_LOTE " +
                "WHERE l.ID_PRODUCTOR = ? " +
                "ORDER BY i.FECHA_INSPECCION DESC";

        List<Inspeccion> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.getConexionActual();
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

    // =============== MAPEO ==================
    private Inspeccion mapRow(ResultSet rs) throws Exception {
        Inspeccion i = new Inspeccion();
        i.setIdInspeccion(rs.getString("ID_INSPECCION"));
        i.setIdLote(rs.getString("ID_LOTE"));
        i.setIdTecnico(rs.getString("ID_TECNICO"));

        Date f = rs.getDate("FECHA_INSPECCION");
        if (f != null) i.setFechaInspeccion(f.toLocalDate());

        i.setTotalPlantas(rs.getInt("TOTAL_PLANTAS"));
        i.setPlantasAfectadas(rs.getInt("PLANTAS_AFECTADAS"));
        i.setEstadoFenologico(rs.getString("ESTADO_FENOLOGICO"));
        i.setPorcentajeInfestacion(rs.getDouble("PORCENTAJE_INFESTACION"));
        i.setObservaciones(rs.getString("OBSERVACIONES"));

        return i;
    }
}
