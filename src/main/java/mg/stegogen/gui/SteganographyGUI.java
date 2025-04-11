package mg.stegogen.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import mg.stegogen.gui.config.MediaType;
import mg.stegogen.gui.config.OperationType;

public class SteganographyGUI extends JFrame {
    private JTextField inputFileField, outputFileField, messageField, seedField, positionsField;
    private JComboBox<OperationType> operationCombo;
    private JComboBox<MediaType> mediaTypeCombo;
    private JButton browseInputButton, browseOutputButton, executeButton;
    private JTextArea resultArea;

    /* -------------------------------------------------------------------------- */
    /*                                  Functions                                 */
    /* -------------------------------------------------------------------------- */
    private void setupFrame() {
        setTitle("Steganography Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 450);
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }
}