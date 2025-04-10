package mg.stegogen.audio;

import java.io.IOException;

import mg.stegogen.core.RandomGenerator;
import mg.stegogen.utils.SteganographyUtils;

public class AudioSteganography {
    private static final int BITS_PER_BYTE = 8;

    private RandomGenerator randomGenerator;

    /* -------------------------------------------------------------------------- */
    /* Constructor */
    /* -------------------------------------------------------------------------- */
    public AudioSteganography(long seed) {
        this.randomGenerator = new RandomGenerator(seed);
    }

    /* -------------------------------------------------------------------------- */
    /* Functions */
    /* -------------------------------------------------------------------------- */

    public void embedMessage(String inputAudioPath, String outputAudioPath, String message, int numPositions)
            throws IOException {
        byte[] audioData = SteganographyUtils.readFile(inputAudioPath);
        validateWavFormat(audioData);

        WavMetadata metadata = extractWavMetadata(audioData);
        int dataOffset = findDataChunk(audioData);

        int numSamples = calculateNumSamples(audioData, dataOffset, metadata.bytesPerSample, metadata.numChannels);
        String binaryMessage = prepareBinaryMessage(message);

        validateMessageAndPositions(binaryMessage, numPositions, numSamples);

        int[] positions = generateRandomPositions(numPositions, numSamples);
        embedBinaryMessage(audioData, binaryMessage, positions, dataOffset, metadata);

        SteganographyUtils.writeFile(outputAudioPath, audioData);
    }

    private void validateWavFormat(byte[] audioData) {
        if (audioData.length < 44 ||
                audioData[0] != 'R' || audioData[1] != 'I' || audioData[2] != 'F' || audioData[3] != 'F' ||
                audioData[8] != 'W' || audioData[9] != 'A' || audioData[10] != 'V' || audioData[11] != 'E') {
            throw new IllegalArgumentException("Not a valid WAV file");
        }
    }

    private WavMetadata extractWavMetadata(byte[] audioData) {
        int numChannels = SteganographyUtils.byteArrayToShort(audioData, 22);
        int bitsPerSample = SteganographyUtils.byteArrayToShort(audioData, 34);

        if (bitsPerSample != 8 && bitsPerSample != 16) {
            throw new IllegalArgumentException("Only 8-bit or 16-bit WAV files are supported");
        }

        return new WavMetadata(numChannels, bitsPerSample);
    }

    private int findDataChunk(byte[] audioData) {
        for (int i = 12; i < audioData.length - 8; i++) {
            if (audioData[i] == 'd' && audioData[i + 1] == 'a' && audioData[i + 2] == 't' && audioData[i + 3] == 'a') {
                return i + 8; // Skip "data" + 4 bytes for chunk size
            }
        }

        throw new IllegalArgumentException("Could not find data chunk in WAV file");
    }

    private int calculateNumSamples(byte[] audioData, int dataOffset, int bytesPerSample, int numChannels) {
        return (audioData.length - dataOffset) / (bytesPerSample * numChannels);
    }

    private String prepareBinaryMessage(String message) {
        return SteganographyUtils.textToBinary(message) + SteganographyUtils.END_MARKER;
    }

    private void validateMessageAndPositions(String binaryMessage, int numPositions, int numSamples) {
        if (binaryMessage.length() > numPositions) {
            throw new IllegalArgumentException("Message is too large for the specified number of positions");
        }

        if (numPositions > numSamples) {
            throw new IllegalArgumentException("Requested positions exceed available samples");
        }
    }

    private int[] generateRandomPositions(int numPositions, int numSamples) {
        randomGenerator.reset();
        return randomGenerator.generateUniquePositions(numPositions, numSamples);
    }

    private void embedBinaryMessage(byte[] audioData, String binaryMessage, int[] positions, int dataOffset,
            WavMetadata metadata) {
        for (int i = 0; i < binaryMessage.length(); i++) {
            int sampleIndex = positions[i];
            int sampleOffset = calculateSampleOffset(dataOffset, sampleIndex, metadata.bytesPerSample,
                    metadata.numChannels);

            int bitToEmbed = binaryMessage.charAt(i) == '1' ? 1 : 0;

            if (metadata.bitsPerSample == 8) {
                embed8BitSample(audioData, sampleOffset, bitToEmbed);
            } else {
                embed16BitSample(audioData, sampleOffset, bitToEmbed);
            }
        }
    }

    private int calculateSampleOffset(int dataOffset, int sampleIndex, int bytesPerSample, int numChannels) {
        return dataOffset + sampleIndex * bytesPerSample * numChannels;
    }

    private void embed8BitSample(byte[] audioData, int sampleOffset, int bitToEmbed) {
        int sample = audioData[sampleOffset] & 0xFF;
        sample = (sample & 0xFE) | bitToEmbed;
        audioData[sampleOffset] = (byte) sample;
    }

    private void embed16BitSample(byte[] audioData, int sampleOffset, int bitToEmbed) {
        if (audioData.length > sampleOffset + 1) {
            int sample = SteganographyUtils.byteArrayToShort(audioData, sampleOffset);
            sample = (sample & 0xFFFE) | bitToEmbed;
            audioData[sampleOffset] = (byte) (sample & 0xFF);
            audioData[sampleOffset + 1] = (byte) ((sample >> 8) & 0xFF);
        }
    }

    private int extract16BitSample(byte[] audioData, int sampleOffset) {
        if (audioData.length > sampleOffset + 1) {
            int sample = SteganographyUtils.byteArrayToShort(audioData, sampleOffset);
            return sample & 0x01;
        }
        return 0; // Default return if sample can't be read
    }
}
