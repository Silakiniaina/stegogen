package mg.stegogen.core;

import java.util.logging.Logger;

public class RandomGenerator {
    private static final Logger logger = Logger.getLogger(RandomGenerator.class.getName());
    private long seed;
    private long multiplier;
    private long increment;
    private long modulus;
    private long currentValue;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */
    public RandomGenerator(long seed) {
        logger.info("Initializing RandomGenerator with seed: " + seed);
        this.setMultiplier(25214903917L);
        this.setIncrement(11L);
        this.setModulus((1L << 48));
        this.setSeed(seed);
        this.setCurrentValue(seed);
        logger.info("RandomGenerator initialized successfully");
    }

    /* -------------------------------------------------------------------------- */
    /*                                  Functions                                 */
    /* -------------------------------------------------------------------------- */
    public long nextLong() {
        logger.fine("Generating next long value");
        this.setCurrentValue((this.getMultiplier() * this.getCurrentValue() + this.getIncrement()) % this.getModulus()); 
        long value = this.getCurrentValue();
        logger.fine("Generated long value: " + value);
        return value;
    }

    public int nextInt(int bound) {
        logger.info("Generating next int with bound: " + bound);
        if (bound <= 0) {
            logger.severe("Invalid bound: " + bound + ", must be positive");
            throw new IllegalArgumentException("Bound must be positive");
        }

        long result = nextLong();
        int bits = (int) (result & Integer.MAX_VALUE);
        int value = bits % bound;

        while (bits - value + (bound - 1) < 0) {
            logger.fine("Retrying due to bias in random number generation");
            result = nextLong();
            bits = (int) (result & Integer.MAX_VALUE);
            value = bits % bound;
        }

        logger.fine("Generated int value: " + value);
        return value;
    }

    public int[] generateUniquePositions(int count, int bound) {
        logger.info("Generating " + count + " unique positions with bound: " + bound);
        if (count > bound) {
            logger.severe("Count " + count + " exceeds bound " + bound);
            throw new IllegalArgumentException("Cannot generate more unique positions than the bound");
        }

        int[] positions = new int[count];
        boolean[] used = new boolean[bound];
        int generated = 0;

        while (generated < count) {
            int pos = nextInt(bound);
            if (!used[pos]) {
                positions[generated++] = pos;
                used[pos] = true;
                logger.fine("Selected position: " + pos);
            } else {
                logger.fine("Position " + pos + " already used, retrying");
            }
        }

        logger.info("Generated " + count + " unique positions successfully");
        return positions;
    }

    public void reset() {
        logger.info("Resetting RandomGenerator to seed: " + seed);
        currentValue = seed;
        logger.info("Reset completed");
    }
    
    /* -------------------------------------------------------------------------- */
    /*                                   Getters                                  */
    /* -------------------------------------------------------------------------- */
    public long getSeed() {
        logger.fine("Getting seed: " + seed);
        return seed;
    }
    
    public long getMultiplier() {
        logger.fine("Getting multiplier: " + multiplier);
        return multiplier;
    }
    
    public long getIncrement() {
        logger.fine("Getting increment: " + increment);
        return increment;
    }
    
    public long getModulus() {
        logger.fine("Getting modulus: " + modulus);
        return modulus;
    }
    
    public long getCurrentValue() {
        logger.fine("Getting current value: " + currentValue);
        return currentValue;
    }
    
    /* -------------------------------------------------------------------------- */
    /*                                   Setters                                  */
    /* -------------------------------------------------------------------------- */
    public void setSeed(long newSeed) {
        logger.info("Setting seed to: " + newSeed);
        seed = newSeed;
        currentValue = seed;
        logger.info("Seed set successfully");
    }
    
    public void setMultiplier(long multiplier) {
        logger.info("Setting multiplier to: " + multiplier);
        this.multiplier = multiplier;
        logger.info("Multiplier set successfully");
    }
    
    public void setIncrement(long increment) {
        logger.info("Setting increment to: " + increment);
        this.increment = increment;
        logger.info("Increment set successfully");
    }
    
    public void setModulus(long modulus) {
        logger.info("Setting modulus to: " + modulus);
        this.modulus = modulus;
        logger.info("Modulus set successfully");
    }
    
    public void setCurrentValue(long currentValue) {
        logger.info("Setting current value to: " + currentValue);
        this.currentValue = currentValue;
        logger.info("Current value set successfully");
    }
}