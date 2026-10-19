
package com.gorriti.tp3;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class VentanaPrincipal extends JFrame {

    private final ServicioHospital servicio = new ServicioHospital();

    private final JTabbedPane pestanas = new JTabbedPane();

    private final DefaultTableModel modeloPacientes =
            crearModelo("ID", "Documento", "Nombre",
                    "Apellido", "Telefono", "Email");

    private final DefaultTableModel modeloDisponibilidad =
            crearModelo("ID", "Fecha", "Hora", "Profesional",
                    "Especialidad", "Lugar", "Estado");

    private final DefaultTableModel modeloTurnos =
            crearModelo("Turno", "Documento", "Paciente",
                    "Especialidad", "Profesional",
                    "Fecha", "Hora", "Estado");

    private final JComboBox<String> filtroEspecialidad =
            new JComboBox<>();

    private final JComboBox<String> filtroProfesional =
            new JComboBox<>();

    private final JComboBox<String> filtroFecha =
            new JComboBox<>();

    private List<Disponibilidad> disponibilidades =
            new ArrayList<>();

    private List<Profesional> profesionales =
            new ArrayList<>();

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    public VentanaPrincipal() {

        setTitle("Hospital Dr. Fernando Rocha - Gestion de Turnos");
        setSize(1200, 720);
        setMinimumSize(new Dimension(950, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel encabezado = new JLabel(
                "  SISTEMA DE GESTION DE TURNOS "
                + "Y COMUNICACION CON PACIENTES");

        encabezado.setFont(new Font("Arial", Font.BOLD, 18));
        encabezado.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10));

        add(encabezado, BorderLayout.NORTH);

        pestanas.addTab("Pacientes", crearPanelPacientes());
        pestanas.addTab("Disponibilidad",
                crearPanelDisponibilidad());
        pestanas.addTab("Turnos", crearPanelTurnos());

        add(pestanas, BorderLayout.CENTER);

        JLabel pie = new JLabel(
                "  Hospital Area Programa Dr. Fernando Rocha"
                + " - Luis Beltran, Rio Negro");

        pie.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(pie, BorderLayout.SOUTH);

        refrescar();
    }

    // ============================================
    // CONFIGURACION GENERAL
    // ============================================

    private DefaultTableModel crearModelo(String... columnas) {

        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JPanel panelBase(
            String titulo,
            JPanel controles,
            DefaultTableModel modelo) {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel superior = new JPanel(new BorderLayout(5, 10));
        superior.add(etiqueta, BorderLayout.NORTH);
        superior.add(controles, BorderLayout.CENTER);

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(27);
        tabla.setAutoCreateRowSorter(true);

        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        return panel;
    }

    private void informar(String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje, "Sistema hospitalario",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private String pedir(String mensaje) {
        return JOptionPane.showInputDialog(this, mensaje);
    }

    private boolean confirmar(JPanel panel, String titulo) {
        return JOptionPane.showConfirmDialog(
                this, panel, titulo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE)
                == JOptionPane.OK_OPTION;
    }

    private JPanel formulario(
            JTextField[] campos,
            String[] etiquetas) {

        JPanel panel = new JPanel(
                new GridLayout(campos.length, 2, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < campos.length; i++) {
            panel.add(new JLabel(etiquetas[i]));
            panel.add(campos[i]);
        }

        return panel;
    }

    // ============================================
    // PESTANA PACIENTES
    // ============================================

    private JPanel crearPanelPacientes() {

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        JButton registrar = new JButton("Registrar paciente");
        JButton buscar = new JButton("Buscar por documento");
        JButton actualizar = new JButton("Actualizar");

        registrar.addActionListener(e -> registrarPaciente());
        buscar.addActionListener(e -> buscarPaciente());
        actualizar.addActionListener(e -> refrescar());

        botones.add(registrar);
        botones.add(buscar);
        botones.add(actualizar);

        return panelBase(
                "Registro de pacientes",
                botones, modeloPacientes);
    }

    private void registrarPaciente() {

        JTextField documento = new JTextField();
        JTextField nombre = new JTextField();
        JTextField apellido = new JTextField();
        JTextField telefono = new JTextField();
        JTextField email = new JTextField();

        JPanel panel = formulario(
                new JTextField[]{
                    documento, nombre, apellido, telefono, email
                },
                new String[]{
                    "Documento:", "Nombre:", "Apellido:",
                    "Telefono:", "Email:"
                });

        if (!confirmar(panel, "Registrar paciente")) {
            return;
        }

        try {
            servicio.registrarPaciente(
                    documento.getText(),
                    nombre.getText(),
                    apellido.getText(),
                    telefono.getText(),
                    email.getText());

            refrescar();
            informar("Paciente registrado correctamente.");

        } catch (SQLException | IllegalArgumentException ex) {
            mostrarError(
                    "No se pudo registrar el paciente:\n"
                    + ex.getMessage());
        }
    }

    private void buscarPaciente() {

        String documento = pedir(
                "Ingrese el DNI del paciente:");

        if (documento == null) {
            return;
        }

        documento = documento.trim();

        if (documento.isEmpty()) {
            mostrarError("Debe ingresar un DNI.");
            return;
        }

        try {
            Paciente paciente =
                    servicio.buscarPaciente(documento);

            if (paciente == null) {
                informar("No se encontro ningun paciente.");
            } else {

                Persona persona = paciente;

                informar(
                        persona.mostrarInformacion()
                        + "\nTelefono: " + paciente.getTelefono()
                        + "\nEmail: " + paciente.getEmail());
            }

        } catch (SQLException ex) {
            mostrarError(ex.getMessage());
        }
    }

    // ============================================
    // PESTANA DISPONIBILIDAD
    // ============================================

    private JPanel crearPanelDisponibilidad() {

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        JButton especialidad =
                new JButton("Agregar especialidad");

        JButton profesional =
                new JButton("Agregar profesional");

        JButton horario =
                new JButton("Agregar horario");

        JButton generar =
                new JButton("Generar horarios");

        JButton bloquear =
                new JButton("Bloquear horario");

        JButton actualizar =
                new JButton("Actualizar");

        especialidad.addActionListener(
                e -> agregarEspecialidad());

        profesional.addActionListener(
                e -> agregarProfesional());

        horario.addActionListener(
                e -> cargarHorario(false));

        generar.addActionListener(
                e -> cargarHorario(true));

        bloquear.addActionListener(
                e -> bloquearHorario());

        actualizar.addActionListener(
                e -> refrescar());

        botones.add(especialidad);
        botones.add(profesional);
        botones.add(horario);
        botones.add(generar);
        botones.add(bloquear);
        botones.add(actualizar);

        JPanel filtros = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 8));

        filtros.setBorder(
                BorderFactory.createTitledBorder(
                        "Filtrar disponibilidad"));

        filtroEspecialidad.setPreferredSize(
                new Dimension(200, 28));

        filtroProfesional.setPreferredSize(
                new Dimension(220, 28));

        filtroFecha.setPreferredSize(
                new Dimension(160, 28));

        filtros.add(new JLabel("Especialidad:"));
        filtros.add(filtroEspecialidad);

        filtros.add(new JLabel("Profesional:"));
        filtros.add(filtroProfesional);

        filtros.add(new JLabel("Fecha:"));
        filtros.add(filtroFecha);

        filtroEspecialidad.addActionListener(
                e -> actualizarFiltroProfesionales());

        filtroProfesional.addActionListener(
                e -> actualizarFiltroFechas());

        filtroFecha.addActionListener(
                e -> filtrarDisponibilidad());

        JPanel controles = new JPanel(
                new BorderLayout(5, 5));

        controles.add(botones, BorderLayout.NORTH);
        controles.add(filtros, BorderLayout.CENTER);

        return panelBase(
                "Disponibilidad de profesionales",
                controles, modeloDisponibilidad);
    }

    // ============================================
    // REGISTRO DE ESPECIALIDADES
    // ============================================

    private void agregarEspecialidad() {

        String nombre = pedir(
                "Nombre de la nueva especialidad:");

        if (nombre == null) {
            return;
        }

        nombre = nombre.trim();

        if (nombre.isEmpty()) {
            mostrarError("Debe ingresar una especialidad.");
            return;
        }

        try {

            servicio.registrarEspecialidad(nombre);
            refrescar();

            informar(
                    "Especialidad registrada correctamente:\n"
                    + nombre);

        } catch (SQLException | IllegalArgumentException ex) {

            mostrarError(
                    "No se pudo registrar la especialidad:\n"
                    + ex.getMessage());
        }
    }

    // ============================================
    // REGISTRO DE PROFESIONALES
    // ============================================

    private void agregarProfesional() {

        try {

            List<String> especialidades =
                    servicio.listarEspecialidades();

            if (especialidades.isEmpty()) {

                mostrarError(
                        "Primero debe registrar una especialidad.");
                return;
            }

            JTextField nombre = new JTextField();
            JTextField apellido = new JTextField();

            JComboBox<String> comboEspecialidad =
                    new JComboBox<>(
                            especialidades.toArray(new String[0]));

            JPanel panel = new JPanel(
                    new GridLayout(3, 2, 10, 10));

            panel.setBorder(
                    BorderFactory.createEmptyBorder(
                            10, 10, 10, 10));

            panel.add(new JLabel("Nombre:"));
            panel.add(nombre);

            panel.add(new JLabel("Apellido:"));
            panel.add(apellido);

            panel.add(new JLabel("Especialidad:"));
            panel.add(comboEspecialidad);

            if (!confirmar(panel, "Registrar profesional")) {
                return;
            }

            servicio.registrarProfesional(
                    nombre.getText(),
                    apellido.getText(),
                    (String) comboEspecialidad.getSelectedItem());

            refrescar();

            informar("Profesional registrado correctamente.");

        } catch (SQLException | IllegalArgumentException ex) {

            mostrarError(
                    "No se pudo registrar el profesional:\n"
                    + ex.getMessage());
        }
    }

    // ============================================
    // FILTROS DE DISPONIBILIDAD
    // ============================================

    private void cargarFiltrosDisponibilidad() {

        filtroEspecialidad.removeAllItems();
        filtroEspecialidad.addItem("Todas");

        Set<String> especialidades =
                new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (Profesional p : profesionales) {
            especialidades.add(p.getEspecialidad());
        }

        for (String nombre : especialidades) {
            filtroEspecialidad.addItem(nombre);
        }

        actualizarFiltroProfesionales();
    }

    private void actualizarFiltroProfesionales() {

        String especialidad =
                (String) filtroEspecialidad.getSelectedItem();

        filtroProfesional.removeAllItems();
        filtroProfesional.addItem("Todos");

        for (Profesional p : profesionales) {

            if (especialidad == null
                    || especialidad.equals("Todas")
                    || p.getEspecialidad()
                            .equalsIgnoreCase(especialidad)) {

                filtroProfesional.addItem(
                        p.getIdProfesional()
                        + " - " + p.getNombreCompleto());
            }
        }

        actualizarFiltroFechas();
    }

    private void actualizarFiltroFechas() {

        String especialidad =
                (String) filtroEspecialidad.getSelectedItem();

        String profesional =
                (String) filtroProfesional.getSelectedItem();

        filtroFecha.removeAllItems();
        filtroFecha.addItem("Todas");

        Set<LocalDate> fechas = new TreeSet<>();

        for (Disponibilidad d : disponibilidades) {

            if (coincideEspecialidad(d, especialidad)
                    && coincideProfesional(d, profesional)) {

                fechas.add(d.getFecha());
            }
        }

        for (LocalDate fecha : fechas) {
            filtroFecha.addItem(
                    fecha.format(FORMATO_FECHA));
        }

        filtrarDisponibilidad();
    }

    private boolean coincideEspecialidad(
            Disponibilidad d,
            String especialidad) {

        return especialidad == null
                || especialidad.equals("Todas")
                || d.getProfesional().getEspecialidad()
                        .equalsIgnoreCase(especialidad);
    }

    private boolean coincideProfesional(
            Disponibilidad d,
            String profesional) {

        if (profesional == null
                || profesional.equals("Todos")) {
            return true;
        }

        String id = profesional.split(" - ", 2)[0];

        try {
            return d.getProfesional().getIdProfesional()
                    == Integer.parseInt(id);

        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void filtrarDisponibilidad() {

        String especialidad =
                (String) filtroEspecialidad.getSelectedItem();

        String profesional =
                (String) filtroProfesional.getSelectedItem();

        String fecha =
                (String) filtroFecha.getSelectedItem();

        modeloDisponibilidad.setRowCount(0);

        for (Disponibilidad d : disponibilidades) {

            if (!coincideEspecialidad(d, especialidad)
                    || !coincideProfesional(d, profesional)) {
                continue;
            }

            if (fecha != null && !fecha.equals("Todas")
                    && !d.getFecha().format(FORMATO_FECHA)
                            .equals(fecha)) {
                continue;
            }

            modeloDisponibilidad.addRow(new Object[]{
                d.getIdDisponibilidad(),
                d.getFechaFormateada(),
                d.getHoraFormateada(),
                d.getProfesional().getNombreCompleto(),
                d.getProfesional().getEspecialidad(),
                d.getLugar(),
                d.getEstado()
            });
        }
    }

    // ============================================
    // CREAR Y GENERAR DISPONIBILIDADES
    // ============================================

    private void cargarHorario(boolean automatico) {

        try {

            List<Profesional> listaProfesionales =
                    servicio.listarProfesionales();

            List<String> lugares =
                    servicio.listarLugares();

            if (listaProfesionales.isEmpty()
                    || lugares.isEmpty()) {

                mostrarError(
                        "Debe haber profesionales y lugares "
                        + "registrados en MySQL.");
                return;
            }

            String[] nombres =
                    new String[listaProfesionales.size()];

            for (int i = 0;
                    i < listaProfesionales.size(); i++) {

                Profesional p = listaProfesionales.get(i);

                nombres[i] = p.getNombreCompleto()
                        + " - " + p.getEspecialidad();
            }

            JComboBox<String> comboProfesional =
                    new JComboBox<>(nombres);

            JComboBox<String> comboLugar =
                    new JComboBox<>(
                            lugares.toArray(new String[0]));

            JTextField fecha = new JTextField(
                    LocalDate.now().format(FORMATO_FECHA));

            JTextField inicio = new JTextField("08:00");
            JTextField fin = new JTextField("12:00");
            JTextField duracion = new JTextField("30");

            JPanel panel = new JPanel(
                    new GridLayout(0, 2, 10, 10));

            panel.add(new JLabel("Profesional:"));
            panel.add(comboProfesional);

            panel.add(new JLabel("Lugar:"));
            panel.add(comboLugar);

            panel.add(new JLabel("Fecha (DD/MM/AAAA):"));
            panel.add(fecha);

            panel.add(new JLabel(
                    automatico ? "Hora inicial:" : "Hora:"));
            panel.add(inicio);

            if (automatico) {
                panel.add(new JLabel("Hora final:"));
                panel.add(fin);

                panel.add(new JLabel("Duracion en minutos:"));
                panel.add(duracion);
            }

            if (!confirmar(panel, "Cargar disponibilidad")) {
                return;
            }

            Profesional profesional =
                    listaProfesionales.get(
                            comboProfesional.getSelectedIndex());

            String lugar =
                    (String) comboLugar.getSelectedItem();

            int idLugar = Integer.parseInt(
                    lugar.split(" - ", 2)[0]);

            LocalDate dia = LocalDate.parse(
                    fecha.getText().trim(), FORMATO_FECHA);

            LocalTime horaInicio = LocalTime.parse(
                    inicio.getText().trim(), FORMATO_HORA);

            if (automatico) {

                LocalTime horaFin = LocalTime.parse(
                        fin.getText().trim(), FORMATO_HORA);

                int minutos = Integer.parseInt(
                        duracion.getText().trim());

                int cantidad =
                        servicio.generarDisponibilidades(
                                profesional.getIdProfesional(),
                                idLugar, dia,
                                horaInicio, horaFin, minutos);

                informar("Horarios creados: " + cantidad);

            } else {

                servicio.crearDisponibilidad(
                        profesional.getIdProfesional(),
                        idLugar, dia, horaInicio);

                informar("Horario creado correctamente.");
            }

            refrescar();

        } catch (Exception ex) {

            mostrarError(
                    "No se pudo cargar la disponibilidad:\n"
                    + ex.getMessage());
        }
    }

    private void bloquearHorario() {

        String numero = pedir(
                "Ingrese el ID del horario que desea bloquear:");

        if (numero == null) {
            return;
        }

        try {

            int id = Integer.parseInt(numero.trim());

            servicio.bloquearDisponibilidad(id);
            refrescar();

            informar("Horario bloqueado correctamente.");

        } catch (SQLException | NumberFormatException ex) {

            mostrarError(
                    "No se pudo bloquear el horario:\n"
                    + ex.getMessage());
        }
    }

    // ============================================
    // PESTANA TURNOS
    // ============================================

    private JPanel crearPanelTurnos() {

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        JButton asignar = new JButton("Asignar turno");
        JButton buscar = new JButton("Buscar por documento");
        JButton cancelar = new JButton("Cancelar turno");
        JButton actualizar = new JButton("Actualizar");

        asignar.addActionListener(e -> asignarTurno());
        buscar.addActionListener(e -> buscarTurnos());
        cancelar.addActionListener(e -> cancelarTurno());
        actualizar.addActionListener(e -> refrescar());

        botones.add(asignar);
        botones.add(buscar);
        botones.add(cancelar);
        botones.add(actualizar);

        return panelBase(
                "Turnos hospitalarios",
                botones, modeloTurnos);
    }

    // ============================================
    // ASIGNACION DE TURNOS
    // DNI -> ESPECIALIDAD -> FECHA -> HORARIO
    // ============================================

    private void asignarTurno() {

        String documento = pedir(
                "Ingrese el DNI del paciente:");

        if (documento == null) {
            return;
        }

        documento = documento.trim();

        if (documento.isEmpty()) {
            mostrarError("Debe ingresar un DNI.");
            return;
        }

        try {

            Paciente paciente =
                    servicio.buscarPaciente(documento);

            if (paciente == null) {

                mostrarError(
                        "No se encontro ningun paciente "
                        + "con el DNI " + documento);
                return;
            }

            List<Disponibilidad> libres = new ArrayList<>();

            LocalDateTime ahora = LocalDateTime.now();

            for (Disponibilidad d :
                    servicio.listarDisponibilidades()) {

                boolean libre =
                        "LIBRE".equalsIgnoreCase(d.getEstado());

                LocalDateTime fechaHora =
                        LocalDateTime.of(
                                d.getFecha(), d.getHorario());

                if (libre && fechaHora.isAfter(ahora)) {
                    libres.add(d);
                }
            }

            if (libres.isEmpty()) {

                informar(
                        "No hay turnos disponibles "
                        + "para fechas futuras.");
                return;
            }

            libres.sort(
                    Comparator
                            .comparing(Disponibilidad::getFecha)
                            .thenComparing(
                                    Disponibilidad::getHorario));

            Set<String> especialidades =
                    new TreeSet<>(
                            String.CASE_INSENSITIVE_ORDER);

            for (Disponibilidad d : libres) {
                especialidades.add(
                        d.getProfesional().getEspecialidad());
            }

            JComboBox<String> comboEspecialidad =
                    new JComboBox<>(
                            especialidades.toArray(new String[0]));

            JComboBox<String> comboFecha =
                    new JComboBox<>();

            JComboBox<Disponibilidad> comboTurnos =
                    new JComboBox<>();

            comboTurnos.setPreferredSize(
                    new Dimension(650, 30));

            Runnable actualizarTurnos = () -> {

                comboTurnos.removeAllItems();

                String especialidad =
                        (String) comboEspecialidad.getSelectedItem();

                String fecha =
                        (String) comboFecha.getSelectedItem();

                if (especialidad == null || fecha == null) {
                    return;
                }

                for (Disponibilidad d : libres) {

                    if (d.getProfesional().getEspecialidad()
                            .equalsIgnoreCase(especialidad)
                            && d.getFecha().format(FORMATO_FECHA)
                                    .equals(fecha)) {

                        comboTurnos.addItem(d);
                    }
                }
            };

            Runnable actualizarFechas = () -> {

                String especialidad =
                        (String) comboEspecialidad.getSelectedItem();

                comboFecha.removeAllItems();

                if (especialidad == null) {
                    comboTurnos.removeAllItems();
                    return;
                }

                Set<LocalDate> fechas = new TreeSet<>();

                for (Disponibilidad d : libres) {

                    if (d.getProfesional().getEspecialidad()
                            .equalsIgnoreCase(especialidad)) {

                        fechas.add(d.getFecha());
                    }
                }

                for (LocalDate fecha : fechas) {
                    comboFecha.addItem(
                            fecha.format(FORMATO_FECHA));
                }

                actualizarTurnos.run();
            };

            comboEspecialidad.addActionListener(
                    e -> actualizarFechas.run());

            comboFecha.addActionListener(
                    e -> actualizarTurnos.run());

            actualizarFechas.run();

            JPanel panel = new JPanel(
                    new GridLayout(4, 2, 10, 10));

            panel.setBorder(
                    BorderFactory.createEmptyBorder(
                            10, 10, 10, 10));

            panel.add(new JLabel("Paciente encontrado:"));
            panel.add(new JLabel(
                    paciente.getNombreCompleto()
                    + " - DNI: " + paciente.getDocumento()));

            panel.add(new JLabel("Especialidad:"));
            panel.add(comboEspecialidad);

            panel.add(new JLabel("Fecha disponible:"));
            panel.add(comboFecha);

            panel.add(new JLabel("Turnos disponibles:"));
            panel.add(comboTurnos);

            if (!confirmar(panel, "Asignar turno")) {
                return;
            }

            Disponibilidad seleccionada =
                    (Disponibilidad) comboTurnos.getSelectedItem();

            if (seleccionada == null) {
                mostrarError(
                        "Debe seleccionar un turno disponible.");
                return;
            }

            servicio.asignarTurno(
                    paciente.getIdPaciente(),
                    seleccionada.getIdDisponibilidad());

            refrescar();
            pestanas.setSelectedIndex(2);

            informar(
                    "Turno asignado correctamente.\n\n"
                    + "Paciente: " + paciente.getNombreCompleto()
                    + "\nDNI: " + paciente.getDocumento()
                    + "\nEspecialidad: "
                    + seleccionada.getProfesional().getEspecialidad()
                    + "\nProfesional: "
                    + seleccionada.getProfesional().getNombreCompleto()
                    + "\nFecha: " + seleccionada.getFechaFormateada()
                    + "\nHora: " + seleccionada.getHoraFormateada());

        } catch (SQLException ex) {

            mostrarError(
                    "No se pudo asignar el turno:\n"
                    + ex.getMessage());
        }
    }

    // ============================================
    // BUSCAR Y CANCELAR TURNOS
    // ============================================

    private void buscarTurnos() {

        String documento = pedir(
                "Ingrese el DNI del paciente:");

        if (documento == null) {
            return;
        }

        documento = documento.trim();

        if (documento.isEmpty()) {
            mostrarError("Debe ingresar un DNI.");
            return;
        }

        try {

            StringBuilder resultado = new StringBuilder();

            for (Turnos turno : servicio.listarTurnos()) {

                if (turno.getPaciente().getDocumento()
                        .equals(documento)) {

                    resultado.append(turno.mostrarDatos())
                            .append("\n\n-----------------\n\n");
                }
            }

            informar(
                    resultado.length() == 0
                    ? "No se encontraron turnos para ese DNI."
                    : resultado.toString());

        } catch (SQLException ex) {

            mostrarError(
                    "Error al buscar los turnos:\n"
                    + ex.getMessage());
        }
    }

    private void cancelarTurno() {

        String numero = pedir(
                "Ingrese el numero del turno que desea cancelar:");

        if (numero == null) {
            return;
        }

        try {

            int id = Integer.parseInt(numero.trim());

            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Confirma la cancelacion del turno " + id + "?",
                    "Confirmar cancelacion",
                    JOptionPane.YES_NO_OPTION);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }

            servicio.cancelarTurno(id);
            refrescar();

            informar("Turno cancelado correctamente.");

        } catch (SQLException | NumberFormatException ex) {

            mostrarError(
                    "No se pudo cancelar el turno:\n"
                    + ex.getMessage());
        }
    }

    // ============================================
    // ACTUALIZAR DATOS DESDE MYSQL
    // ============================================

    private void refrescar() {

        try {

            List<Paciente> pacientes =
                    servicio.listarPacientes();

            profesionales =
                    servicio.listarProfesionales();

            disponibilidades =
                    servicio.listarDisponibilidades();

            List<Turnos> turnos =
                    servicio.listarTurnos();

            modeloPacientes.setRowCount(0);
            modeloTurnos.setRowCount(0);

            for (Paciente p : pacientes) {

                modeloPacientes.addRow(new Object[]{
                    p.getIdPaciente(),
                    p.getDocumento(),
                    p.getNombre(),
                    p.getApellido(),
                    p.getTelefono(),
                    p.getEmail()
                });
            }

            cargarFiltrosDisponibilidad();

            for (Turnos t : turnos) {

                Disponibilidad d = t.getDisponibilidad();

                modeloTurnos.addRow(new Object[]{
                    t.getIdTurno(),
                    t.getPaciente().getDocumento(),
                    t.getPaciente().getNombreCompleto(),
                    d.getProfesional().getEspecialidad(),
                    d.getProfesional().getNombreCompleto(),
                    d.getFechaFormateada(),
                    d.getHoraFormateada(),
                    t.getEstado()
                });
            }

        } catch (SQLException ex) {

            mostrarError(
                    "Error al consultar MySQL:\n"
                    + ex.getMessage());
        }
    }
}
