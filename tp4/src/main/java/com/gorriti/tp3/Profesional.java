
package com.gorriti.tp3;

public class Profesional extends Persona {

    private final int idProfesional;
    private final String especialidad;

    public Profesional(int idProfesional, String nombre,
                       String apellido, String especialidad) {

        super(nombre, apellido);

        this.idProfesional = idProfesional;
        this.especialidad = especialidad;
    }

    public int getIdProfesional() {
        return idProfesional;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    @Override
    public String mostrarInformacion() {
        return "Profesional: " + getNombreCompleto()
                + " - Especialidad: " + especialidad;
    }
}
