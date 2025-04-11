package mg.stegogen.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SteganographyUtils {
    public static final int BITS_PER_BYTE = 8;
    public static final String END_MARKER = "1111111111111111";

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
        StringBuilder binaryBuilder = new StringBuilder();
        for (char c : text.toCharArray()) {
            String binary = Integer.toBinaryString(c);
            while (binary.length() < BITS_PER_BYTE) {
                binary = "0" + binary;
            }
            binaryBuilder.append(binary);
        }
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

}
