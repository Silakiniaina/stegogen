package mg.stegogen.image;

import mg.stegogen.core.RandomGenerator;

public class ImageSteganography {

    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', '\u001a', '\n'};
    private RandomGenerator randomGenerator;

    /* -------------------------------------------------------------------------- */
    /*                                 Constructor                                */
    /* -------------------------------------------------------------------------- */
    public ImageSteganography(long seed) {
        this.randomGenerator = new RandomGenerator(seed);
    }

}

