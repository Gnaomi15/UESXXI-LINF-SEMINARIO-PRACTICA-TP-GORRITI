
package com.gorriti.tp3;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TP3 {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Se conserva el estilo predeterminado.
            }

            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
