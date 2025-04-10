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

}

