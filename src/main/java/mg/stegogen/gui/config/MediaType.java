package mg.stegogen.gui.config;

public enum MediaType {
    IMAGE("Image"), AUDIO("Audio");

    private final String displayName;

    MediaType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
