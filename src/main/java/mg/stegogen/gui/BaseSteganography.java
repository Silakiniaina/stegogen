package mg.stegogen.gui;

import java.io.IOException;
import java.lang.reflect.Constructor;

import mg.stegogen.core.RandomGenerator;

public abstract class BaseSteganography {
    protected final RandomGenerator randomGenerator;

    /* -------------------------------------------------------------------------- */
    /*                                 Constructor                                */
    /* -------------------------------------------------------------------------- */
    public BaseSteganography(long seed) {
        this.randomGenerator = new RandomGenerator(seed);
    }
    
    /* -------------------------------------------------------------------------- */
    /*                              Abstract Methods                              */
    /* -------------------------------------------------------------------------- */
    public abstract void embedMessage(String inputPath, String outputPath, String message, int numPositions)
            throws IOException;

    public abstract String extractMessage(String inputPath, int numPositions) throws IOException;
}