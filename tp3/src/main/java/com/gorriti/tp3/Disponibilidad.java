
package com.gorriti.tp3;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Disponibilidad {

    private final int idDisponibilidad;
    private final LocalDate fecha;
    private final LocalTime horario;
    private final String estado;
    private final Profesional profesional;
    private final String lugar;

    public Disponibilidad(int idDisponibilidad,
                          LocalDate fecha,
                          LocalTime horario,
                          String estado,
                          Profesional profesional,
                          String lugar) {

        this.idDisponibilidad = idDisponibilidad;
        this.fecha = fecha;
        this.horario = horario;
        this.estado = estado;
        this.profesional = profesional;
        this.lugar = lugar;
    }

    public int getIdDisponibilidad() {
        return idDisponibilidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public String getEstado() {
        return estado;
    }

    public Profesional getProfesional() {
        return profesional;
    }

    public String getLugar() {
        return lugar;
    }

    public String getFechaFormateada() {
        return fecha.format(
                DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }

    public String getHoraFormateada() {
        return horario.format(
                DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public String toString() {
        return getFechaFormateada()
                + " - " + getHoraFormateada()
                + " - " + profesional.getNombreCompleto()
                + " - " + profesional.getEspecialidad()
                + " - " + lugar;
    }
}
