
package com.gorriti.tp3;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public final class TemaVisual {

    private static final Color AZUL = new Color(25, 75, 125);
    private static final Color VERDE_AGUA = new Color(21, 145, 148);
    private static final Color FONDO = new Color(245, 249, 252);
    private static final Color BLANCO = Color.WHITE;
    private static final Color TEXTO = new Color(40, 57, 72);
    private static final Color BORDE = new Color(218, 229, 237);

    private TemaVisual() {
    }

    public static void aplicar(Container contenedor) {
        for (Component componente : contenedor.getComponents()) {

            if (componente instanceof JPanel panel) {
                panel.setBackground(FONDO);
            }

            if (componente instanceof JLabel etiqueta) {
                etiqueta.setForeground(TEXTO);
                etiqueta.setFont(
                        new Font("Segoe UI", Font.PLAIN, 14));
            }

            if (componente instanceof JTextField campo) {
                campo.setBackground(BLANCO);
                campo.setForeground(TEXTO);
                campo.setFont(
                        new Font("Segoe UI", Font.PLAIN, 14));
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE),
                        new EmptyBorder(7, 10, 7, 10)));
            }

            if (componente instanceof JComboBox<?> combo) {
                combo.setBackground(BLANCO);
                combo.setForeground(TEXTO);
                combo.setFont(
                        new Font("Segoe UI", Font.PLAIN, 13));
            }

            if (componente instanceof AbstractButton boton) {
                boton.setBackground(VERDE_AGUA);
                boton.setForeground(BLANCO);
                boton.setFont(
                        new Font("Segoe UI", Font.BOLD, 13));
                boton.setFocusPainted(false);
                boton.setOpaque(true);
                boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(VERDE_AGUA),
                        new EmptyBorder(9, 14, 9, 14)));
            }

            if (componente instanceof JTable tabla) {
                tabla.setBackground(BLANCO);
                tabla.setForeground(TEXTO);
                tabla.setFont(
                        new Font("Segoe UI", Font.PLAIN, 13));
                tabla.setRowHeight(34);
                tabla.setShowVerticalLines(false);
                tabla.setGridColor(BORDE);
                tabla.setSelectionBackground(
                        new Color(210, 239, 240));
                tabla.setSelectionForeground(TEXTO);
                tabla.setFillsViewportHeight(true);

                tabla.getTableHeader().setBackground(AZUL);
                tabla.getTableHeader().setForeground(BLANCO);
                tabla.getTableHeader().setFont(
                        new Font("Segoe UI", Font.BOLD, 13));
                tabla.getTableHeader().setOpaque(true);
                tabla.getTableHeader().setPreferredSize(
                        new java.awt.Dimension(0, 38));
            }

            if (componente instanceof JScrollPane scroll) {
                scroll.setBorder(
                        BorderFactory.createLineBorder(BORDE));
                scroll.getViewport().setBackground(BLANCO);
            }

            if (componente instanceof JTabbedPane pestanas) {
                pestanas.setBackground(FONDO);
                pestanas.setForeground(AZUL);
                pestanas.setFont(
                        new Font("Segoe UI", Font.BOLD, 14));
            }

            if (componente instanceof JComponent componenteSwing) {
                componenteSwing.setDoubleBuffered(true);
            }

            if (componente instanceof Container hijo) {
                aplicar(hijo);
            }
        }
    }
}
