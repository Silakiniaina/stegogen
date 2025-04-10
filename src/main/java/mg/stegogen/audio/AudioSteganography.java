package mg.stegogen.audio;

import mg.stegogen.core.RandomGenerator;
import mg.stegogen.utils.SteganographyUtils;

public class AudioSteganography {
    private static final int BITS_PER_BYTE = 8;

    private RandomGenerator randomGenerator;

    /* -------------------------------------------------------------------------- */
    /*                                 Constructor                                */
    /* -------------------------------------------------------------------------- */
    public AudioSteganography(long seed) {
        this.randomGenerator = new RandomGenerator(seed);
    }

    /* -------------------------------------------------------------------------- */
    /*                                  Functions                                 */
    /* -------------------------------------------------------------------------- */
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
}

