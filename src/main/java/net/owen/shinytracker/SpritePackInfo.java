package net.owen.shinytracker;

public class SpritePackInfo {

    private final String id;
    private final String displayName;
    private final String basePath;

    public SpritePackInfo(String id, String displayName, String basePath) {
        this.id = id;
        this.displayName = displayName;
        this.basePath = basePath;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBasePath() {
        return basePath;
    }

    @Override
    public String toString() {
        return displayName;
    }
}