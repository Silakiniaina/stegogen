package mg.stegogen;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import mg.stegogen.gui.SteganographyGUI;

/**
 * Hello world!
 *
 */
public class Steganography {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new SteganographyGUI().setVisible(true));
    }
}
