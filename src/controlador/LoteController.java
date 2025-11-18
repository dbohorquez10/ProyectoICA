/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.RolUsuario;
import modelo.Sesion;
import modelo.dao.LoteDAO;
import modelo.dao.ProductorDAO;
import modelo.entidades.Lote;
import modelo.entidades.Productor;

import java.time.LocalDate;
import java.util.List;

public class LoteController {

    private final LoteDAO loteDAO = new LoteDAO();
    private final ProductorDAO productorDAO = new ProductorDAO();

    /** Lista según el rol:
     *  - ADMIN: todos los lotes
     *  - PRODUCTOR: solo los lotes de ese productor
     */
    public List<Lote> listarLotesParaUsuarioActual() throws Exception {
        RolUsuario rol = Sesion.getRolActual();

        if (rol == RolUsuario.PRODUCTOR) {
            int usuarioId = Sesion.getUsuarioActual().getId();
            Productor p = productorDAO.buscarPorUsuarioId(usuarioId);
            if (p == null) {
                throw new Exception("No se encontró productor para el usuario actual.");
            }
            return loteDAO.listarPorProductor(p.getIdProductor());
        } else {
            // ADMIN y TECNICO (si quieres que también vea todos)
            return loteDAO.listarTodos();
        }
    }

    // Registrar un nuevo lote
    // dentro de LoteController
public void registrarLote(String idCultivo,
                          String idProductor,
                          double area,
                          String numeroLote,
                          LocalDate fechaSiembra) throws Exception {

    Lote l = new Lote();
    l.setIdCultivo(idCultivo);
    l.setIdProductor(idProductor);
    l.setAreaHectareas(area);
    l.setNumeroLote(numeroLote);
    l.setFechaSiembra(fechaSiembra);

    loteDAO.insertar(l);   // <-- usa el INSERT directo del DAO
}


    public void actualizarLote(Lote l) throws Exception {
        loteDAO.actualizar(l);
    }

    public void eliminarLote(String idLote) throws Exception {
        loteDAO.eliminar(idLote);
    }

    // Genera ID tipo "LOT-1", "LOT-2"... (puedes cambiar por secuencia si quieres)
    private String generarIdLote() throws Exception {
        // Versión sencilla: usar fecha/tiempo o una consulta.
        // Para el documento lógico realmente usamos seq_lote,
        // pero en Java puedes seguir con 'LOT-' + System.currentTimeMillis().
        return "LOT-" + System.currentTimeMillis();
    }
}

