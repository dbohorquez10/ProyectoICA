/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.dao.CultivoDAO;
import modelo.entidades.Cultivo;

import java.util.List;

/**
 * Controlador para la gestión de cultivos.
 */
public class CultivoController {

    private final CultivoDAO cultivoDAO;

    /**
     * Crea una instancia de CultivoController.
     */
    public CultivoController() {
        this.cultivoDAO = new CultivoDAO();
    }

    /**
     * Registra un nuevo cultivo usando el procedimiento registrar_cultivo.
     *
     * @param nombreCientifico nombre científico
     * @param nombreComun      nombre común
     * @param cicloCultivo     ciclo de cultivo
     * @throws Exception si los datos son inválidos o ocurre un error de BD
     */
    public void registrarCultivo(String nombreCientifico,
                                 String nombreComun,
                                 String cicloCultivo) throws Exception {

        if (nombreCientifico == null || nombreCientifico.isBlank()) {
            throw new Exception("El nombre científico es obligatorio.");
        }
        if (nombreComun == null || nombreComun.isBlank()) {
            throw new Exception("El nombre común es obligatorio.");
        }

        String ncient = nombreCientifico.trim();
        String ncomun = nombreComun.trim();
        String ciclo  = cicloCultivo != null ? cicloCultivo.trim() : null;

        // Llama al procedimiento almacenado a través del DAO
        cultivoDAO.registrarCultivo(ncient, ncomun, ciclo);
    }

    /**
     * Lista todos los cultivos registrados.
     *
     * @return lista de cultivos
     * @throws Exception si ocurre un error de BD
     */
    public List<Cultivo> listarCultivos() throws Exception {
        return cultivoDAO.listarCultivos();
    }

    /**
     * Busca un cultivo por ID.
     */
    public Cultivo buscarPorId(String idCultivo) throws Exception {
        if (idCultivo == null || idCultivo.isBlank()) {
            throw new Exception("Debe indicar el ID del cultivo.");
        }
        return cultivoDAO.buscarPorId(idCultivo.trim());
    }

    /**
     * Actualiza un cultivo existente.
     *
     * @param cultivo cultivo con ID y datos actualizados
     * @throws Exception si hay datos inválidos o error de BD
     */
    public void actualizarCultivo(Cultivo cultivo) throws Exception {
        if (cultivo == null ||
            cultivo.getIdCultivo() == null ||
            cultivo.getIdCultivo().isBlank()) {
            throw new Exception("Falta el ID del cultivo a actualizar.");
        }
        cultivoDAO.actualizarCultivo(cultivo);
    }

    /**
     * Elimina un cultivo por ID.
     *
     * @param idCultivo identificador del cultivo
     * @throws Exception si ocurre un error de BD
     */
    public void eliminarCultivo(String idCultivo) throws Exception {
        if (idCultivo == null || idCultivo.isBlank()) {
            throw new Exception("Debe seleccionar un cultivo a eliminar.");
        }
        cultivoDAO.eliminarCultivo(idCultivo.trim());
    }
}


