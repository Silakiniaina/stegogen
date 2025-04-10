package mg.stegogen.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
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

    public void embedMessage(String inputImagePath, String outputImagePath, String message, int numPositions)
            throws IOException {
        byte[] pngData = SteganographyUtils.readFile(inputImagePath);
        validatePngSignature(pngData);

        PngMetadata metadata = extractPngMetadata(pngData);
        int width = metadata.width;
        int height = metadata.height;

        byte[] decompressedData = extractAndDecompressIdatData(pngData);

        String binaryMessage = SteganographyUtils.textToBinary(message) + SteganographyUtils.END_MARKER;
        validateMessageSize(binaryMessage, numPositions, width * height);

        int bytesPerPixel = 4;
        int scanlineLength = width * bytesPerPixel + 1;

        int[] positions = generateRandomPositions(numPositions, width * height);
        embedBinaryMessage(decompressedData, binaryMessage, positions, width, scanlineLength, bytesPerPixel);

        byte[] recompressedData = recompressData(decompressedData);
        createOutputPng(pngData, recompressedData, outputImagePath);
    }

    public String extractMessage(String stegoImagePath, int numPositions) throws IOException {
        byte[] pngData = SteganographyUtils.readFile(stegoImagePath);
        validatePngSignature(pngData);

        PngMetadata metadata = extractPngMetadata(pngData);
        int width = metadata.width;
        int height = metadata.height;

        byte[] decompressedData = extractAndDecompressIdatData(pngData);

        validatePositionsCount(numPositions, width * height);

        int bytesPerPixel = 4;
        int scanlineLength = width * bytesPerPixel + 1;

        int[] positions = generateRandomPositions(numPositions, width * height);
        String extractedMessage = extractBinaryMessage(decompressedData, positions, width, scanlineLength,
                bytesPerPixel);

        return extractedMessage;
    }

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

    private void embedBinaryMessage(byte[] decompressedData, String binaryMessage, int[] positions, int width,
            int scanlineLength, int bytesPerPixel) {
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

    private byte[] recompressData(byte[] decompressedData) throws IOException {
        ByteArrayOutputStream recompressedStream = new ByteArrayOutputStream();

        try (DeflaterOutputStream deflaterStream = new DeflaterOutputStream(
                recompressedStream, new Deflater(Deflater.DEFAULT_COMPRESSION))) {
            deflaterStream.write(decompressedData);
        }

        return recompressedStream.toByteArray();
    }

    private void createOutputPng(byte[] originalPngData, byte[] recompressedData, String outputPath)
            throws IOException {
        ByteArrayOutputStream newPngStream = new ByteArrayOutputStream();
        newPngStream.write(PNG_SIGNATURE);

        int offset = 8;
        boolean idatWritten = false;

        while (offset < originalPngData.length - 12) {
            int chunkLength = SteganographyUtils.readInt(originalPngData, offset);
            String chunkType = new String(originalPngData, offset + 4, 4);

            if (chunkType.equals("IDAT")) {
                offset += 4 + 4 + chunkLength + 4;
                if (!idatWritten) {
                    writeChunk(newPngStream, "IDAT", recompressedData);
                    idatWritten = true;
                }
            } else {
                newPngStream.write(originalPngData, offset, 4 + 4 + chunkLength + 4);
                offset += 4 + 4 + chunkLength + 4;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(newPngStream.toByteArray());
        }
    }

    private void writeChunk(ByteArrayOutputStream stream, String chunkType, byte[] data) throws IOException {
        // Write chunk length (4 bytes)
        byte[] lengthBytes = new byte[4];
        SteganographyUtils.writeInt(lengthBytes, 0, data.length);
        stream.write(lengthBytes);

        // Write chunk type (4 bytes)
        stream.write(chunkType.getBytes());

        // Write chunk data
        stream.write(data);

        // Calculate and write CRC (4 bytes)
        int crc = calculateCrc(chunkType.getBytes(), data);
        byte[] crcBytes = new byte[4];
        SteganographyUtils.writeInt(crcBytes, 0, crc);
        stream.write(crcBytes);
    }

    private int calculateCrc(byte[] typeBytes, byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(typeBytes);
        crc.update(data);
        return (int) crc.getValue();
    }

    private String extractBinaryMessage(byte[] decompressedData, int[] positions, int width,
            int scanlineLength, int bytesPerPixel) throws IOException {
        StringBuilder binaryCode = new StringBuilder();

        for (int i = 0; i < positions.length; i++) {
            int pixelIndex = positions[i];
            int row = pixelIndex / width;
            int col = pixelIndex % width;
            int dataIndex = row * scanlineLength + 1 + col * bytesPerPixel;

            if (dataIndex < decompressedData.length) {
                int extractedBit = extractLSB(decompressedData[dataIndex]);
                binaryCode.append(extractedBit);

                if (isEndMarkerFound(binaryCode)) {
                    return trimEndMarker(binaryCode);
                }
            }
        }

        throw new IOException("No hidden code found or code is corrupted");
    }

    private int extractLSB(byte value) {
        return (value & 0xFF) & 0x01;
    }

    private boolean isEndMarkerFound(StringBuilder binaryCode) {
        int codeLength = binaryCode.length();
        int markerLength = SteganographyUtils.END_MARKER.length();

        return codeLength >= markerLength &&
                binaryCode.substring(codeLength - markerLength).equals(SteganographyUtils.END_MARKER);
    }

    private String trimEndMarker(StringBuilder binaryCode) {
        int endMarkerLength = SteganographyUtils.END_MARKER.length();
        return binaryCode.substring(0, binaryCode.length() - endMarkerLength);
    }

    private void validatePositionsCount(int numPositions, int totalPixels) {
        if (numPositions > totalPixels) {
            throw new IllegalArgumentException("Requested positions exceed available pixels");
        }
    }
}
