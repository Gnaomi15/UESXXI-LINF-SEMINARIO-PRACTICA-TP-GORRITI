
package com.gorriti.tp3;

public class Turnos {

    private final int idTurno;
    private final Paciente paciente;
    private final Disponibilidad disponibilidad;
    private final String estado;

    public Turnos(int idTurno, Paciente paciente,
                  Disponibilidad disponibilidad, String estado) {

        this.idTurno = idTurno;
        this.paciente = paciente;
        this.disponibilidad = disponibilidad;
        this.estado = estado;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Disponibilidad getDisponibilidad() {
        return disponibilidad;
    }

    public String getEstado() {
        return estado;
    }

    public String mostrarDatos() {
        return "Turno Nro.: " + idTurno
                + "\nPaciente: " + paciente.getNombreCompleto()
                + "\nDocumento: " + paciente.getDocumento()
                + "\nEspecialidad: "
                + disponibilidad.getProfesional().getEspecialidad()
                + "\nProfesional: "
                + disponibilidad.getProfesional().getNombreCompleto()
                + "\nFecha: " + disponibilidad.getFechaFormateada()
                + "\nHora: " + disponibilidad.getHoraFormateada()
                + "\nLugar: " + disponibilidad.getLugar()
                + "\nEstado: " + estado;
    }
}
