
package com.gorriti.tp3;

public class Paciente extends Persona {

    private final int idPaciente;
    private final String documento;
    private final String telefono;
    private final String email;

    public Paciente(int idPaciente, String documento,
                    String nombre, String apellido,
                    String telefono, String email) {

        super(nombre, apellido);

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento es obligatorio.");
        }

        this.idPaciente = idPaciente;
        this.documento = documento.trim();
        this.telefono = telefono == null ? "" : telefono.trim();
        this.email = email == null ? "" : email.trim();
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String mostrarInformacion() {
        return "Paciente: " + getNombreCompleto()
                + " - Documento: " + documento;
    }
}
