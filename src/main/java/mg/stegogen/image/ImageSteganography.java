package mg.stegogen.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
    private byte[] readFile(String filePath) throws IOException {
        File inputFile = new File(filePath);
        byte[] fileData = new byte[(int) inputFile.length()];

        try (FileInputStream fis = new FileInputStream(inputFile)) {
            fis.read(fileData);
        }

        return fileData;
    }

}
