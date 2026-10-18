
package com.gorriti.tp3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/gestion_turnos_hospital"
            + "?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires";

    private static final String USUARIO =
            System.getenv().getOrDefault(
                    "HOSPITAL_DB_USER", "root");

    private ConexionBD() {
    }

    public static Connection conectar() throws SQLException {

        String clave = System.getenv("HOSPITAL_DB_PASSWORD");

        if (clave == null) {
            throw new SQLException(
                    "Falta configurar la variable de entorno "
                    + "HOSPITAL_DB_PASSWORD.");
        }

        return DriverManager.getConnection(
                URL, USUARIO, clave);
    }
}
