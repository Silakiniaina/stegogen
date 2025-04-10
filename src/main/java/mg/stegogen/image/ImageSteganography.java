package mg.stegogen.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import mg.stegogen.core.RandomGenerator;
import mg.stegogen.utils.SteganographyUtils;

public class ImageSteganography {

    private static final byte[] PNG_SIGNATURE = { (byte) 0x89, 'P', 'N', 'G', '\r', '\n', '\u001a', '\n' };
    private RandomGenerator randomGenerator;

    /* -------------------------------------------------------------------------- */
    /* Constructor */
    /* -------------------------------------------------------------------------- */
    public ImageSteganography(long seed) {
        this.randomGenerator = new RandomGenerator(seed);
    }

    /* -------------------------------------------------------------------------- */
    /* Functions */
    /* -------------------------------------------------------------------------- */
    private void validatePngSignature(byte[] pngData) {
        if (pngData.length < 8 || !Arrays.equals(Arrays.copyOfRange(pngData, 0, 8), PNG_SIGNATURE)) {
            throw new IllegalArgumentException("Not a valid PNG file");
        }
    }

    private PngMetadata extractPngMetadata(byte[] pngData) {
        int width = 0;
        int height = 0;
        int offset = 8;

        while (offset < pngData.length - 12) {
            int chunkLength = SteganographyUtils.readInt(pngData, offset);
            offset += 4;

            String chunkType = new String(pngData, offset, 4);
            offset += 4;

            if (chunkType.equals("IHDR")) {
                width = SteganographyUtils.readInt(pngData, offset);
                height = SteganographyUtils.readInt(pngData, offset + 4);
                break;
            }

            offset += chunkLength + 4;
        }

        if (width == 0 || height == 0) {
            throw new IllegalArgumentException("Could not determine PNG dimensions");
        }

        return new PngMetadata(width, height);
    }

    private byte[] extractAndDecompressIdatData(byte[] pngData) throws IOException {
        ByteArrayOutputStream idatData = collectIdatChunks(pngData);
        return decompressData(idatData.toByteArray());
    }

    private ByteArrayOutputStream collectIdatChunks(byte[] pngData) {
        ByteArrayOutputStream idatData = new ByteArrayOutputStream();
        int offset = 8;

        while (offset < pngData.length - 12) {
            int chunkLength = SteganographyUtils.readInt(pngData, offset);
            offset += 4;

            String chunkType = new String(pngData, offset, 4);
            offset += 4;

            if (chunkType.equals("IDAT")) {
                idatData.write(pngData, offset, chunkLength);
            }

            offset += chunkLength + 4;
        }

        return idatData;
    }

    private byte[] decompressData(byte[] compressedData) throws IOException {
        ByteArrayOutputStream decompressedStream = new ByteArrayOutputStream();
        
        try (InflaterInputStream inflaterStream = new InflaterInputStream(
                new ByteArrayInputStream(compressedData), new Inflater())) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inflaterStream.read(buffer)) != -1) {
                decompressedStream.write(buffer, 0, read);
            }
        }
        
        return decompressedStream.toByteArray();
    }

    private void validateMessageSize(String binaryMessage, int numPositions, int totalPixels) {
        if (binaryMessage.length() > numPositions) {
            throw new IllegalArgumentException("Message is too large for the specified number of positions");
        }
        if (numPositions > totalPixels) {
            throw new IllegalArgumentException("Requested positions exceed available pixels");
        }
    }

    private int[] generateRandomPositions(int numPositions, int totalPixels) {
        randomGenerator.reset();
        return randomGenerator.generateUniquePositions(numPositions, totalPixels);
    }

    private void embedBinaryMessage(byte[] decompressedData, String binaryMessage, int[] positions, int width, int scanlineLength, int bytesPerPixel) {
        for (int i = 0; i < binaryMessage.length(); i++) {
            int pixelIndex = positions[i];
            int row = pixelIndex / width;
            int col = pixelIndex % width;
            int dataIndex = row * scanlineLength + 1 + col * bytesPerPixel;

            if (dataIndex < decompressedData.length) {
                int value = decompressedData[dataIndex] & 0xFF;
                int bitToEmbed = binaryMessage.charAt(i) == '1' ? 1 : 0;
                value = (value & 0xFE) | bitToEmbed;
                decompressedData[dataIndex] = (byte) value;
            }
        }
    }
}
