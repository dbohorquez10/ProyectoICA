/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.RolUsuario;
import modelo.Sesion;
import modelo.dao.ProductorDAO;
import modelo.dao.TecnicoDAO;
import modelo.dao.VisitaDAO;
import modelo.entidades.Productor;
import modelo.entidades.Tecnico;
import modelo.entidades.Visita;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para visitas técnicas.
 *
 * Flujo:
 *  - Productor: solicita visita → estado SOLICITADA
 *  - Admin: asigna técnico / cambia estado
 *  - Técnico: marca REALIZADA y agrega observaciones
 */
public class VisitaController {

    private final VisitaDAO visitaDAO = new VisitaDAO();

    public void registrarVisita(String idProductor,
                                String idLote,
                                String motivo,
                                String idTecnico,
                                LocalDate fechaVisita,
                                String observaciones) throws Exception {

        if (idProductor == null || idProductor.isBlank()) {
            throw new Exception("Debe indicar el productor.");
        }
        if (idLote == null || idLote.isBlank()) {
            throw new Exception("Debe indicar el lote.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new Exception("Debe indicar el motivo de la visita.");
        }

        Visita v = new Visita();
        v.setIdProductor(idProductor.trim());
        v.setIdLote(idLote.trim());
        v.setIdTecnico(idTecnico != null && !idTecnico.isBlank()
                ? idTecnico.trim()
                : null);
        v.setFechaVisita(fechaVisita);
        v.setMotivo(motivo.trim());
        // Productor solo SOLICITA
        v.setEstado("SOLICITADA");
        v.setObservaciones(observaciones != null ? observaciones.trim() : null);

        visitaDAO.insertarVisita(v);
    }

    public void actualizarVisita(Visita v) throws Exception {
        if (v.getIdVisita() == null || v.getIdVisita().isBlank()) {
            throw new Exception("Falta el ID de la visita.");
        }
        visitaDAO.actualizarVisita(v);
    }

    public void eliminarVisita(String idVisita) throws Exception {
        if (idVisita == null || idVisita.isBlank()) {
            throw new Exception("Debe seleccionar una visita.");
        }
        visitaDAO.eliminarVisita(idVisita.trim());
    }

    public List<Visita> listarVisitasParaUsuarioActual() throws Exception {
        RolUsuario rol = Sesion.getRolActual();
        int usuarioId = Sesion.getUsuarioActual().getId();

        if (rol == RolUsuario.ADMIN) {
            return visitaDAO.listarTodas();
        } else if (rol == RolUsuario.TECNICO) {
            TecnicoDAO tecDAO = new TecnicoDAO();
            Tecnico t = tecDAO.buscarPorUsuarioId(usuarioId);
            if (t == null) {
                throw new Exception("No se encontró el técnico asociado al usuario actual.");
            }
            return visitaDAO.listarPorTecnico(t.getIdTecnico());
        } else {
            ProductorDAO prodDAO = new ProductorDAO();
            Productor p = prodDAO.buscarPorUsuarioId(usuarioId);
            if (p == null) {
                throw new Exception("No se encontró el productor asociado al usuario actual.");
            }
            return visitaDAO.listarPorProductor(p.getIdProductor());
        }
    }
}

