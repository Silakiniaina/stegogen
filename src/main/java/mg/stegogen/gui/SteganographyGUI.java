package mg.stegogen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import mg.stegogen.audio.AudioSteganography;
import mg.stegogen.gui.config.MediaType;
import mg.stegogen.gui.config.OperationType;
import mg.stegogen.image.ImageSteganography;

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

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();
        
        // Operation selection
        addComponent(panel, new JLabel("Operation:"), gbc, 0, 0);
        operationCombo = new JComboBox<>(OperationType.values());
        addComponent(panel, operationCombo, gbc, 1, 0);
        
        // Media type selection
        addComponent(panel, new JLabel("Media Type:"), gbc, 0, 1);
        mediaTypeCombo = new JComboBox<>(MediaType.values());
        addComponent(panel, mediaTypeCombo, gbc, 1, 1);
        
        // Input file selection
        addComponent(panel, new JLabel("Input File:"), gbc, 0, 2);
        inputFileField = new JTextField(20);
        addComponent(panel, inputFileField, gbc, 1, 2);
        browseInputButton = new JButton("Browse");
        addComponent(panel, browseInputButton, gbc, 2, 2);
        
        // Output file selection
        addComponent(panel, new JLabel("Output File:"), gbc, 0, 3);
        outputFileField = new JTextField(20);
        addComponent(panel, outputFileField, gbc, 1, 3);
        browseOutputButton = new JButton("Browse");
        addComponent(panel, browseOutputButton, gbc, 2, 3);
        
        // Message input
        addComponent(panel, new JLabel("Message:"), gbc, 0, 4);
        messageField = new JTextField(20);
        addComponent(panel, messageField, gbc, 1, 4);
        
        // Seed input
        addComponent(panel, new JLabel("Seed:"), gbc, 0, 5);
        seedField = new JTextField("1234"); // Default seed
        addComponent(panel, seedField, gbc, 1, 5);
        
        // Positions input
        addComponent(panel, new JLabel("Positions:"), gbc, 0, 6);
        positionsField = new JTextField("1000"); // Default positions
        addComponent(panel, positionsField, gbc, 1, 6);
        
        // Execute button
        executeButton = new JButton("Execute");
        addComponent(panel, executeButton, gbc, 1, 7);
        
        return panel;
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addComponent(JPanel panel, Component component, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(component, gbc);
    }

    private void setupResultArea() {
        resultArea = new JTextArea(8, 30);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Result"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private FileFilter getFileExtensionFilter() {
        MediaType mediaType = (MediaType) mediaTypeCombo.getSelectedItem();
        if (mediaType == MediaType.IMAGE) {
            return new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png");
        } else {
            return new javax.swing.filechooser.FileNameExtensionFilter("WAV Audio", "wav");
        }
    }

    private void updateFileFieldsBasedOnMediaType() {
        // Clear file fields when media type changes
        inputFileField.setText("");
        outputFileField.setText("");
    }

    private void browseFile(JTextField field, FileFilter filter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(filter);
        
        int result;
        if (field == outputFileField) {
            result = fileChooser.showSaveDialog(this);
        } else {
            result = fileChooser.showOpenDialog(this);
        }
        
        if (result == JFileChooser.APPROVE_OPTION) {
            field.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void updateUIState() {
        boolean isEmbed = operationCombo.getSelectedItem() == OperationType.EMBED;
        outputFileField.setEnabled(isEmbed);
        browseOutputButton.setEnabled(isEmbed);
        messageField.setEnabled(isEmbed);
    }

    private void validateInputs() {
        // Validate seed
        try {
            Long.parseLong(seedField.getText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Seed must be a valid number");
        }
        
        // Validate positions
        try {
            int positions = Integer.parseInt(positionsField.getText());
            if (positions <= 0) {
                throw new IllegalArgumentException("Positions must be a positive number");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Positions must be a valid number");
        }
        
        // Validate files
        if (inputFileField.getText().isEmpty()) {
            throw new IllegalArgumentException("Input file is required");
        }
        
        if (operationCombo.getSelectedItem() == OperationType.EMBED) {
            if (outputFileField.getText().isEmpty()) {
                throw new IllegalArgumentException("Output file is required for embedding");
            }
            
            if (messageField.getText().isEmpty()) {
                throw new IllegalArgumentException("Message is required for embedding");
            }
        }
    }

    private BaseSteganography createSteganographyInstance(MediaType mediaType, long seed) {
        if (mediaType == MediaType.IMAGE) {
            return new ImageSteganography(seed);
        } else {
            return new AudioSteganography(seed);
        }
    }

    private void clearResult() {
        resultArea.setText("");
    }
    
    private void showResult(String message) {
        resultArea.setText(message);
    }
    
    private void showSuccess(String message) {
        resultArea.setText("Success: " + message);
    }
    
    private void showError(String message) {
        resultArea.setText(message);
    }
    

    private void embedMessage(MediaType mediaType, String inputPath, String outputPath, 
        String message, long seed, int numPositions) throws IOException {
        BaseSteganography stego = createSteganographyInstance(mediaType, seed);
        stego.embedMessage(inputPath, outputPath, message, numPositions);

        showSuccess("Message embedded successfully in " + mediaType.toString().toLowerCase() + ".");
    }
}