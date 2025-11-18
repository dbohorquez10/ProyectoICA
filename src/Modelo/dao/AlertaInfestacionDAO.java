/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.AlertaInfestacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad ALERTA_INFESTACION.
 * <p>
 * Permite registrar alertas manuales usando el procedimiento registrar_alerta
 * y consultar alertas generadas automática o manualmente.
 */
public class AlertaInfestacionDAO {

    /**
     * Registra una alerta manual para una inspección concreta usando
     * el procedimiento registrar_alerta.
     *
     * @param idInspeccion ID de la inspección
     * @param nivel        nivel crítico de infestación
     * @param mensaje      mensaje descriptivo
     * @throws SQLException si ocurre un error de BD
     */
    public void registrarAlertaManual(String idInspeccion, double nivel, String mensaje)
            throws SQLException {

        String sql = "{ call registrar_alerta(?, ?, ?) }";

        try (Connection con = ConexionBD.getConexionActual();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, idInspeccion);
            cs.setDouble(2, nivel);
            cs.setString(3, mensaje);

            cs.execute();
        }
    }

    /**
     * Lista las alertas asociadas a un lote (a través de sus inspecciones).
     *
     * @param idLote identificador del lote
     * @return lista de alertas
     * @throws SQLException si ocurre un error de BD
     */
    public List<AlertaInfestacion> listarPorLote(String idLote) throws SQLException {
        List<AlertaInfestacion> lista = new ArrayList<>();

        String sql = "SELECT a.ID_ALERTA, a.ID_INSPECCION, a.NIVEL_CRITICO, " +
                     "a.FECHA_ALERTA, a.ESTADO_ALERTA, a.MENSAJE_ALERTA " +
                     "FROM ALERTA_INFESTACION a " +
                     "JOIN INSPECCION i ON a.ID_INSPECCION = i.ID_INSPECCION " +
                     "WHERE i.ID_LOTE = ? " +
                     "ORDER BY a.FECHA_ALERTA DESC";

        try (Connection con = ConexionBD.getConexionActual();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idLote);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AlertaInfestacion a = new AlertaInfestacion();
                    a.setIdAlerta(rs.getString("ID_ALERTA"));
                    a.setIdInspeccion(rs.getString("ID_INSPECCION"));
                    a.setNivelCritico(rs.getDouble("NIVEL_CRITICO"));
                    a.setFechaAlerta(DAOUtils.toLocalDate(rs.getDate("FECHA_ALERTA")));
                    a.setEstadoAlerta(rs.getString("ESTADO_ALERTA"));
                    a.setMensajeAlerta(rs.getString("MENSAJE_ALERTA"));
                    lista.add(a);
                }
            }
        }
        return lista;
    }
}
