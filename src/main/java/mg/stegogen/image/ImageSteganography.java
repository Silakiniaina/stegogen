package mg.stegogen.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import mg.stegogen.gui.BaseSteganography;
import mg.stegogen.utils.SteganographyUtils;

public class ImageSteganography extends BaseSteganography {

    private static Logger logger = Logger.getLogger(ImageSteganography.class.getName());
    private static final byte[] PNG_SIGNATURE = { (byte) 0x89, 'P', 'N', 'G', '\r', '\n', '\u001a', '\n' };

    /* -------------------------------------------------------------------------- */
    /*                                 Constructor                                */
    /* -------------------------------------------------------------------------- */
    public ImageSteganography(long seed) {
        super(seed);
        logger.info("Initialized ImageSteganography with seed: " + seed);
    }

    /* -------------------------------------------------------------------------- */
    /*                                  Functions                                 */
    /* -------------------------------------------------------------------------- */
    @Override
    public void embedMessage(String inputImagePath, String outputImagePath, String message, int numPositions)
            throws IOException {
        logger.info("Starting embedMessage with input: " + inputImagePath + ", output: " + outputImagePath + 
                   ", message length: " + message.length() + ", positions: " + numPositions);
        byte[] pngData = SteganographyUtils.readFile(inputImagePath);
        validatePngSignature(pngData);

        PngMetadata metadata = extractPngMetadata(pngData);
        int width = metadata.width;
        int height = metadata.height;
        logger.info("Image dimensions: " + width + "x" + height);

        byte[] decompressedData = extractAndDecompressIdatData(pngData);

        // No longer append END_MARKER to the binary message
        String binaryMessage = SteganographyUtils.isBinary(message) ? message : SteganographyUtils.textToBinary(message);
        
        logger.info("Binary message length: " + binaryMessage.length());
        if (numPositions < binaryMessage.length()) {
            logger.severe("numPositions (" + numPositions + ") is less than binary message length (" + binaryMessage.length() + ")");
            throw new IllegalArgumentException("numPositions must be at least " + binaryMessage.length());
        }
        validateMessageSize(binaryMessage, numPositions, width * height);

        int bytesPerPixel = 4;
        int scanlineLength = width * bytesPerPixel + 1;

        logger.info("Generating random positions");
        int[] positions = generateRandomPositions(numPositions, width * height);
        embedBinaryMessage(decompressedData, binaryMessage, positions, width, scanlineLength, bytesPerPixel);

        logger.info("Recompressing data");
        byte[] recompressedData = recompressData(decompressedData);
        createOutputPng(pngData, recompressedData, outputImagePath);
        logger.info("embedMessage completed successfully");
    }

    @Override
    public String extractMessage(String stegoImagePath, int numPositions) throws IOException {
        logger.info("Starting extractMessage from: " + stegoImagePath + ", positions: " + numPositions);
        byte[] pngData = SteganographyUtils.readFile(stegoImagePath);
        validatePngSignature(pngData);

        PngMetadata metadata = extractPngMetadata(pngData);
        int width = metadata.width;
        int height = metadata.height;
        logger.info("Image dimensions: " + width + "x" + height);

        byte[] decompressedData = extractAndDecompressIdatData(pngData);

        validatePositionsCount(numPositions, width * height);

        int bytesPerPixel = 4;
        int scanlineLength = width * bytesPerPixel + 1;

        logger.info("Generating random positions");
        int[] positions = generateRandomPositions(numPositions, width * height);
        String extractedMessage = extractBinaryMessage(decompressedData, positions, width, scanlineLength,
                bytesPerPixel);
        logger.info("Extracted message length: " + extractedMessage.length());
        return extractedMessage;
    }

    private void validatePngSignature(byte[] pngData) {
        logger.info("Validating PNG signature");
        if (pngData.length < 8 || !Arrays.equals(Arrays.copyOfRange(pngData, 0, 8), PNG_SIGNATURE)) {
            logger.severe("Invalid PNG signature");
            throw new IllegalArgumentException("Not a valid PNG file");
        }
        logger.info("PNG signature validated successfully");
    }

    private PngMetadata extractPngMetadata(byte[] pngData) {
        logger.info("Extracting PNG metadata");
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
                logger.info("Found IHDR chunk - width: " + width + ", height: " + height);
                break;
            }

            offset += chunkLength + 4;
        }

        if (width == 0 || height == 0) {
            logger.severe("Failed to determine PNG dimensions");
            throw new IllegalArgumentException("Could not determine PNG dimensions");
        }

        return new PngMetadata(width, height);
    }

    private byte[] extractAndDecompressIdatData(byte[] pngData) throws IOException {
        logger.info("Extracting and decompressing IDAT data");
        ByteArrayOutputStream idatData = collectIdatChunks(pngData);
        byte[] decompressedData = decompressData(idatData.toByteArray());
        logger.info("IDAT data extracted and decompressed successfully");
        return decompressedData;
    }

    private ByteArrayOutputStream collectIdatChunks(byte[] pngData) {
        logger.info("Collecting IDAT chunks");
        ByteArrayOutputStream idatData = new ByteArrayOutputStream();
        int offset = 8;

        while (offset < pngData.length - 12) {
            int chunkLength = SteganographyUtils.readInt(pngData, offset);
            offset += 4;

            String chunkType = new String(pngData, offset, 4);
            offset += 4;

            if (chunkType.equals("IDAT")) {
                idatData.write(pngData, offset, chunkLength);
                logger.info("Collected IDAT chunk of length: " + chunkLength);
            }

            offset += chunkLength + 4;
        }

        logger.info("IDAT chunks collection completed");
        return idatData;
    }

    private byte[] decompressData(byte[] compressedData) throws IOException {
        logger.info("Decompressing data");
        ByteArrayOutputStream decompressedStream = new ByteArrayOutputStream();

        try (InflaterInputStream inflaterStream = new InflaterInputStream(
                new ByteArrayInputStream(compressedData), new Inflater())) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inflaterStream.read(buffer)) != -1) {
                decompressedStream.write(buffer, 0, read);
            }
        }

        logger.info("Data decompression completed");
        return decompressedStream.toByteArray();
    }

    private void validateMessageSize(String binaryMessage, int numPositions, int totalPixels) {
        logger.info("Validating message size - binary length: " + binaryMessage.length() +
                ", positions: " + numPositions + ", total pixels: " + totalPixels);
        if (binaryMessage.length() > numPositions) {
            logger.severe("Message too large for specified positions");
            throw new IllegalArgumentException("Message is too large for the specified number of positions");
        }
        if (numPositions > totalPixels) {
            logger.severe("Requested positions exceed available pixels");
            throw new IllegalArgumentException("Requested positions exceed available pixels");
        }
        logger.info("Message size validated successfully");
    }

    private int[] generateRandomPositions(int numPositions, int totalPixels) {
        logger.info("Generating " + numPositions + " random positions from " + totalPixels + " pixels");
        this.getRandomGenerator().reset();
        int[] positions = this.getRandomGenerator().generateUniquePositions(numPositions, totalPixels);
        logger.info("Random positions generated successfully");
        return positions;
    }

    private void embedBinaryMessage(byte[] decompressedData, String binaryMessage, int[] positions, int width,
            int scanlineLength, int bytesPerPixel) {
        logger.info("Embedding binary message of length: " + binaryMessage.length());
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
        logger.info("Binary message embedded successfully");
    }

    private byte[] recompressData(byte[] decompressedData) throws IOException {
        logger.info("Recompressing data");
        ByteArrayOutputStream recompressedStream = new ByteArrayOutputStream();

        try (DeflaterOutputStream deflaterStream = new DeflaterOutputStream(
                recompressedStream, new Deflater(Deflater.DEFAULT_COMPRESSION))) {
            deflaterStream.write(decompressedData);
        }

        logger.info("Data recompression completed");
        return recompressedStream.toByteArray();
    }

    private void createOutputPng(byte[] originalPngData, byte[] recompressedData, String outputPath)
            throws IOException {
        logger.info("Creating output PNG: " + outputPath);
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
                    logger.info("Wrote new IDAT chunk");
                }
            } else {
                newPngStream.write(originalPngData, offset, 4 + 4 + chunkLength + 4);
                offset += 4 + 4 + chunkLength + 4;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(newPngStream.toByteArray());
        }
        logger.info("Output PNG created successfully");
    }

    private void writeChunk(ByteArrayOutputStream stream, String chunkType, byte[] data) throws IOException {
        logger.info("Writing chunk: " + chunkType);
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
        logger.info("Chunk " + chunkType + " written successfully");
    }

    private int calculateCrc(byte[] typeBytes, byte[] data) {
        logger.info("Calculating CRC");
        CRC32 crc = new CRC32();
        crc.update(typeBytes);
        crc.update(data);
        int crcValue = (int) crc.getValue();
        logger.info("CRC calculated: " + crcValue);
        return crcValue;
    }

    private String extractBinaryMessage(byte[] decompressedData, int[] positions, int width,
            int scanlineLength, int bytesPerPixel) throws IOException {
        logger.info("Extracting binary message");
        StringBuilder binaryCode = new StringBuilder();

        // Extract all bits from the specified positions
        for (int i = 0; i < positions.length; i++) {
            int pixelIndex = positions[i];
            int row = pixelIndex / width;
            int col = pixelIndex % width;
            int dataIndex = row * scanlineLength + 1 + col * bytesPerPixel;

            if (dataIndex < decompressedData.length) {
                int extractedBit = extractLSB(decompressedData[dataIndex]);
                binaryCode.append(extractedBit);
            } else {
                logger.warning("Data index " + dataIndex + " out of bounds for decompressed data length " + decompressedData.length);
            }
        }

        // Return all extracted bits, no need to check for END_MARKER
        String result = binaryCode.toString();
        logger.info("Binary message extracted successfully, length: " + result.length());
        return result;
    }

    private int extractLSB(byte value) {
        logger.fine("Extracting LSB from byte");
        int lsb = (value & 0xFF) & 0x01;
        logger.fine("LSB extracted: " + lsb);
        return lsb;
    }

    private void validatePositionsCount(int numPositions, int totalPixels) {
        logger.info("Validating positions count: " + numPositions + " against " + totalPixels);
        if (numPositions > totalPixels) {
            logger.severe("Requested positions exceed available pixels");
            throw new IllegalArgumentException("Requested positions exceed available pixels");
        }
        logger.info("Positions count validated successfully");
    }
}