package mg.stegogen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import mg.stegogen.audio.AudioSteganography;
import mg.stegogen.core.RandomGenerator;
import mg.stegogen.gui.config.MediaType;
import mg.stegogen.gui.config.OperationType;
import mg.stegogen.image.ImageSteganography;
import mg.stegogen.utils.SteganographyUtils;

public class SteganographyGUI extends JFrame {
    private JTextField inputFileField, outputFileField, messageField, positionsField;
    private JComboBox<OperationType> operationCombo;
    private JComboBox<MediaType> mediaTypeCombo;
    private JButton browseInputButton, browseOutputButton, executeButton;
    private JTextArea resultArea;

    // Random Position Generator fields
    private JTextField seedField, countField, boundField;
    private JButton generateButton;
    private JTextArea positionsResultArea;

    /* -------------------------------------------------------------------------- */
    /*                                 Constructor                                */
    /* -------------------------------------------------------------------------- */
    public SteganographyGUI() {
        setupFrame();
        setupTabbedPane();
        setupEventListeners();
    }

    /* -------------------------------------------------------------------------- */
    /*                                  Functions                                 */
    /* -------------------------------------------------------------------------- */
    private void setupFrame() {
        setTitle("Steganography Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 500);
    }

    private void setupTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        JPanel steganographyPanel = createSteganographyPanel();
        JPanel randomGeneratorPanel = createRandomGeneratorPanel();
        
        tabbedPane.addTab("Steganography", steganographyPanel);
        tabbedPane.addTab("Random Position Generator", randomGeneratorPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSteganographyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel inputPanel = createSteganographyInputPanel();
        panel.add(inputPanel, BorderLayout.NORTH);
        
        resultArea = new JTextArea(8, 30);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Result"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createSteganographyInputPanel() {
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
        
        // Message input (only for embedding)
        addComponent(panel, new JLabel("Message:"), gbc, 0, 4);
        messageField = new JTextField(20);
        addComponent(panel, messageField, gbc, 1, 4);
        
        // Positions input
        addComponent(panel, new JLabel("Positions:"), gbc, 0, 5);
        positionsField = new JTextField(20);
        addComponent(panel, positionsField, gbc, 1, 5);
        
        // Execute button
        executeButton = new JButton("Execute");
        addComponent(panel, executeButton, gbc, 1, 6);
        
        return panel;
    }

    private JPanel createRandomGeneratorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel inputPanel = createRandomGeneratorInputPanel();
        panel.add(inputPanel, BorderLayout.NORTH);
        
        positionsResultArea = new JTextArea(12, 30);
        positionsResultArea.setEditable(true);
        positionsResultArea.setLineWrap(true);
        positionsResultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(positionsResultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Generated Positions"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createRandomGeneratorInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();
        
        // Seed input
        addComponent(panel, new JLabel("Seed:"), gbc, 0, 0);
        seedField = new JTextField("1234", 20);
        addComponent(panel, seedField, gbc, 1, 0);
        
        // Count input
        addComponent(panel, new JLabel("Count:"), gbc, 0, 1);
        countField = new JTextField("10", 20);
        addComponent(panel, countField, gbc, 1, 1);
        
        // Bound input
        addComponent(panel, new JLabel("Bound:"), gbc, 0, 2);
        boundField = new JTextField("1000", 20);
        addComponent(panel, boundField, gbc, 1, 2);
        
        // Generate button
        generateButton = new JButton("Generate Positions");
        addComponent(panel, generateButton, gbc, 1, 3);
        
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
        
        // Update UI elements based on operation type
        outputFileField.setEnabled(isEmbed);
        browseOutputButton.setEnabled(isEmbed);
        messageField.setEnabled(isEmbed);
    }

    private void validateInputs() {
        // Validate positions input
        if (positionsField.getText().isEmpty()) {
            throw new IllegalArgumentException("Positions field is required");
        }
        
        try {
            parsePositions(positionsField.getText());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid positions format. Use comma-separated numbers (e.g., 1,54,65,3)");
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
    
    private void validateRandomGeneratorInputs() {
        try {
            Long.parseLong(seedField.getText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Seed must be a valid number");
        }
        
        try {
            int count = Integer.parseInt(countField.getText());
            if (count <= 0) {
                throw new IllegalArgumentException("Count must be a positive number");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Count must be a valid number");
        }
        
        try {
            int bound = Integer.parseInt(boundField.getText());
            if (bound <= 0) {
                throw new IllegalArgumentException("Bound must be a positive number");
            }
            
            int count = Integer.parseInt(countField.getText());
            if (count > bound) {
                throw new IllegalArgumentException("Count cannot be greater than bound");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bound must be a valid number");
        }
    }

    private BaseSteganography createSteganographyInstance(MediaType mediaType) {
        // Create stego instance using a default seed since we're using custom positions
        if (mediaType == MediaType.IMAGE) {
            return new ImageSteganography(1);  // Using default seed of 1
        } else {
            return new AudioSteganography(1);  // Using default seed of 1
        }
    }

    private int[] parsePositions(String positionsText) {
        String[] positionStrings = positionsText.split(",");
        int[] positions = new int[positionStrings.length];
        
        for (int i = 0; i < positionStrings.length; i++) {
            positions[i] = Integer.parseInt(positionStrings[i].trim());
        }
        
        return positions;
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
        resultArea.setText("Error: " + message);
    }
    
    private void setupEventListeners() {
        browseInputButton.addActionListener(e -> browseFile(inputFileField, getFileExtensionFilter()));
        browseOutputButton.addActionListener(e -> browseFile(outputFileField, getFileExtensionFilter()));
        executeButton.addActionListener(this::executeOperation);
        operationCombo.addActionListener(e -> updateUIState());
        mediaTypeCombo.addActionListener(e -> updateFileFieldsBasedOnMediaType());
        generateButton.addActionListener(this::generateRandomPositions);
    }

    private void executeOperation(ActionEvent e) {
        clearResult();
        
        try {
            // Validate inputs
            validateInputs();
            
            OperationType operation = (OperationType) operationCombo.getSelectedItem();
            MediaType mediaType = (MediaType) mediaTypeCombo.getSelectedItem();
            String inputPath = inputFileField.getText();
            int[] positions = parsePositions(positionsField.getText());
            
            if (operation == OperationType.EMBED) {
                String message = messageField.getText();
                embedMessage(mediaType, inputPath, outputFileField.getText(), message, positions);
            } else {
                extractMessage(mediaType, inputPath, positions);
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
            ex.printStackTrace(); // For debugging purposes
        }
    }

    private void generateRandomPositions(ActionEvent e) {
        try {
            // Validate inputs
            validateRandomGeneratorInputs();
            
            long seed = Long.parseLong(seedField.getText());
            int count = Integer.parseInt(countField.getText());
            int bound = Integer.parseInt(boundField.getText());
            
            RandomGenerator generator = new RandomGenerator(seed);
            int[] positions = generator.generateUniquePositions(count, bound);
            
            // Convert positions to comma-separated string
            String positionsString = Arrays.stream(positions)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining(", "));
            
            positionsResultArea.setText(positionsString);
        } catch (Exception ex) {
            positionsResultArea.setText("Error: " + ex.getMessage());
            ex.printStackTrace(); // For debugging purposes
        }
    }

    private void embedMessage(MediaType mediaType, String inputPath, String outputPath, 
        String message, int[] positions) throws IOException {
        BaseSteganography stego = createSteganographyInstance(mediaType);
        
        // Calculate how many positions are needed
        String binaryMessage = SteganographyUtils.isBinary(message) ? 
                         message : SteganographyUtils.textToBinary(message);
        int requiredPositions = binaryMessage.length() + SteganographyUtils.END_MARKER.length();
        
        // Check if we have enough positions
        if (positions.length < requiredPositions) {
            throw new IllegalArgumentException("Need at least " + requiredPositions + " positions, but only " + 
                                             positions.length + " were provided");
        }
        
        // Override the default random positions with our custom positions
        stego.getRandomGenerator().setCustomPositions(positions);
        
        // Use the positions length as numPositions parameter
        stego.embedMessage(inputPath, outputPath, message, positions.length);

        showSuccess("Message embedded successfully in " + mediaType.toString().toLowerCase() + 
                   ". Used " + requiredPositions + " positions out of " + positions.length + " provided.");
    }

    private void extractMessage(MediaType mediaType, String inputPath, int[] positions) throws IOException {
        BaseSteganography stego = createSteganographyInstance(mediaType);
        
        // Override the default random positions with our custom positions
        stego.getRandomGenerator().setCustomPositions(positions);
        
        String binaryMessage = stego.extractMessage(inputPath, positions.length);
        showResult("Extracted message: " + binaryMessage);
    }
}