/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.AsignacionLote;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad ASIGNACION_LOTE.
 * <p>
 * Utiliza el procedimiento registrar_asignacion para crear nuevas asignaciones.
 */
public class AsignacionLoteDAO {

    /**
     * Registra una nueva asignación de lote a técnico usando el procedimiento
     * registrar_asignacion. El trigger trg_limite_asignaciones se encargará
     * de impedir más de 5 lotes por técnico.
     *
     * @param idTecnico ID del técnico
     * @param idLote    ID del lote
     * @throws SQLException si ocurre un error de BD (incluye el error del trigger)
     */
    public void registrarAsignacion(String idTecnico, String idLote) throws SQLException {
        String sql = "{ call registrar_asignacion(?, ?) }";

        try (Connection con = ConexionBD.getConexionActual();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, idTecnico);
            cs.setString(2, idLote);

            cs.execute();
        }
    }

    /**
     * Lista las asignaciones de un técnico.
     *
     * @param idTecnico identificador del técnico
     * @return lista de asignaciones
     * @throws SQLException si ocurre un error de BD
     */
    public List<AsignacionLote> listarPorTecnico(String idTecnico) throws SQLException {
        List<AsignacionLote> lista = new ArrayList<>();

        String sql = "SELECT ID_ASIGNACION, ID_TECNICO, ID_LOTE, FECHA_ASIGNACION " +
                     "FROM ASIGNACION_LOTE WHERE ID_TECNICO = ?";

        try (Connection con = ConexionBD.getConexionActual();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idTecnico);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AsignacionLote a = new AsignacionLote();
                    a.setIdAsignacion(rs.getString("ID_ASIGNACION"));
                    a.setIdTecnico(rs.getString("ID_TECNICO"));
                    a.setIdLote(rs.getString("ID_LOTE"));
                    a.setFechaAsignacion(DAOUtils.toLocalDate(rs.getDate("FECHA_ASIGNACION")));
                    lista.add(a);
                }
            }
        }
        return lista;
    }
}
