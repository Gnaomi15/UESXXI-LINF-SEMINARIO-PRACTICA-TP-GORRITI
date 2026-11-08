package com.gorriti.tp3;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/** Controlador: mantiene la vista separada de las operaciones del servicio. */
public class ControladorHospital {
    private final ServicioHospital servicio;
    public ControladorHospital() { this(new ServicioHospital()); }
    public ControladorHospital(ServicioHospital servicio) { this.servicio = servicio; }
    public List<Paciente> listarPacientesPorEstado(boolean activo) throws SQLException { return servicio.listarPacientesPorEstado(activo); }
    public Paciente buscarPaciente(String dni) throws SQLException { return servicio.buscarPaciente(dni); }
    public void registrarPaciente(String dni,String nombre,String apellido,String tel,String email) throws SQLException { servicio.registrarPaciente(dni,nombre,apellido,tel,email); }
    public void editarPaciente(int id,String dni,String nombre,String apellido,String tel,String email) throws SQLException { servicio.editarPaciente(id,dni,nombre,apellido,tel,email); }
    public void cambiarEstadoPaciente(int id,boolean activo) throws SQLException { servicio.cambiarEstadoPaciente(id,activo); }
    public List<String> listarEspecialidades() throws SQLException { return servicio.listarEspecialidades(); }
    public void registrarEspecialidad(String nombre) throws SQLException { servicio.registrarEspecialidad(nombre); }
    public List<Profesional> listarProfesionales() throws SQLException { return servicio.listarProfesionalesPorEstado(true); }
    public List<Profesional> listarProfesionalesPorEstado(boolean activo) throws SQLException { return servicio.listarProfesionalesPorEstado(activo); }
    public void registrarProfesional(String nombre,String apellido,String esp) throws SQLException { servicio.registrarProfesional(nombre,apellido,esp); }
    public void editarProfesional(int id,String nombre,String apellido,String esp) throws SQLException { servicio.editarProfesional(id,nombre,apellido,esp); }
    public void cambiarEstadoProfesional(int id,boolean activo) throws SQLException { servicio.cambiarEstadoProfesional(id,activo); }
    public List<String> listarLugares() throws SQLException { return servicio.listarLugares(); }
    public void registrarLugar(String nombre,String direccion) throws SQLException { servicio.registrarLugar(nombre,direccion); }
    public List<Disponibilidad> listarDisponibilidades() throws SQLException { return servicio.listarDisponibilidades(); }
    public List<Disponibilidad> filtrarDisponibilidades(String esp,Integer profesional,LocalDate fecha) throws SQLException {
        return listarDisponibilidades().stream().filter(d -> esp == null || esp.equals("Todas") || esp.equals(d.getProfesional().getEspecialidad()))
            .filter(d -> profesional == null || d.getProfesional().getIdProfesional() == profesional)
            .filter(d -> fecha == null || d.getFecha().equals(fecha)).collect(Collectors.toList());
    }
    public List<Disponibilidad> obtenerHorariosLibres() throws SQLException {
        return listarDisponibilidades().stream().filter(d -> "LIBRE".equalsIgnoreCase(d.getEstado()))
            .filter(d -> LocalDateTime.of(d.getFecha(),d.getHorario()).isAfter(LocalDateTime.now())).collect(Collectors.toList());
    }
    public void crearDisponibilidad(int profesional,int lugar,LocalDate fecha,LocalTime hora) throws SQLException { servicio.crearDisponibilidad(profesional,lugar,fecha,hora); }
    public int generarDisponibilidades(int profesional,int lugar,LocalDate fecha,LocalTime desde,LocalTime hasta,int minutos) throws SQLException { return servicio.generarDisponibilidades(profesional,lugar,fecha,desde,hasta,minutos); }
    public void bloquearDisponibilidad(int id) throws SQLException { servicio.bloquearDisponibilidad(id); }
    public void desbloquearDisponibilidad(int id) throws SQLException { servicio.desbloquearDisponibilidad(id); }
    public void asignarTurno(int paciente,int disponibilidad) throws SQLException { servicio.asignarTurno(paciente,disponibilidad); }
    public void cancelarTurno(int id) throws SQLException { servicio.cancelarTurno(id); }
    public List<Turnos> listarTurnosVigentes() throws SQLException {
        return servicio.listarTurnos().stream().filter(t -> "ACTIVO".equalsIgnoreCase(t.getEstado()))
            .filter(t -> LocalDateTime.of(t.getDisponibilidad().getFecha(),t.getDisponibilidad().getHorario()).isAfter(LocalDateTime.now())).collect(Collectors.toList());
    }
    public List<Turnos> listarHistorial() throws SQLException {
        return servicio.listarTurnos().stream().filter(t -> !"ACTIVO".equalsIgnoreCase(t.getEstado()) ||
            !LocalDateTime.of(t.getDisponibilidad().getFecha(),t.getDisponibilidad().getHorario()).isAfter(LocalDateTime.now())).collect(Collectors.toList());
    }
    public List<Turnos> buscarTurnosPorDocumento(String dni) throws SQLException {
        return servicio.listarTurnos().stream().filter(t -> t.getPaciente().getDocumento().equals(dni)).collect(Collectors.toList());
    }
}
