package mg.stegogen.audio;

import mg.stegogen.core.RandomGenerator;

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

}

