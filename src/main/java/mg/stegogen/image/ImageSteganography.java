package mg.stegogen.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import mg.stegogen.core.RandomGenerator;

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
}
