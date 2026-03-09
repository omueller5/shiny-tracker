package net.owen.shinytracker;

public class GameInfo {

    private final String id;
    private final String displayName;
    private final int shinyOddsDenominator;
    private final boolean shinyCharmSupported;

    public GameInfo(String id, String displayName, int shinyOddsDenominator, boolean shinyCharmSupported) {
        this.id = id;
        this.displayName = displayName;
        this.shinyOddsDenominator = shinyOddsDenominator;
        this.shinyCharmSupported = shinyCharmSupported;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getShinyOddsDenominator() {
        return shinyOddsDenominator;
    }

    public boolean isShinyCharmSupported() {
        return shinyCharmSupported;
    }

    @Override
    public String toString() {
        return displayName;
    }
}