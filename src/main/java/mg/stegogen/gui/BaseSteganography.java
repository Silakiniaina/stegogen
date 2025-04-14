package mg.stegogen.gui;

import java.io.IOException;

import mg.stegogen.core.RandomGenerator;

public abstract class BaseSteganography {
    
    private RandomGenerator randomGenerator;
    
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

    /* -------------------------------------------------------------------------- */
    /*                                  Functions                                 */
    /* -------------------------------------------------------------------------- */
    public RandomGenerator getRandomGenerator() {
        return randomGenerator;
    }

    public void setRandomGenerator(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }
}