package mg.stegogen.gui.config;

public enum OperationType {
    EMBED("Embed"), EXTRACT("Extract");

    private final String displayName;

    OperationType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
