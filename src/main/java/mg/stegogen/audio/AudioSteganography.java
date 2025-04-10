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

}

