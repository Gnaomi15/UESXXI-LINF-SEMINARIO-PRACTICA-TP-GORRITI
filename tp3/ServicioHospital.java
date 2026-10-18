
package com.gorriti.tp3;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioHospital {

    public boolean comprobarConexion() throws SQLException {
        try (Connection c = ConexionBD.conectar()) {
            return c.isValid(5);
        }
    }

    // ============================================
    // PACIENTES
    // ============================================

    public List<Paciente> listarPacientes() throws SQLException {

        List<Paciente> resultado = new ArrayList<>();

        String sql = """
                SELECT id_paciente, documento, nombre, apellido,
                       telefono, email
                FROM paciente
                ORDER BY apellido, nombre
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(leerPaciente(rs));
            }
        }

        return resultado;
    }

    private Paciente leerPaciente(ResultSet rs)
            throws SQLException {

        return new Paciente(
                rs.getInt("id_paciente"),
                rs.getString("documento"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email")
        );
    }

    public Paciente buscarPaciente(String documento)
            throws SQLException {

        String sql = """
                SELECT id_paciente, documento, nombre, apellido,
                       telefono, email
                FROM paciente
                WHERE documento = ?
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, documento);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerPaciente(rs) : null;
            }
        }
    }

    public void registrarPaciente(
            String documento,
            String nombre,
            String apellido,
            String telefono,
            String email) throws SQLException {

        new Paciente(
                0, documento, nombre, apellido, telefono, email
        );

        String sql = """
                INSERT INTO paciente
                (documento, nombre, apellido, telefono, email)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, documento.trim());
            ps.setString(2, nombre.trim());
            ps.setString(3, apellido.trim());
            ps.setString(4, telefono);
            ps.setString(5, email);

            ps.executeUpdate();
        }
    }

    // ============================================
    // ESPECIALIDADES - NUEVO
    // ============================================

    public List<String> listarEspecialidades()
            throws SQLException {

        List<String> resultado = new ArrayList<>();

        String sql = """
                SELECT nombre
                FROM especialidad
                ORDER BY nombre
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(rs.getString("nombre"));
            }
        }

        return resultado;
    }

    public void registrarEspecialidad(String nombre)
            throws SQLException {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe ingresar el nombre de la especialidad."
            );
        }

        String sql = """
                INSERT INTO especialidad (nombre)
                VALUES (?)
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nombre.trim());
            ps.executeUpdate();
        }
    }

    // ============================================
    // PROFESIONALES
    // ============================================

    public List<Profesional> listarProfesionales()
            throws SQLException {

        List<Profesional> resultado = new ArrayList<>();

        String sql = """
                SELECT pr.id_profesional, pr.nombre,
                       pr.apellido, e.nombre AS especialidad
                FROM profesional pr
                JOIN especialidad e
                  ON e.id_especialidad = pr.id_especialidad
                ORDER BY pr.apellido, pr.nombre
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(new Profesional(
                        rs.getInt("id_profesional"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("especialidad")
                ));
            }
        }

        return resultado;
    }

    // NUEVO: registrar profesional.

    public void registrarProfesional(
            String nombre,
            String apellido,
            String especialidad) throws SQLException {

        if (nombre == null || nombre.trim().isEmpty()
                || apellido == null || apellido.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe ingresar nombre y apellido."
            );
        }

        if (especialidad == null
                || especialidad.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una especialidad."
            );
        }

        String sql = """
                INSERT INTO profesional
                (nombre, apellido, id_especialidad)
                SELECT ?, ?, id_especialidad
                FROM especialidad
                WHERE nombre = ?
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nombre.trim());
            ps.setString(2, apellido.trim());
            ps.setString(3, especialidad.trim());

            if (ps.executeUpdate() == 0) {
                throw new SQLException(
                        "La especialidad seleccionada no existe."
                );
            }
        }
    }

    // ============================================
    // LUGARES DE ATENCION
    // ============================================

    public List<String> listarLugares() throws SQLException {

        List<String> resultado = new ArrayList<>();

        String sql = """
                SELECT id_lugar, nombre
                FROM lugar_atencion
                ORDER BY nombre
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(
                        rs.getInt("id_lugar")
                        + " - " + rs.getString("nombre")
                );
            }
        }

        return resultado;
    }

    // ============================================
    // DISPONIBILIDAD
    // ============================================

    public List<Disponibilidad> listarDisponibilidades()
            throws SQLException {

        List<Disponibilidad> resultado = new ArrayList<>();

        String sql = """
                SELECT d.id_disponibilidad, d.fecha, d.horario,
                       d.estado, pr.id_profesional,
                       pr.nombre, pr.apellido,
                       e.nombre AS especialidad,
                       l.nombre AS lugar
                FROM disponibilidad d
                JOIN profesional pr
                  ON pr.id_profesional = d.id_profesional
                JOIN especialidad e
                  ON e.id_especialidad = pr.id_especialidad
                JOIN lugar_atencion l
                  ON l.id_lugar = d.id_lugar
                ORDER BY d.fecha, d.horario
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Profesional profesional = new Profesional(
                        rs.getInt("id_profesional"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("especialidad")
                );

                resultado.add(new Disponibilidad(
                        rs.getInt("id_disponibilidad"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("horario").toLocalTime(),
                        rs.getString("estado"),
                        profesional,
                        rs.getString("lugar")
                ));
            }
        }

        return resultado;
    }

    public void crearDisponibilidad(
            int idProfesional,
            int idLugar,
            LocalDate fecha,
            LocalTime hora) throws SQLException {

        String sql = """
                INSERT INTO disponibilidad
                (fecha, horario, estado, id_profesional, id_lugar)
                VALUES (?, ?, 'LIBRE', ?, ?)
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fecha));
            ps.setTime(2, Time.valueOf(hora));
            ps.setInt(3, idProfesional);
            ps.setInt(4, idLugar);

            ps.executeUpdate();
        }
    }

    public int generarDisponibilidades(
            int idProfesional,
            int idLugar,
            LocalDate fecha,
            LocalTime inicio,
            LocalTime fin,
            int minutos) throws SQLException {

        if (minutos <= 0 || minutos > 720
                || !fin.isAfter(inicio)) {

            throw new IllegalArgumentException(
                    "La franja horaria o duracion es incorrecta."
            );
        }

        int creadas = 0;

        for (LocalTime hora = inicio;
             !hora.plusMinutes(minutos).isAfter(fin);
             hora = hora.plusMinutes(minutos)) {

            try {

                crearDisponibilidad(
                        idProfesional,
                        idLugar,
                        fecha,
                        hora
                );

                creadas++;

            } catch (SQLIntegrityConstraintViolationException ex) {
                // Se omiten los horarios duplicados.
            }
        }

        return creadas;
    }

    // ============================================
    // TURNOS
    // ============================================

    public List<Turnos> listarTurnos() throws SQLException {

        List<Turnos> resultado = new ArrayList<>();

        String sql = """
                SELECT t.id_turno, t.estado AS estado_turno,
                       p.id_paciente, p.documento,
                       p.nombre AS nombre_paciente,
                       p.apellido AS apellido_paciente,
                       p.telefono, p.email,
                       d.id_disponibilidad, d.fecha, d.horario,
                       d.estado AS estado_disponibilidad,
                       pr.id_profesional,
                       pr.nombre AS nombre_profesional,
                       pr.apellido AS apellido_profesional,
                       e.nombre AS especialidad,
                       l.nombre AS lugar
                FROM turno t
                JOIN paciente p
                  ON p.id_paciente = t.id_paciente
                JOIN disponibilidad d
                  ON d.id_disponibilidad = t.id_disponibilidad
                JOIN profesional pr
                  ON pr.id_profesional = d.id_profesional
                JOIN especialidad e
                  ON e.id_especialidad = pr.id_especialidad
                JOIN lugar_atencion l
                  ON l.id_lugar = d.id_lugar
                ORDER BY t.id_turno DESC
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Paciente paciente = new Paciente(
                        rs.getInt("id_paciente"),
                        rs.getString("documento"),
                        rs.getString("nombre_paciente"),
                        rs.getString("apellido_paciente"),
                        rs.getString("telefono"),
                        rs.getString("email")
                );

                Profesional profesional = new Profesional(
                        rs.getInt("id_profesional"),
                        rs.getString("nombre_profesional"),
                        rs.getString("apellido_profesional"),
                        rs.getString("especialidad")
                );

                Disponibilidad disponibilidad =
                        new Disponibilidad(
                                rs.getInt("id_disponibilidad"),
                                rs.getDate("fecha").toLocalDate(),
                                rs.getTime("horario").toLocalTime(),
                                rs.getString("estado_disponibilidad"),
                                profesional,
                                rs.getString("lugar")
                        );

                resultado.add(new Turnos(
                        rs.getInt("id_turno"),
                        paciente,
                        disponibilidad,
                        rs.getString("estado_turno")
                ));
            }
        }

        return resultado;
    }

    public void asignarTurno(
            int idPaciente,
            int idDisponibilidad) throws SQLException {

        String bloquear = """
                SELECT estado
                FROM disponibilidad
                WHERE id_disponibilidad = ?
                FOR UPDATE
                """;

        String insertar = """
                INSERT INTO turno
                (estado, id_paciente, id_disponibilidad)
                VALUES ('ACTIVO', ?, ?)
                """;

        String ocupar = """
                UPDATE disponibilidad
                SET estado = 'OCUPADA'
                WHERE id_disponibilidad = ?
                """;

        try (Connection c = ConexionBD.conectar()) {

            c.setAutoCommit(false);

            try {

                try (PreparedStatement ps =
                             c.prepareStatement(bloquear)) {

                    ps.setInt(1, idDisponibilidad);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()
                                || !"LIBRE".equalsIgnoreCase(
                                        rs.getString("estado"))) {

                            throw new SQLException(
                                    "El horario ya no esta disponible."
                            );
                        }
                    }
                }

                try (PreparedStatement ps =
                             c.prepareStatement(insertar)) {

                    ps.setInt(1, idPaciente);
                    ps.setInt(2, idDisponibilidad);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             c.prepareStatement(ocupar)) {

                    ps.setInt(1, idDisponibilidad);
                    ps.executeUpdate();
                }

                c.commit();

            } catch (SQLException ex) {

                c.rollback();
                throw ex;
            }
        }
    }

    public void cancelarTurno(int idTurno)
            throws SQLException {

        String buscar = """
                SELECT id_disponibilidad, estado
                FROM turno
                WHERE id_turno = ?
                FOR UPDATE
                """;

        String cancelar = """
                UPDATE turno
                SET estado = 'CANCELADO'
                WHERE id_turno = ?
                """;

        String liberar = """
                UPDATE disponibilidad
                SET estado = 'LIBRE'
                WHERE id_disponibilidad = ?
                """;

        try (Connection c = ConexionBD.conectar()) {

            c.setAutoCommit(false);

            try {

                int idDisponibilidad;

                try (PreparedStatement ps =
                             c.prepareStatement(buscar)) {

                    ps.setInt(1, idTurno);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "El turno no existe."
                            );
                        }

                        if (!"ACTIVO".equalsIgnoreCase(
                                rs.getString("estado"))) {

                            throw new SQLException(
                                    "El turno no esta activo."
                            );
                        }

                        idDisponibilidad =
                                rs.getInt("id_disponibilidad");
                    }
                }

                try (PreparedStatement ps =
                             c.prepareStatement(cancelar)) {

                    ps.setInt(1, idTurno);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             c.prepareStatement(liberar)) {

                    ps.setInt(1, idDisponibilidad);
                    ps.executeUpdate();
                }

                c.commit();

            } catch (SQLException ex) {

                c.rollback();
                throw ex;
            }
        }
    }

    public void bloquearDisponibilidad(
            int idDisponibilidad) throws SQLException {

        String sql = """
                UPDATE disponibilidad
                SET estado = 'BLOQUEADA'
                WHERE id_disponibilidad = ?
                  AND estado = 'LIBRE'
                """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idDisponibilidad);

            if (ps.executeUpdate() == 0) {
                throw new SQLException(
                        "Solo se pueden bloquear horarios libres."
                );
            }
        }
    }
}
