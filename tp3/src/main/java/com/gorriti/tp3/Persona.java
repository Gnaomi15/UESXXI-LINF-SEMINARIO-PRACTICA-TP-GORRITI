
package com.gorriti.tp3;

public abstract class Persona {

    private final String nombre;
    private final String apellido;

    public Persona(String nombre, String apellido) {
        if (nombre == null || nombre.isBlank()
                || apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre y el apellido son obligatorios.");
        }

        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public abstract String mostrarInformacion();
}
