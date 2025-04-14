package mg.stegogen.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class SteganographyUtils {
    private static Logger logger = Logger.getLogger(SteganographyUtils.class.getName());
    public static final int BITS_PER_BYTE = 8;
    // END_MARKER removed - we no longer need this

    /* -------------------------------------------------------------------------- */
    /*                                  Functions                                 */
    /* -------------------------------------------------------------------------- */
    public static int readInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 24) |
                ((data[offset + 1] & 0xFF) << 16) |
                ((data[offset + 2] & 0xFF) << 8) |
                (data[offset + 3] & 0xFF);
    }

    public static byte[] readFile(String filePath) throws IOException {
        logger.info("Reading file: " + filePath);
        File inputFile = new File(filePath);
        byte[] fileData = new byte[(int) inputFile.length()];

        try (FileInputStream fis = new FileInputStream(inputFile)) {
            fis.read(fileData);
        }

        return fileData;
    }

    public static void writeInt(byte[] bytes, int offset, int value) {
        bytes[offset] = (byte) ((value >>> 24) & 0xFF);
        bytes[offset + 1] = (byte) ((value >>> 16) & 0xFF);
        bytes[offset + 2] = (byte) ((value >>> 8) & 0xFF);
        bytes[offset + 3] = (byte) (value & 0xFF);
    }

    public static String textToBinary(String text) {
        logger.info("Processing text: " + text);
        if (isBinary(text)) {
            logger.info("Text is already binary, returning unchanged: " + text);
            return text;
        }
        logger.info("Converting text to binary");
        StringBuilder binaryBuilder = new StringBuilder();
        for (char c : text.toCharArray()) {
            String binary = Integer.toBinaryString(c);
            while (binary.length() < BITS_PER_BYTE) {
                binary = "0" + binary;
            }
            binaryBuilder.append(binary);
        }
        logger.info("Successfully converted text to binary: " + binaryBuilder.toString());
        return binaryBuilder.toString();
    }

    public static short byteArrayToShort(byte[] data, int offset) {
        return (short) ((data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8));
    }

    public static void writeFile(String filePath, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
    }

    public static String binaryToText(String binary) {
        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i < binary.length(); i += BITS_PER_BYTE) {
            if (i + BITS_PER_BYTE <= binary.length()) {
                String byteStr = binary.substring(i, i + BITS_PER_BYTE);
                int charCode = Integer.parseInt(byteStr, 2);
                textBuilder.append((char) charCode);
            }
        }
        return textBuilder.toString();
    }

    public static boolean isBinary(String text) {
        logger.info("Checking if text is binary: " + text);
        if (text == null || text.isEmpty()) {
            logger.info("Text is null or empty, returning false");
            return false;
        }
        boolean isBinary = text.matches("^[01]+$");
        logger.info("Text is binary: " + isBinary);
        return isBinary;
    }
}