package mg.stegogen.gui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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

}