
package com.gorriti.tp3;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class VentanaPrincipal extends JFrame {

    // =====================================================
    // IDENTIDAD VISUAL
    // =====================================================

    private static final Color AZUL = new Color(24, 71, 105);
    private static final Color AZUL_OSCURO = new Color(19, 60, 91);
    private static final Color TURQUESA = new Color(16, 132, 151);
    private static final Color FONDO = new Color(243, 248, 251);
    private static final Color BORDE = new Color(209, 224, 234);
    private static final Color SELECCION = new Color(220, 237, 243);

    private static final Font FUENTE =
            new Font("Segoe UI", Font.PLAIN, 13);

    private static final Font FUENTE_BOTON =
            new Font("Segoe UI", Font.BOLD, 13);

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    // =====================================================
    // CONTROLADOR Y NAVEGACIÓN
    // =====================================================

    private final ControladorHospital controlador =
            new ControladorHospital();

    private final CardLayout tarjetas = new CardLayout();

    private final JPanel contenido = new JPanel(tarjetas);

    private final JLabel subtitulo = new JLabel("Inicio");

    private final Map<String, JButton> botonesMenu =
            new LinkedHashMap<>();

    private String paginaActual = "Inicio";

    // =====================================================
    // MODELOS DE TABLAS
    // =====================================================

    private final DefaultTableModel modeloPacientes =
            modelo("ID", "Documento", "Nombre", "Apellido",
                    "Teléfono", "Email", "Estado");

    private final DefaultTableModel modeloProfesionales =
            modelo("ID", "Nombre", "Apellido",
                    "Especialidad", "Estado");

    private final DefaultTableModel modeloDisponibilidad =
            modelo("ID", "Fecha", "Hora", "Profesional",
                    "Especialidad", "Lugar", "Estado");

    private final DefaultTableModel modeloTurnos =
            modelo("ID", "DNI", "Paciente", "Especialidad",
                    "Profesional", "Fecha", "Hora",
                    "Lugar", "Estado");

    private final DefaultTableModel modeloHistorial =
            modelo("ID", "DNI", "Paciente", "Especialidad",
                    "Profesional", "Fecha", "Hora",
                    "Lugar", "Estado");

    private final JTable tablaPacientes = tabla(modeloPacientes);
    private final JTable tablaProfesionales = tabla(modeloProfesionales);
    private final JTable tablaDisponibilidad = tabla(modeloDisponibilidad);
    private final JTable tablaTurnos = tabla(modeloTurnos);
    private final JTable tablaHistorial = tabla(modeloHistorial);

    // =====================================================
    // FILTROS
    // =====================================================

    private final JCheckBox pacientesInactivos =
            new JCheckBox("Ver inactivos");

    private final JCheckBox profesionalesInactivos =
            new JCheckBox("Ver inactivos");

    private final JComboBox<String> filtroEspecialidad = combo();
    private final JComboBox<String> filtroProfesional = combo();

    private final JTextField filtroFecha = campo(11);

    // =====================================================
    // DATOS
    // =====================================================

    private List<Paciente> listaPacientes = new ArrayList<>();
    private List<Profesional> listaProfesionales = new ArrayList<>();
    private List<Profesional> profesionalesActivos = new ArrayList<>();
    private List<Disponibilidad> listaDisponibilidad = new ArrayList<>();
    private List<Turnos> listaTurnos = new ArrayList<>();

    private boolean cargando = false;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public VentanaPrincipal() {

        super("Sistema de Gestión de Turnos y Comunicación con Pacientes");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1350, 800);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FONDO);
        setContentPane(raiz);

        raiz.add(crearMenuLateral(), BorderLayout.WEST);

        JPanel principal = new JPanel(new BorderLayout(0, 20));
        principal.setBackground(FONDO);
        principal.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel encabezado = new JLabel(
                "SISTEMA DE GESTIÓN DE TURNOS Y COMUNICACIÓN CON PACIENTES"
        );

        encabezado.setForeground(AZUL);
        encabezado.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );

        subtitulo.setForeground(TURQUESA);
        subtitulo.setFont(
                new Font("Segoe UI", Font.BOLD, 24)
        );

        JPanel superior = new JPanel(new GridLayout(2, 1, 0, 8));
        superior.setOpaque(false);
        superior.add(encabezado);
        superior.add(subtitulo);

        principal.add(superior, BorderLayout.NORTH);

        contenido.setOpaque(false);

        contenido.add(panelInicio(), "Inicio");
        contenido.add(panelPacientes(), "Pacientes");
        contenido.add(panelProfesionales(), "Profesionales");
        contenido.add(panelDisponibilidad(), "Disponibilidad");
        contenido.add(panelTurnos(), "Turnos");
        contenido.add(panelHistorial(), "Historial");

        principal.add(contenido, BorderLayout.CENTER);
        raiz.add(principal, BorderLayout.CENTER);

        pacientesInactivos.addActionListener(e -> actualizar());
        profesionalesInactivos.addActionListener(e -> actualizar());

        filtroEspecialidad.addActionListener(e -> {
            if (!cargando) {
                cargarFiltroProfesionales();
                filtrarDisponibilidad();
            }
        });

        filtroProfesional.addActionListener(e -> {
            if (!cargando) {
                filtrarDisponibilidad();
            }
        });

        actualizar();

        mostrarPagina("Inicio");
    }

    // =====================================================
    // MENÚ LATERAL
    // =====================================================

    private JPanel crearMenuLateral() {

        JPanel lateral = new JPanel();

        lateral.setLayout(
                new BoxLayout(lateral, BoxLayout.Y_AXIS)
        );

        lateral.setBackground(AZUL);
        lateral.setPreferredSize(new Dimension(245, 0));
        lateral.setBorder(new EmptyBorder(28, 15, 20, 15));

        JLabel marca = new JLabel("HOSPITAL");
        marca.setFont(new Font("Segoe UI", Font.BOLD, 24));
        marca.setForeground(Color.WHITE);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hospital = new JLabel("Dr. Fernando Rocha");
        hospital.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hospital.setForeground(new Color(210, 232, 240));
        hospital.setAlignmentX(Component.LEFT_ALIGNMENT);

        lateral.add(marca);
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(hospital);
        lateral.add(Box.createVerticalStrut(45));

        String[] secciones = {
            "Inicio",
            "Pacientes",
            "Profesionales",
            "Disponibilidad",
            "Turnos",
            "Historial"
        };

        for (String seccion : secciones) {

            JButton boton = new JButton("  " + seccion);

            boton.setFont(
                    new Font("Segoe UI", Font.BOLD, 14)
            );

            boton.setForeground(AZUL);
            boton.setBackground(Color.WHITE);

            boton.setOpaque(true);
            boton.setContentAreaFilled(true);
            boton.setFocusPainted(false);
            boton.setBorderPainted(true);

            boton.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDE),
                            new EmptyBorder(13, 13, 13, 13)
                    )
            );

            boton.setHorizontalAlignment(SwingConstants.LEFT);
            boton.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 49)
            );

            boton.setAlignmentX(Component.LEFT_ALIGNMENT);

            boton.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            );

            boton.addActionListener(e -> mostrarPagina(seccion));

            botonesMenu.put(seccion, boton);

            lateral.add(boton);
            lateral.add(Box.createVerticalStrut(9));
        }

        return lateral;
    }

    private void mostrarPagina(String pagina) {

        paginaActual = pagina;

        tarjetas.show(contenido, pagina);
        subtitulo.setText(pagina);

        for (Map.Entry<String, JButton> entrada
                : botonesMenu.entrySet()) {

            JButton boton = entrada.getValue();

            boolean seleccionado =
                    entrada.getKey().equals(pagina);

            boton.setBackground(
                    seleccionado ? SELECCION : Color.WHITE
            );

            boton.setForeground(AZUL_OSCURO);
        }

        if (!"Inicio".equals(pagina)) {
            actualizar();
        }
    }

    // =====================================================
    // PANTALLA INICIO
    // =====================================================

    private JPanel panelInicio() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO);

        JPanel bienvenida = new JPanel();
        bienvenida.setLayout(
                new BoxLayout(bienvenida, BoxLayout.Y_AXIS)
        );

        bienvenida.setBackground(Color.WHITE);
        bienvenida.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE),
                        new EmptyBorder(45, 45, 45, 45)
                )
        );

        JLabel titulo = new JLabel(
                "Bienvenido al Sistema de Gestión de Turnos"
        );

        titulo.setFont(
                new Font("Segoe UI", Font.BOLD, 27)
        );

        titulo.setForeground(AZUL);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel institucion = new JLabel(
                "Hospital Dr. Fernando Rocha"
        );

        institucion.setFont(
                new Font("Segoe UI", Font.BOLD, 20)
        );

        institucion.setForeground(TURQUESA);
        institucion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel localidad = new JLabel(
                "Luis Beltrán, Río Negro"
        );

        localidad.setFont(
                new Font("Segoe UI", Font.PLAIN, 15)
        );

        localidad.setForeground(AZUL);
        localidad.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descripcion = new JTextArea(
                "Este sistema permite gestionar pacientes, profesionales "
                + "de salud, especialidades, lugares de atención, "
                + "disponibilidades y turnos médicos.\n\n"
                + "Desde el menú lateral podés registrar y consultar "
                + "pacientes, administrar profesionales, generar horarios "
                + "de atención, asignar y cancelar turnos, y consultar "
                + "el historial de operaciones."
        );

        descripcion.setFont(
                new Font("Segoe UI", Font.PLAIN, 16)
        );

        descripcion.setForeground(AZUL);
        descripcion.setBackground(Color.WHITE);
        descripcion.setEditable(false);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setFocusable(false);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        descripcion.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 170)
        );

        bienvenida.add(titulo);
        bienvenida.add(Box.createVerticalStrut(16));
        bienvenida.add(institucion);
        bienvenida.add(Box.createVerticalStrut(8));
        bienvenida.add(localidad);
        bienvenida.add(Box.createVerticalStrut(30));
        bienvenida.add(descripcion);

        JPanel accesos = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 12)
        );

        accesos.setOpaque(false);

        JButton pacientes = crearBoton("Gestionar pacientes");
        pacientes.addActionListener(
                e -> mostrarPagina("Pacientes")
        );

        JButton turnos = crearBoton("Gestionar turnos");
        turnos.addActionListener(
                e -> mostrarPagina("Turnos")
        );

        JButton disponibilidad =
                crearBoton("Consultar disponibilidad");

        disponibilidad.addActionListener(
                e -> mostrarPagina("Disponibilidad")
        );

        accesos.add(pacientes);
        accesos.add(turnos);
        accesos.add(disponibilidad);

        JPanel centro = new JPanel(new BorderLayout(0, 15));
        centro.setOpaque(false);
        centro.add(bienvenida, BorderLayout.CENTER);
        centro.add(accesos, BorderLayout.SOUTH);

        panel.add(centro, BorderLayout.NORTH);

        return panel;
    }

    // =====================================================
    // COMPONENTES VISUALES
    // =====================================================

    private static DefaultTableModel modelo(String... columnas) {

        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JTable tabla(DefaultTableModel modelo) {

        JTable tabla = new JTable(modelo);

        tabla.setFont(FUENTE);
        tabla.setRowHeight(35);

        tabla.setBackground(Color.WHITE);
        tabla.setForeground(AZUL_OSCURO);
        tabla.setGridColor(BORDE);

        tabla.setSelectionBackground(SELECCION);
        tabla.setSelectionForeground(AZUL);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);

        JTableHeader encabezado = tabla.getTableHeader();

        encabezado.setPreferredSize(new Dimension(10, 40));
        encabezado.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();

        renderer.setBackground(AZUL);
        renderer.setForeground(Color.WHITE);
        renderer.setOpaque(true);

        renderer.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        renderer.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        renderer.setBorder(
                new EmptyBorder(8, 10, 8, 10)
        );

        encabezado.setDefaultRenderer(renderer);
        encabezado.setBackground(AZUL);
        encabezado.setForeground(Color.WHITE);

        return tabla;
    }

    private JButton crearBoton(String texto) {

        JButton boton = new JButton(texto);

        boton.setFont(FUENTE_BOTON);
        boton.setForeground(AZUL);
        boton.setBackground(Color.WHITE);

        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AZUL),
                        new EmptyBorder(10, 14, 10, 14)
                )
        );

        boton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return boton;
    }

    private JTextField campo(int ancho) {

        JTextField campo = new JTextField(ancho);

        campo.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        campo.setForeground(AZUL);
        campo.setBackground(Color.WHITE);

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE),
                        new EmptyBorder(9, 10, 9, 10)
                )
        );

        return campo;
    }

    private JComboBox<String> combo() {

        JComboBox<String> combo = new JComboBox<>();

        combo.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        combo.setForeground(AZUL);
        combo.setBackground(Color.WHITE);

        combo.setOpaque(true);
        combo.setPreferredSize(new Dimension(200, 38));

        combo.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista,
                    Object valor,
                    int indice,
                    boolean seleccionado,
                    boolean foco) {

                JLabel etiqueta =
                        (JLabel) super.getListCellRendererComponent(
                                lista,
                                valor,
                                indice,
                                seleccionado,
                                foco
                        );

                etiqueta.setFont(FUENTE);
                etiqueta.setOpaque(true);

                etiqueta.setBorder(
                        new EmptyBorder(7, 10, 7, 10)
                );

                etiqueta.setBackground(
                        seleccionado ? SELECCION : Color.WHITE
                );

                etiqueta.setForeground(AZUL);

                return etiqueta;
            }
        });

        return combo;
    }

    private JPanel barra() {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 8)
        );

        panel.setOpaque(false);

        return panel;
    }

    private JPanel panel(JTable tabla, JPanel controles) {

        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(FONDO);

        JScrollPane scroll = new JScrollPane(tabla);

        scroll.setBorder(
                BorderFactory.createLineBorder(BORDE)
        );

        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(controles, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel formulario(
            String[] etiquetas,
            JComponent... componentes) {

        JPanel panel = new JPanel(
                new GridLayout(etiquetas.length, 2, 12, 12)
        );

        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        for (int i = 0; i < etiquetas.length; i++) {

            JLabel etiqueta = new JLabel(etiquetas[i]);

            etiqueta.setFont(FUENTE_BOTON);
            etiqueta.setForeground(AZUL);

            panel.add(etiqueta);
            panel.add(componentes[i]);
        }

        return panel;
    }

    private boolean aceptar(String titulo, JPanel formulario) {

        return JOptionPane.showConfirmDialog(
                this,
                formulario,
                titulo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        ) == JOptionPane.OK_OPTION;
    }

    // =====================================================
    // UTILIDADES
    // =====================================================

    private interface Trabajo {
        void run() throws Exception;
    }

    private void ejecutar(Trabajo trabajo) {

        try {
            trabajo.run();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Atención",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void accion(
            JPanel panel,
            String texto,
            Trabajo trabajo) {

        JButton boton = crearBoton(texto);

        boton.addActionListener(
                e -> ejecutar(trabajo)
        );

        panel.add(boton);
    }

    private void aviso(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Hospital",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private int idSeleccionado(JTable tabla) {

        int filaVista = tabla.getSelectedRow();

        if (filaVista < 0) {
            throw new IllegalArgumentException(
                    "Seleccioná una fila de la tabla."
            );
        }

        int filaModelo =
                tabla.convertRowIndexToModel(filaVista);

        return Integer.parseInt(
                tabla.getModel()
                        .getValueAt(filaModelo, 0)
                        .toString()
        );
    }

    private static String valor(JTextField campo) {
        return campo.getText().trim();
    }

    private static void requerido(String... valores) {

        for (String valor : valores) {

            if (valor == null || valor.isBlank()) {
                throw new IllegalArgumentException(
                        "Completá los campos obligatorios."
                );
            }
        }
    }

    private static void limpiar(DefaultTableModel modelo) {
        modelo.setRowCount(0);
    }

    private static String etiqueta(Profesional profesional) {

        return profesional.getIdProfesional()
                + " - "
                + profesional.getNombreCompleto()
                + " — "
                + profesional.getEspecialidad();
    }

    private static int idDe(String texto) {

        return Integer.parseInt(
                texto.substring(0, texto.indexOf(" - "))
        );
    }

    private static void configurarCheck(JCheckBox check) {

        check.setOpaque(false);
        check.setForeground(AZUL);
        check.setFont(FUENTE);
        check.setFocusPainted(false);
    }

    // =====================================================
    // PACIENTES
    // =====================================================

    private JPanel panelPacientes() {

        JPanel controles = barra();

        configurarCheck(pacientesInactivos);

        accion(controles, "Registrar paciente",
                () -> formularioPaciente(null));

        accion(controles, "Editar",
                () -> formularioPaciente(pacienteElegido()));

        accion(controles, "Activar / desactivar", () -> {

            Paciente paciente = pacienteElegido();

            boolean activar = pacientesInactivos.isSelected();

            controlador.cambiarEstadoPaciente(
                    paciente.getIdPaciente(),
                    activar
            );

            actualizar();

            aviso(activar
                    ? "Paciente reactivado correctamente."
                    : "Paciente desactivado correctamente.");
        });

        accion(controles, "Buscar DNI", () -> {

            String dni = JOptionPane.showInputDialog(
                    this,
                    "Documento del paciente:"
            );

            if (dni == null) {
                return;
            }

            Paciente paciente =
                    controlador.buscarPaciente(dni.trim());

            aviso(paciente == null
                    ? "Paciente no encontrado."
                    : paciente.mostrarInformacion());
        });

        controles.add(pacientesInactivos);

        accion(controles, "Actualizar", this::actualizar);

        return panel(tablaPacientes, controles);
    }

    private Paciente pacienteElegido() {

        int id = idSeleccionado(tablaPacientes);

        return listaPacientes.stream()
                .filter(p -> p.getIdPaciente() == id)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Paciente no encontrado."
                        ));
    }

    private void formularioPaciente(Paciente paciente)
            throws Exception {

        JTextField dni = campo(18);
        JTextField nombre = campo(18);
        JTextField apellido = campo(18);
        JTextField telefono = campo(18);
        JTextField email = campo(18);

        if (paciente != null) {

            dni.setText(paciente.getDocumento());
            nombre.setText(paciente.getNombre());
            apellido.setText(paciente.getApellido());
            telefono.setText(paciente.getTelefono());
            email.setText(paciente.getEmail());
        }

        JPanel formulario = formulario(
                new String[]{
                    "Documento",
                    "Nombre",
                    "Apellido",
                    "Teléfono",
                    "Correo electrónico"
                },
                dni, nombre, apellido, telefono, email
        );

        if (!aceptar(
                paciente == null
                        ? "Registrar paciente"
                        : "Editar paciente",
                formulario)) {
            return;
        }

        requerido(
                valor(dni),
                valor(nombre),
                valor(apellido)
        );

        if (paciente == null) {

            controlador.registrarPaciente(
                    valor(dni),
                    valor(nombre),
                    valor(apellido),
                    valor(telefono),
                    valor(email)
            );

        } else {

            controlador.editarPaciente(
                    paciente.getIdPaciente(),
                    valor(dni),
                    valor(nombre),
                    valor(apellido),
                    valor(telefono),
                    valor(email)
            );
        }

        actualizar();
        aviso("Paciente guardado correctamente.");
    }

    // =====================================================
    // PROFESIONALES
    // =====================================================

    private JPanel panelProfesionales() {

        JPanel controles = barra();

        configurarCheck(profesionalesInactivos);

        accion(controles, "Registrar profesional",
                () -> formularioProfesional(null));

        accion(controles, "Editar",
                () -> formularioProfesional(profesionalElegido()));

        accion(controles, "Activar / desactivar", () -> {

            Profesional profesional = profesionalElegido();

            boolean activar =
                    profesionalesInactivos.isSelected();

            controlador.cambiarEstadoProfesional(
                    profesional.getIdProfesional(),
                    activar
            );

            actualizar();

            aviso(activar
                    ? "Profesional reactivado."
                    : "Profesional desactivado.");
        });

        accion(controles, "Nueva especialidad", () -> {

            String nombre = JOptionPane.showInputDialog(
                    this,
                    "Nombre de la especialidad:"
            );

            if (nombre == null) {
                return;
            }

            controlador.registrarEspecialidad(nombre);
            actualizar();
            aviso("Especialidad registrada.");
        });

        accion(controles, "Nuevo lugar", () -> {

            String nombre = JOptionPane.showInputDialog(
                    this,
                    "Nombre del hospital o centro de salud:"
            );

            if (nombre == null) {
                return;
            }

            String direccion = JOptionPane.showInputDialog(
                    this,
                    "Dirección:"
            );

            if (direccion == null) {
                return;
            }

            controlador.registrarLugar(nombre, direccion);

            actualizar();
            aviso("Lugar de atención registrado.");
        });

        controles.add(profesionalesInactivos);

        accion(controles, "Actualizar", this::actualizar);

        return panel(tablaProfesionales, controles);
    }

    private Profesional profesionalElegido() {

        int id = idSeleccionado(tablaProfesionales);

        return listaProfesionales.stream()
                .filter(p -> p.getIdProfesional() == id)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Profesional no encontrado."
                        ));
    }

    private void formularioProfesional(Profesional profesional)
            throws Exception {

        JTextField nombre = campo(18);
        JTextField apellido = campo(18);

        JComboBox<String> especialidad = combo();

        for (String item : controlador.listarEspecialidades()) {
            especialidad.addItem(item);
        }

        if (profesional != null) {

            nombre.setText(profesional.getNombre());
            apellido.setText(profesional.getApellido());

            especialidad.setSelectedItem(
                    profesional.getEspecialidad()
            );
        }

        JPanel formulario = formulario(
                new String[]{
                    "Nombre",
                    "Apellido",
                    "Especialidad"
                },
                nombre, apellido, especialidad
        );

        if (!aceptar(
                profesional == null
                        ? "Registrar profesional"
                        : "Editar profesional",
                formulario)) {
            return;
        }

        requerido(valor(nombre), valor(apellido));

        if (especialidad.getSelectedItem() == null) {
            throw new IllegalArgumentException(
                    "Seleccioná una especialidad."
            );
        }

        String seleccion =
                especialidad.getSelectedItem().toString();

        if (profesional == null) {

            controlador.registrarProfesional(
                    valor(nombre),
                    valor(apellido),
                    seleccion
            );

        } else {

            controlador.editarProfesional(
                    profesional.getIdProfesional(),
                    valor(nombre),
                    valor(apellido),
                    seleccion
            );
        }

        actualizar();
        aviso("Profesional guardado correctamente.");
    }

    // =====================================================
    // DISPONIBILIDAD
    // =====================================================

   
private JPanel panelDisponibilidad() {

    JPanel controles = barra();

    JLabel especialidad = new JLabel("Especialidad");
    JLabel profesional = new JLabel("Profesional");
    JLabel fecha = new JLabel("Fecha");

    especialidad.setForeground(AZUL);
    profesional.setForeground(AZUL);
    fecha.setForeground(AZUL);

    controles.add(especialidad);
    controles.add(filtroEspecialidad);

    controles.add(profesional);
    controles.add(filtroProfesional);

    filtroFecha.setToolTipText(
            "Fecha dd/MM/aaaa. Vacío = todas."
    );

    controles.add(fecha);
    controles.add(filtroFecha);

    accion(controles, "Filtrar",
            this::filtrarDisponibilidad);

    // Un solo botón para generar horarios automáticamente.
    accion(controles, "Generar turnos",
            this::generarTurnos);

    accion(controles, "Bloquear / desbloquear",
            this::bloquear);

    accion(controles, "Actualizar",
            this::actualizar);

    return panel(tablaDisponibilidad, controles);
}


    private void cargarFiltroProfesionales() {

        cargando = true;

        try {

            String especialidad =
                    (String) filtroEspecialidad.getSelectedItem();

            filtroProfesional.removeAllItems();
            filtroProfesional.addItem("Todos");

            for (Profesional profesional : profesionalesActivos) {

                if (especialidad == null
                        || especialidad.equals("Todas")
                        || profesional.getEspecialidad()
                                .equals(especialidad)) {

                    filtroProfesional.addItem(
                            etiqueta(profesional)
                    );
                }
            }

        } finally {
            cargando = false;
        }
    }


private void filtrarDisponibilidad() {

    ejecutar(() -> {

        String especialidad =
                (String) filtroEspecialidad.getSelectedItem();

        String profesional =
                (String) filtroProfesional.getSelectedItem();

        LocalDate fecha = valor(filtroFecha).isBlank()
                ? null
                : LocalDate.parse(valor(filtroFecha), FECHA);

        Integer idProfesional =
                profesional == null || profesional.equals("Todos")
                ? null
                : idDe(profesional);

        limpiar(modeloDisponibilidad);

        LocalDateTime ahora = LocalDateTime.now();

        for (Disponibilidad disponibilidad
                : controlador.filtrarDisponibilidades(
                        especialidad,
                        idProfesional,
                        fecha)) {

            LocalDateTime fechaHora =
                    LocalDateTime.of(
                            disponibilidad.getFecha(),
                            disponibilidad.getHorario()
                    );

            // No mostrar disponibilidades vencidas.
            if (!fechaHora.isAfter(ahora)) {
                continue;
            }

            modeloDisponibilidad.addRow(new Object[]{
                disponibilidad.getIdDisponibilidad(),
                disponibilidad.getFechaFormateada(),
                disponibilidad.getHoraFormateada(),
                disponibilidad.getProfesional()
                        .getNombreCompleto(),
                disponibilidad.getProfesional()
                        .getEspecialidad(),
                disponibilidad.getLugar(),
                disponibilidad.getEstado()
            });
        }
    });
}


    // Relaciona profesionales con su especialidad.

    private void llenarProfesionales(
            JComboBox<String> comboProfesionales,
            String especialidad) {

        comboProfesionales.removeAllItems();

        for (Profesional profesional : profesionalesActivos) {

            if (especialidad == null
                    || especialidad.equals("Todas")
                    || profesional.getEspecialidad()
                            .equals(especialidad)) {

                comboProfesionales.addItem(
                        etiqueta(profesional)
                );
            }
        }
    }

    private void crearHorario(boolean franja)
            throws Exception {

        JComboBox<String> especialidad = combo();
        JComboBox<String> profesional = combo();
        JComboBox<String> lugar = combo();

        for (String item : controlador.listarEspecialidades()) {
            especialidad.addItem(item);
        }

        for (String item : controlador.listarLugares()) {
            lugar.addItem(item);
        }

        especialidad.addActionListener(e ->
                llenarProfesionales(
                        profesional,
                        (String) especialidad.getSelectedItem()
                )
        );

        llenarProfesionales(
                profesional,
                (String) especialidad.getSelectedItem()
        );

        JTextField fecha = campo(15);
        JTextField inicio = campo(10);
        JTextField fin = campo(10);
        JTextField duracion = campo(10);

        fecha.setText(
                LocalDate.now().plusDays(1).format(FECHA)
        );

        inicio.setText("08:00");
        fin.setText("12:00");
        duracion.setText("30");

        JPanel formulario;

        if (franja) {

            formulario = formulario(
                    new String[]{
                        "Especialidad",
                        "Profesional",
                        "Lugar de atención",
                        "Fecha (dd/MM/aaaa)",
                        "Desde (HH:mm)",
                        "Hasta (HH:mm)",
                        "Minutos por turno"
                    },
                    especialidad,
                    profesional,
                    lugar,
                    fecha,
                    inicio,
                    fin,
                    duracion
            );

        } else {

            formulario = formulario(
                    new String[]{
                        "Especialidad",
                        "Profesional",
                        "Lugar de atención",
                        "Fecha (dd/MM/aaaa)",
                        "Hora (HH:mm)"
                    },
                    especialidad,
                    profesional,
                    lugar,
                    fecha,
                    inicio
            );
        }

        if (!aceptar(
                franja ? "Generar horarios" : "Crear horario",
                formulario)) {
            return;
        }

        if (profesional.getSelectedItem() == null
                || lugar.getSelectedItem() == null) {

            throw new SQLException(
                    "Seleccioná un profesional y un lugar."
            );
        }

        int idProfesional =
                idDe(profesional.getSelectedItem().toString());

        int idLugar =
                idDe(lugar.getSelectedItem().toString());

        LocalDate dia =
                LocalDate.parse(valor(fecha), FECHA);

        LocalTime desde =
                LocalTime.parse(valor(inicio));

        if (franja) {

            int cantidad =
                    controlador.generarDisponibilidades(
                            idProfesional,
                            idLugar,
                            dia,
                            desde,
                            LocalTime.parse(valor(fin)),
                            Integer.parseInt(valor(duracion))
                    );

            aviso("Se crearon " + cantidad + " horarios.");

        } else {

            controlador.crearDisponibilidad(
                    idProfesional,
                    idLugar,
                    dia,
                    desde
            );

            aviso("Horario creado correctamente.");
        }

        actualizar();
    }
    
private void generarTurnos() throws Exception {

    JComboBox<String> especialidad = combo();
    JComboBox<String> profesional = combo();
    JComboBox<String> lugar = combo();

    // Cargar especialidades.
    for (String item : controlador.listarEspecialidades()) {
        especialidad.addItem(item);
    }

    // Cargar lugares de atención.
    for (String item : controlador.listarLugares()) {
        lugar.addItem(item);
    }

    // Mostrar únicamente los profesionales
    // de la especialidad seleccionada.
    especialidad.addActionListener(e -> {

        llenarProfesionales(
                profesional,
                (String) especialidad.getSelectedItem()
        );
    });

    llenarProfesionales(
            profesional,
            (String) especialidad.getSelectedItem()
    );

    // Fecha.
    JTextField fecha = campo(15);

    fecha.setText(
            LocalDate.now().plusDays(1).format(FECHA)
    );

    // Horario de atención.
    JTextField horaInicio = campo(10);
    JTextField horaFin = campo(10);

    horaInicio.setText("08:00");
    horaFin.setText("12:00");

    // Intervalos disponibles.
    JComboBox<String> intervalo = combo();

    intervalo.addItem("Cada 15 minutos");
    intervalo.addItem("Cada 20 minutos");
    intervalo.addItem("Cada 30 minutos");
    intervalo.addItem("Cada 45 minutos");
    intervalo.addItem("Cada 60 minutos");

    intervalo.setSelectedItem("Cada 30 minutos");

    // Formulario.
    JPanel formulario = formulario(
            new String[]{
                "Especialidad",
                "Profesional",
                "Lugar de atención",
                "Fecha (dd/MM/aaaa)",
                "Hora de inicio (HH:mm)",
                "Hora de finalización (HH:mm)",
                "Intervalo entre turnos"
            },
            especialidad,
            profesional,
            lugar,
            fecha,
            horaInicio,
            horaFin,
            intervalo
    );

    if (!aceptar("Generar turnos", formulario)) {
        return;
    }

    // Validaciones.
    if (profesional.getSelectedItem() == null) {
        throw new IllegalArgumentException(
                "Seleccioná un profesional."
        );
    }

    if (lugar.getSelectedItem() == null) {
        throw new IllegalArgumentException(
                "Seleccioná un lugar de atención."
        );
    }

    int idProfesional = idDe(
            profesional.getSelectedItem().toString()
    );

    int idLugar = idDe(
            lugar.getSelectedItem().toString()
    );

    LocalDate dia = LocalDate.parse(
            valor(fecha),
            FECHA
    );

    LocalTime desde = LocalTime.parse(
            valor(horaInicio)
    );

    LocalTime hasta = LocalTime.parse(
            valor(horaFin)
    );

    if (dia.isBefore(LocalDate.now())) {
        throw new IllegalArgumentException(
                "No se pueden generar turnos en fechas anteriores."
        );
    }

    if (!hasta.isAfter(desde)) {
        throw new IllegalArgumentException(
                "La hora de finalización debe ser posterior "
                + "a la hora de inicio."
        );
    }

    if (dia.equals(LocalDate.now())
            && !desde.isAfter(LocalTime.now())) {

        throw new IllegalArgumentException(
                "La hora de inicio debe ser posterior "
                + "a la hora actual."
        );
    }

    // Convertir el intervalo elegido a minutos.
    String intervaloElegido =
            intervalo.getSelectedItem().toString();

    int minutos = Integer.parseInt(
            intervaloElegido.replaceAll("[^0-9]", "")
    );

    // Generar las disponibilidades.
    int cantidad = controlador.generarDisponibilidades(
            idProfesional,
            idLugar,
            dia,
            desde,
            hasta,
            minutos
    );

    actualizar();

    aviso(
            "Generación finalizada.\n\n"
            + "Fecha: " + dia.format(FECHA)
            + "\nProfesional: "
            + profesional.getSelectedItem()
            + "\nHorario: " + desde + " a " + hasta
            + "\nIntervalo: " + minutos + " minutos"
            + "\n\nHorarios generados: " + cantidad
    );
}


    private void bloquear() throws Exception {

        int id = idSeleccionado(tablaDisponibilidad);

        Disponibilidad disponibilidad =
                listaDisponibilidad.stream()
                        .filter(d ->
                                d.getIdDisponibilidad() == id)
                        .findFirst()
                        .orElseThrow();

        if ("LIBRE".equalsIgnoreCase(
                disponibilidad.getEstado())) {

            controlador.bloquearDisponibilidad(id);

        } else if ("BLOQUEADA".equalsIgnoreCase(
                disponibilidad.getEstado())) {

            controlador.desbloquearDisponibilidad(id);

        } else {

            throw new SQLException(
                    "No se puede bloquear un horario ocupado."
            );
        }

        actualizar();
    }

    // =====================================================
    // TURNOS
    // =====================================================

    private JPanel panelTurnos() {

        JPanel controles = barra();

        accion(controles, "Asignar turno",
                this::asignarTurno);

        accion(controles, "Cancelar turno",
                this::cancelarTurno);

        accion(controles, "Buscar por DNI", () -> {

            String dni = JOptionPane.showInputDialog(
                    this,
                    "Documento del paciente:"
            );

            if (dni == null) {
                return;
            }

            limpiar(modeloTurnos);

            for (Turnos turno
                    : controlador.buscarTurnosPorDocumento(
                            dni.trim())) {

                filaTurno(modeloTurnos, turno);
            }
        });

        accion(controles, "Exportar CSV",
                this::exportarCSV);

        accion(controles, "Imprimir / guardar PDF",
                this::imprimirAgenda);

        accion(controles, "Actualizar",
                this::actualizar);

        return panel(tablaTurnos, controles);
    }

    private void asignarTurno() throws Exception {

        String dni = JOptionPane.showInputDialog(
                this,
                "Ingresá el documento del paciente:"
        );

        if (dni == null) {
            return;
        }

        Paciente paciente =
                controlador.buscarPaciente(dni.trim());

        if (paciente == null) {

            throw new SQLException(
                    "Paciente no encontrado. Registralo primero."
            );
        }

        // Primero especialidad, después profesional.

        JComboBox<String> especialidad = combo();
        JComboBox<String> profesional = combo();

        especialidad.addItem("Todas");

        for (String item : controlador.listarEspecialidades()) {
            especialidad.addItem(item);
        }

        JTextField fecha = campo(15);

        fecha.setText(
                LocalDate.now().plusDays(1).format(FECHA)
        );

        JComboBox<Disponibilidad> horarios =
                new JComboBox<>();

        horarios.setFont(FUENTE);
        horarios.setBackground(Color.WHITE);
        horarios.setForeground(AZUL);

        horarios.setPreferredSize(
                new Dimension(490, 38)
        );

        horarios.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista,
                    Object valor,
                    int indice,
                    boolean seleccionado,
                    boolean foco) {

                JLabel etiqueta =
                        (JLabel) super.getListCellRendererComponent(
                                lista,
                                valor,
                                indice,
                                seleccionado,
                                foco
                        );

                etiqueta.setOpaque(true);
                etiqueta.setFont(FUENTE);
                etiqueta.setForeground(AZUL);

                etiqueta.setBackground(
                        seleccionado ? SELECCION : Color.WHITE
                );

                etiqueta.setBorder(
                        new EmptyBorder(7, 10, 7, 10)
                );

                if (valor instanceof Disponibilidad d) {

                    etiqueta.setText(
                            d.getHoraFormateada()
                            + " — "
                            + d.getLugar()
                    );
                }

                return etiqueta;
            }
        });

        JLabel mensaje = new JLabel(
                "Seleccioná especialidad, profesional y fecha."
        );

        mensaje.setForeground(TURQUESA);
        mensaje.setFont(FUENTE);

        Runnable cargarHorarios = () -> {

            horarios.removeAllItems();

            if (profesional.getSelectedItem() == null) {

                mensaje.setText(
                        "No hay profesionales para esta especialidad."
                );

                return;
            }

            try {

                LocalDate dia =
                        LocalDate.parse(valor(fecha), FECHA);

                int idProfesional =
                        idDe(profesional
                                .getSelectedItem()
                                .toString());

                String especialidadSeleccionada =
                        (String) especialidad.getSelectedItem();

                for (Disponibilidad disponibilidad
                        : listaDisponibilidad) {

                    boolean mismoProfesional =
                            disponibilidad.getProfesional()
                                    .getIdProfesional()
                            == idProfesional;

                    boolean mismaEspecialidad =
                            especialidadSeleccionada == null
                            || especialidadSeleccionada.equals("Todas")
                            || disponibilidad.getProfesional()
                                    .getEspecialidad()
                                    .equals(especialidadSeleccionada);

                    boolean mismaFecha =
                            disponibilidad.getFecha().equals(dia);

                    boolean libre =
                            "LIBRE".equalsIgnoreCase(
                                    disponibilidad.getEstado()
                            );

                    boolean futura =
                            LocalDateTime.of(
                                    disponibilidad.getFecha(),
                                    disponibilidad.getHorario()
                            ).isAfter(LocalDateTime.now());

                    if (mismoProfesional
                            && mismaEspecialidad
                            && mismaFecha
                            && libre
                            && futura) {

                        horarios.addItem(disponibilidad);
                    }
                }

                mensaje.setText(
                        horarios.getItemCount() == 0
                        ? "No hay horarios libres para esta selección."
                        : "Horarios disponibles: "
                                + horarios.getItemCount()
                );

            } catch (DateTimeParseException ex) {

                mensaje.setText(
                        "Ingresá una fecha válida (dd/MM/aaaa)."
                );
            }
        };

        // Cuando cambia la especialidad,
        // se actualizan los profesionales asociados.

        especialidad.addActionListener(e -> {

            llenarProfesionales(
                    profesional,
                    (String) especialidad.getSelectedItem()
            );

            cargarHorarios.run();
        });

        // Cuando cambia el profesional,
        // se actualizan los horarios disponibles.

        profesional.addActionListener(
                e -> cargarHorarios.run()
        );

        fecha.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        cargarHorarios.run();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        cargarHorarios.run();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        cargarHorarios.run();
                    }
                }
        );

        llenarProfesionales(
                profesional,
                (String) especialidad.getSelectedItem()
        );

        cargarHorarios.run();

        JLabel pacienteSeleccionado = new JLabel(
                paciente.getNombreCompleto()
                + " — "
                + paciente.getDocumento()
        );

        pacienteSeleccionado.setForeground(AZUL);
        pacienteSeleccionado.setFont(FUENTE_BOTON);

        JPanel formulario = formulario(
                new String[]{
                    "Paciente",
                    "Especialidad",
                    "Profesional",
                    "Fecha (dd/MM/aaaa)",
                    "Horario libre",
                    "Disponibilidad"
                },
                pacienteSeleccionado,
                especialidad,
                profesional,
                fecha,
                horarios,
                mensaje
        );

        if (!aceptar("Asignación de turno", formulario)) {
            return;
        }

        Disponibilidad seleccionada =
                (Disponibilidad) horarios.getSelectedItem();

        if (seleccionada == null) {

            aviso(
                    "No hay horarios libres para la selección."
            );

            return;
        }

        if (profesional.getSelectedItem() == null) {

            throw new SQLException(
                    "Seleccioná un profesional."
            );
        }

        int idProfesional =
                idDe(profesional.getSelectedItem().toString());

        if (seleccionada.getProfesional()
                .getIdProfesional() != idProfesional) {

            throw new SQLException(
                    "El horario no corresponde al profesional."
            );
        }

        String especialidadSeleccionada =
                (String) especialidad.getSelectedItem();

        if (!"Todas".equals(especialidadSeleccionada)
                && !seleccionada.getProfesional()
                        .getEspecialidad()
                        .equals(especialidadSeleccionada)) {

            throw new SQLException(
                    "La especialidad no corresponde al profesional."
            );
        }

        if (!"LIBRE".equalsIgnoreCase(
                seleccionada.getEstado())) {

            throw new SQLException(
                    "El horario no está disponible."
            );
        }

        if (!LocalDateTime.of(
                seleccionada.getFecha(),
                seleccionada.getHorario()
        ).isAfter(LocalDateTime.now())) {

            throw new SQLException(
                    "No se pueden asignar turnos vencidos."
            );
        }

        controlador.asignarTurno(
                paciente.getIdPaciente(),
                seleccionada.getIdDisponibilidad()
        );

        actualizar();
        mostrarPagina("Turnos");

        aviso("Turno asignado correctamente.");
    }

    private void cancelarTurno() throws Exception {

        int id = idSeleccionado(tablaTurnos);

        int confirmacion =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Cancelar el turno " + id + "?",
                        "Confirmar cancelación",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        controlador.cancelarTurno(id);

        actualizar();

        aviso(
                "Turno cancelado. Se conserva en el historial."
        );
    }

    // =====================================================
    // HISTORIAL
    // =====================================================

    private JPanel panelHistorial() {

        JPanel controles = barra();

        accion(controles, "Actualizar", this::actualizar);

        return panel(tablaHistorial, controles);
    }

    private void filaTurno(
            DefaultTableModel modelo,
            Turnos turno) {

        Disponibilidad disponibilidad =
                turno.getDisponibilidad();

        modelo.addRow(new Object[]{
            turno.getIdTurno(),
            turno.getPaciente().getDocumento(),
            turno.getPaciente().getNombreCompleto(),
            disponibilidad.getProfesional().getEspecialidad(),
            disponibilidad.getProfesional().getNombreCompleto(),
            disponibilidad.getFechaFormateada(),
            disponibilidad.getHoraFormateada(),
            disponibilidad.getLugar(),
            turno.getEstado()
        });
    }

    // =====================================================
    // EXPORTACIÓN CSV
    // =====================================================

    private void exportarCSV() throws Exception {

        JFileChooser selector = new JFileChooser();

        selector.setSelectedFile(
                new File("agenda_hospital.csv")
        );

        if (selector.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();

        if (!archivo.getName()
                .toLowerCase()
                .endsWith(".csv")) {

            archivo = new File(
                    archivo.getAbsolutePath() + ".csv"
            );
        }

        if (archivo.exists()) {

            int confirmar =
                    JOptionPane.showConfirmDialog(
                            this,
                            "¿Reemplazar el archivo existente?",
                            "Confirmar",
                            JOptionPane.YES_NO_OPTION
                    );

            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try (Writer escritor =
                     Files.newBufferedWriter(
                             archivo.toPath(),
                             StandardCharsets.UTF_8
                     )) {

            escritor.write('\ufeff');

            escritor.write(
                    "Turno;DNI;Paciente;Especialidad;"
                    + "Profesional;Fecha;Hora;Lugar;Estado\n"
            );

            for (Turnos turno
                    : controlador.listarTurnosVigentes()) {

                Disponibilidad disponibilidad =
                        turno.getDisponibilidad();

                String[] columnas = {
                    String.valueOf(turno.getIdTurno()),
                    turno.getPaciente().getDocumento(),
                    turno.getPaciente().getNombreCompleto(),
                    disponibilidad.getProfesional()
                            .getEspecialidad(),
                    disponibilidad.getProfesional()
                            .getNombreCompleto(),
                    disponibilidad.getFechaFormateada(),
                    disponibilidad.getHoraFormateada(),
                    disponibilidad.getLugar(),
                    turno.getEstado()
                };

                for (int i = 0; i < columnas.length; i++) {

                    if (i > 0) {
                        escritor.write(';');
                    }

                    escritor.write('"');

                    escritor.write(
                            columnas[i] == null
                            ? ""
                            : columnas[i].replace("\"", "\"\"")
                    );

                    escritor.write('"');
                }

                escritor.write('\n');
            }
        }

        aviso(
                "Archivo CSV guardado en:\n"
                + archivo.getAbsolutePath()
                + "\n\nContiene datos personales."
                + " Protegé el archivo."
        );
    }

    // =====================================================
    // IMPRESIÓN / PDF
    // =====================================================

    private void imprimirAgenda() throws Exception {

        boolean impreso = tablaTurnos.print(
                JTable.PrintMode.FIT_WIDTH,
                new MessageFormat(
                        "Agenda de turnos hospitalarios"
                ),
                new MessageFormat("Página {0}")
        );

        if (!impreso) {

            aviso(
                    "Se canceló la impresión.\n"
                    + "Para guardar un PDF, seleccioná "
                    + "una impresora PDF en el diálogo."
            );
        }
    }

    // =====================================================
    // ACTUALIZACIÓN GENERAL
    // =====================================================

    private void actualizar() {

        ejecutar(() -> {

            listaPacientes =
                    controlador.listarPacientesPorEstado(
                            !pacientesInactivos.isSelected()
                    );

            listaProfesionales =
                    controlador.listarProfesionalesPorEstado(
                            !profesionalesInactivos.isSelected()
                    );

            profesionalesActivos =
                    controlador.listarProfesionales();

            listaDisponibilidad =
                    controlador.listarDisponibilidades();

            listaTurnos =
                    controlador.listarTurnosVigentes();

            // Pacientes

            limpiar(modeloPacientes);

            for (Paciente paciente : listaPacientes) {

                modeloPacientes.addRow(new Object[]{
                    paciente.getIdPaciente(),
                    paciente.getDocumento(),
                    paciente.getNombre(),
                    paciente.getApellido(),
                    paciente.getTelefono(),
                    paciente.getEmail(),
                    pacientesInactivos.isSelected()
                            ? "INACTIVO"
                            : "ACTIVO"
                });
            }

            // Profesionales

            limpiar(modeloProfesionales);

            for (Profesional profesional : listaProfesionales) {

                modeloProfesionales.addRow(new Object[]{
                    profesional.getIdProfesional(),
                    profesional.getNombre(),
                    profesional.getApellido(),
                    profesional.getEspecialidad(),
                    profesionalesInactivos.isSelected()
                            ? "INACTIVO"
                            : "ACTIVO"
                });
            }

            // Especialidades

            cargando = true;

            try {

                String seleccionAnterior =
                        (String) filtroEspecialidad.getSelectedItem();

                filtroEspecialidad.removeAllItems();
                filtroEspecialidad.addItem("Todas");

                for (String especialidad
                        : controlador.listarEspecialidades()) {

                    filtroEspecialidad.addItem(especialidad);
                }

                if (seleccionAnterior != null) {

                    for (int i = 0;
                            i < filtroEspecialidad.getItemCount();
                            i++) {

                        if (seleccionAnterior.equals(
                                filtroEspecialidad.getItemAt(i))) {

                            filtroEspecialidad.setSelectedIndex(i);
                            break;
                        }
                    }
                }

            } finally {
                cargando = false;
            }

            cargarFiltroProfesionales();
            filtrarDisponibilidad();

            // Turnos vigentes

            limpiar(modeloTurnos);

            for (Turnos turno : listaTurnos) {
                filaTurno(modeloTurnos, turno);
            }

            // Historial

            limpiar(modeloHistorial);

            for (Turnos turno : controlador.listarHistorial()) {
                filaTurno(modeloHistorial, turno);
            }
        });
    }
}
