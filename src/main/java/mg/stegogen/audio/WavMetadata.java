package mg.stegogen.audio;

public class WavMetadata {
    int numChannels;
    int bitsPerSample;
    int bytesPerSample;
    
    public WavMetadata(int numChannels, int bitsPerSample) {
        this.numChannels = numChannels;
        this.bitsPerSample = bitsPerSample;
        this.bytesPerSample = bitsPerSample / 8;
    }
}
