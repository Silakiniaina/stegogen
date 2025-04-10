package mg.stegogen.utils;

public class SteganographyUtils {
    public static final int BITS_PER_BYTE = 8;
    public static final String END_MARKER = "1111111111111111";

    /* -------------------------------------------------------------------------- */
    /*                                  Functions                                 */
    /* -------------------------------------------------------------------------- */
    public static int readInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 24) |
                ((data[offset + 1] & 0xFF) << 16) |
                ((data[offset + 2] & 0xFF) << 8) |
                (data[offset + 3] & 0xFF);
    }
}
