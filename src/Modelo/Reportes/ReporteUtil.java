package modelo.reportes;

import modelo.ConexionBD;
import net.sf.jasperreports.engine.*;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class ReporteUtil {

    // JRXML de inspección
    private static final String REP_INSPECCION       = "/Recursos/Inspeccionfitosanitaria.jrxml";

    // JRXML de cultivos–plagas
    private static final String REP_CULTIVOS         = "/Recursos/Cultivo_Plaga.jrxml";

    // JRXML productor + lotes
    private static final String REP_PRODUCTOR_LOTES  = "/Recursos/Productor_Lotes.jrxml";

    // JRXML usuarios (productores + técnicos)
    private static final String REP_USUARIOS         = "/Recursos/usuarios.jrxml";

    // JRXML plagas
    private static final String REP_PLAGAS           = "/Recursos/plagas.jrxml";

    // Ruta del logo (igual que venías usando)
    private static final String RUTA_LOGO_ICA =
            "C:\\Users\\dboho\\OneDrive\\Documentos\\NetBeansProjects\\ProyectoICA\\src\\Recursos\\logo_ica.png";

    // =========================================================
    //  INSPECCIÓN
    // =========================================================
    public static void exportarInspeccionPdf(String idInspeccion, String rutaDestino) throws Exception {

        try (Connection cn = ConexionBD.getConexionActual();
             InputStream is = ReporteUtil.class.getResourceAsStream(REP_INSPECCION)) {

            if (is == null) {
                throw new Exception("No se encontró el archivo del reporte: " + REP_INSPECCION);
            }

            JasperReport reporte = JasperCompileManager.compileReport(is);

            Map<String, Object> params = new HashMap<>();
            params.put("P_ID_INSPECCION", idInspeccion);
            params.put("LOGO_ICA", RUTA_LOGO_ICA);

            JasperPrint print = JasperFillManager.fillReport(reporte, params, cn);
            JasperExportManager.exportReportToPdfFile(print, rutaDestino);
        }
    }

    // =========================================================
    //  CULTIVOS–PLAGAS (reporte general)
    // =========================================================
    public static void exportarCultivosPdf(String rutaDestino) throws Exception {

        try (Connection cn = ConexionBD.getConexionActual();
             InputStream is = ReporteUtil.class.getResourceAsStream(REP_CULTIVOS)) {

            if (is == null) {
                throw new Exception("No se encontró el archivo del reporte: " + REP_CULTIVOS);
            }

            JasperReport reporte = JasperCompileManager.compileReport(is);

            Map<String, Object> params = new HashMap<>();
            params.put("LOGO_ICA", RUTA_LOGO_ICA);

            JasperPrint print = JasperFillManager.fillReport(reporte, params, cn);
            JasperExportManager.exportReportToPdfFile(print, rutaDestino);
        }
    }

    // =========================================================
    //  PRODUCTOR + LOTES
    // =========================================================
    public static void exportarProductorLotesPdf(String idProductor, String rutaDestino) throws Exception {

        try (Connection cn = ConexionBD.getConexionActual();
             InputStream is = ReporteUtil.class.getResourceAsStream(REP_PRODUCTOR_LOTES)) {

            if (is == null) {
                throw new Exception("No se encontró el archivo del reporte: " + REP_PRODUCTOR_LOTES);
            }

            JasperReport reporte = JasperCompileManager.compileReport(is);

            Map<String, Object> params = new HashMap<>();
            params.put("P_ID_PRODUCTOR", idProductor);
            params.put("LOGO_ICA", RUTA_LOGO_ICA);

            JasperPrint print = JasperFillManager.fillReport(reporte, params, cn);
            JasperExportManager.exportReportToPdfFile(print, rutaDestino);
        }
    }

    // =========================================================
    //  NUEVO: USUARIOS (productores + técnicos)
    // =========================================================
    public static void exportarUsuariosPdf(String rutaDestino) throws Exception {

        try (Connection cn = ConexionBD.getConexionActual();
             InputStream is = ReporteUtil.class.getResourceAsStream(REP_USUARIOS)) {

            if (is == null) {
                throw new Exception("No se encontró el archivo del reporte: " + REP_USUARIOS);
            }

            JasperReport reporte = JasperCompileManager.compileReport(is);

            Map<String, Object> params = new HashMap<>();
            params.put("LOGO_ICA", RUTA_LOGO_ICA);

            JasperPrint print = JasperFillManager.fillReport(reporte, params, cn);
            JasperExportManager.exportReportToPdfFile(print, rutaDestino);
        }
    }

    // =========================================================
    //  NUEVO: PLAGAS (catálogo)
    // =========================================================
    public static void exportarPlagasPdf(String rutaDestino) throws Exception {

        try (Connection cn = ConexionBD.getConexionActual();
             InputStream is = ReporteUtil.class.getResourceAsStream(REP_PLAGAS)) {

            if (is == null) {
                throw new Exception("No se encontró el archivo del reporte: " + REP_PLAGAS);
            }

            JasperReport reporte = JasperCompileManager.compileReport(is);

            Map<String, Object> params = new HashMap<>();
            params.put("LOGO_ICA", RUTA_LOGO_ICA);

            JasperPrint print = JasperFillManager.fillReport(reporte, params, cn);
            JasperExportManager.exportReportToPdfFile(print, rutaDestino);
        }
    }
}
