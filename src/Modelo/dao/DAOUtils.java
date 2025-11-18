/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Métodos de utilidad comunes para los DAOs.
 */
public final class DAOUtils {

    private DAOUtils() {
        // Evitar instanciación
    }

    /**
     * Convierte un LocalDate de Java a java.sql.Date para usar en JDBC.
     *
     * @param fecha fecha en formato LocalDate (puede ser null)
     * @return instancia de java.sql.Date o null si fecha es null
     */
    public static Date toSqlDate(LocalDate fecha) {
        return (fecha == null) ? null : Date.valueOf(fecha);
    }

    /**
     * Convierte un java.sql.Date de JDBC a LocalDate.
     *
     * @param fechaSql fecha en formato java.sql.Date (puede ser null)
     * @return instancia de LocalDate o null si fechaSql es null
     */
    public static LocalDate toLocalDate(Date fechaSql) {
        return (fechaSql == null) ? null : fechaSql.toLocalDate();
    }
}
