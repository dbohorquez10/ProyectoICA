/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// archivo: src/controlador/CultivoPlagaController.java
package controlador;

import modelo.dao.CultivoDAO;
import modelo.dao.PlagaDAO;
import modelo.dao.PlagaCultivoDAO;
import modelo.entidades.Cultivo;
import modelo.entidades.Plaga;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador para la relación muchos-a-muchos CULTIVO_PLAGA.
 */
public class CultivoPlagaController {

    private final CultivoDAO cultivoDAO;
    private final PlagaDAO plagaDAO;
    private final PlagaCultivoDAO cultivoPlagaDAO;

    public CultivoPlagaController() {
        this.cultivoDAO = new CultivoDAO();
        this.plagaDAO = new PlagaDAO();
        this.cultivoPlagaDAO = new PlagaCultivoDAO();
    }

    // ===== Listados base =====

    public List<Cultivo> listarCultivos() throws Exception {
        return cultivoDAO.listarCultivos();
    }

    public List<Plaga> listarPlagas() throws SQLException {
        return plagaDAO.listarTodas();
    }

    // ===== Relación vista desde PLAGA (ya la tenías) =====

    public Set<String> obtenerCultivosDePlaga(String idPlaga) throws SQLException {
        return new HashSet<>(cultivoPlagaDAO.listarCultivosDePlaga(idPlaga));
    }

    public void guardarRelaciones(String idPlaga, Set<String> seleccionados) throws Exception {
        if (idPlaga == null || idPlaga.isBlank()) {
            throw new Exception("No se ha seleccionado una plaga.");
        }

        Set<String> actuales =
                new HashSet<>(cultivoPlagaDAO.listarCultivosDePlaga(idPlaga));

        // Altas
        for (String idCultivo : seleccionados) {
            if (!actuales.contains(idCultivo)) {
                // alta
                cultivoPlagaDAO.guardarCultivosDePlaga(idPlaga, List.of(idCultivo));
            }
        }

        // Bajas (para simplificar usamos el método de reemplazo completo
        // en vez de baja individual)
        cultivoPlagaDAO.guardarCultivosDePlaga(idPlaga, List.copyOf(seleccionados));
    }

    // ===== NUEVO: relación vista desde CULTIVO =====

    /**
     * Devuelve la lista de plagas que afectan a un cultivo.
     * Lo usarán productor y técnico en la pantalla de cultivos.
     */
    public List<Plaga> listarPlagasDeCultivo(String idCultivo) throws SQLException {
        return cultivoPlagaDAO.listarPlagasDeCultivo(idCultivo);
    }
}

