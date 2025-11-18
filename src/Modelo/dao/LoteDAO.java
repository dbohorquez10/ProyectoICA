/*
 * DAO para la tabla LOTE.
 */
package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.Lote;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoteDAO {

    private static final String OWNER = "ADMINICA";

    // =========================================================
    //  CONSULTAS
    // =========================================================

    /** Lista todos los lotes (para el rol ADMIN). */
    public List<Lote> listarTodos() throws Exception {

        String sql =
                "SELECT " +
                "    ID_LOTE, " +
                "    ID_CULTIVO, " +
                "    ID_PRODUCTOR, " +
                "    AREA_HECTAREAS, " +
                "    NUMERO_LOTE, " +
                "    FECHA_SIEMBRA, " +
                "    FECHA_ELIMINACION " +
                "FROM " + OWNER + ".LOTE " +
                "ORDER BY ID_LOTE";

        List<Lote> lista = new ArrayList<Lote>();

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Lote l = mapear(rs);
                lista.add(l);
            }
        }
        return lista;
    }

    /** Lista los lotes de un productor concreto (para rol PRODUCTOR). */
    public List<Lote> listarPorProductor(String idProductor) throws Exception {

        String sql =
                "SELECT " +
                "    ID_LOTE, " +
                "    ID_CULTIVO, " +
                "    ID_PRODUCTOR, " +
                "    AREA_HECTAREAS, " +
                "    NUMERO_LOTE, " +
                "    FECHA_SIEMBRA, " +
                "    FECHA_ELIMINACION " +
                "FROM " + OWNER + ".LOTE " +
                "WHERE ID_PRODUCTOR = ? " +
                "ORDER BY ID_LOTE";

        List<Lote> lista = new ArrayList<Lote>();

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, idProductor);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Lote l = mapear(rs);
                    lista.add(l);
                }
            }
        }
        return lista;
    }

    // =========================================================
    //  INSERT / UPDATE / DELETE
    // =========================================================

    /**
     * Inserta un lote NUEVO.
     * Genera el ID_LOTE con la secuencia ADMINICA.LOTE_SEQ.
     */
    public void insertar(Lote l) throws Exception {
        if (l.getFechaSiembra() == null) {
            throw new Exception("La fecha de siembra es obligatoria.");
        }

        String sql =
                "INSERT INTO " + OWNER + ".LOTE (" +
                "    ID_LOTE, " +
                "    ID_CULTIVO, " +
                "    ID_PRODUCTOR, " +
                "    AREA_HECTAREAS, " +
                "    NUMERO_LOTE, " +
                "    FECHA_SIEMBRA, " +
                "    FECHA_ELIMINACION" +
                ") VALUES (" +
                "    'LOT-' || " + OWNER + ".LOTE_SEQ.NEXTVAL, " +
                "    ?, ?, ?, ?, ?, NULL" +
                ")";

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            // ID_CULTIVO (VARCHAR2)
            ps.setString(1, l.getIdCultivo());

            // ID_PRODUCTOR (VARCHAR2)
            ps.setString(2, l.getIdProductor());

            // AREA_HECTAREAS (NUMBER)
            ps.setDouble(3, l.getAreaHectareas());

            // NUMERO_LOTE (VARCHAR2 o NUMBER, según tu columna)
            ps.setString(4, l.getNumeroLote());

            // FECHA_SIEMBRA (DATE)
            ps.setDate(5, Date.valueOf(l.getFechaSiembra()));

            ps.executeUpdate();
        }
    }

    /** Actualiza un lote existente. */
    public void actualizar(Lote l) throws Exception {

        String sql =
                "UPDATE " + OWNER + ".LOTE " +
                "SET " +
                "    ID_CULTIVO        = ?, " +
                "    ID_PRODUCTOR      = ?, " +
                "    AREA_HECTAREAS    = ?, " +
                "    NUMERO_LOTE       = ?, " +
                "    FECHA_SIEMBRA     = ?, " +
                "    FECHA_ELIMINACION = ? " +
                "WHERE ID_LOTE = ?";

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, l.getIdCultivo());
            ps.setString(2, l.getIdProductor());
            ps.setDouble(3, l.getAreaHectareas());
            ps.setString(4, l.getNumeroLote());
            ps.setDate(5, Date.valueOf(l.getFechaSiembra()));

            if (l.getFechaEliminacion() != null) {
                ps.setDate(6, Date.valueOf(l.getFechaEliminacion()));
            } else {
                ps.setNull(6, Types.DATE);
            }

            ps.setString(7, l.getIdLote());

            ps.executeUpdate();
        }
    }

    /** Elimina un lote por ID. */
    public void eliminar(String idLote) throws Exception {
        String sql = "DELETE FROM " + OWNER + ".LOTE WHERE ID_LOTE = ?";

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, idLote);
            ps.executeUpdate();
        }
    }

    // =========================================================
    //  MAPEO ResultSet -> Entidad
    // =========================================================

    private Lote mapear(ResultSet rs) throws Exception {
        Lote l = new Lote();
        l.setIdLote(rs.getString("ID_LOTE"));
        l.setIdCultivo(rs.getString("ID_CULTIVO"));
        l.setIdProductor(rs.getString("ID_PRODUCTOR"));
        l.setAreaHectareas(rs.getDouble("AREA_HECTAREAS"));
        l.setNumeroLote(rs.getString("NUMERO_LOTE"));

        Date fSiembra = rs.getDate("FECHA_SIEMBRA");
        Date fElim    = rs.getDate("FECHA_ELIMINACION");

        if (fSiembra != null) {
            l.setFechaSiembra(fSiembra.toLocalDate());
        }
        if (fElim != null) {
            l.setFechaEliminacion(fElim.toLocalDate());
        }
        return l;
    }
    public List<Lote> listarLotesAsignadosATecnico(String idTecnico) throws Exception {

        String sql =
                "SELECT DISTINCT " +
                "    l.ID_LOTE, " +
                "    l.ID_CULTIVO, " +
                "    l.ID_PRODUCTOR, " +
                "    l.AREA_HECTAREAS, " +
                "    l.NUMERO_LOTE, " +
                "    l.FECHA_SIEMBRA, " +
                "    l.FECHA_ELIMINACION " +
                "FROM " + OWNER + ".LOTE l " +
                "JOIN " + OWNER + ".VISITA v ON v.ID_LOTE = l.ID_LOTE " +
                "WHERE v.ID_TECNICO = ? " +
                "  AND v.ESTADO = 'ASIGNADA' " +
                "ORDER BY l.ID_LOTE";

        List<Lote> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.getConexionActual();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, idTecnico);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }
}
