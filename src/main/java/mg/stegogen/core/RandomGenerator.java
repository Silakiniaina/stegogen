package mg.stegogen.core;

public class RandomGenerator {
    private long seed;
    private long multiplier;
    private long increment;
    private long modulus;
    private long currentValue;
    
    /* -------------------------------------------------------------------------- */
    /*                                   Getters                                  */
    /* -------------------------------------------------------------------------- */
    public long getSeed() {
        return seed;
    }
    public long getMultiplier() {
        return multiplier;
    }
    public long getIncrement() {
        return increment;
    }
    public long getModulus() {
        return modulus;
    }
    public long getCurrentValue() {
        return currentValue;
    }
    
    /* -------------------------------------------------------------------------- */
    /*                                   Setters                                  */
    /* -------------------------------------------------------------------------- */
    public void setSeed(long seed) {
        this.seed = seed;
    }
    public void setMultiplier(long multiplier) {
        this.multiplier = multiplier;
    }
    public void setIncrement(long increment) {
        this.increment = increment;
    }
    public void setModulus(long modulus) {
        this.modulus = modulus;
    }
    public void setCurrentValue(long currentValue) {
        this.currentValue = currentValue;
    }
    
}
