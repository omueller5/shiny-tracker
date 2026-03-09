package net.owen.shinytracker;

public class PokemonEntry {

    private final int dexNumber;
    private final String displayName;

    public PokemonEntry(int dexNumber, String displayName) {
        this.dexNumber = dexNumber;
        this.displayName = displayName;
    }

    public int getDexNumber() {
        return dexNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFormattedDisplayName() {
        return displayName + " (#" + formatDexNumber(dexNumber) + ")";
    }

    private String formatDexNumber(int dexNumber) {
        if (dexNumber < 10) {
            return "00" + dexNumber;
        }
        if (dexNumber < 100) {
            return "0" + dexNumber;
        }
        return String.valueOf(dexNumber);
    }

    @Override
    public String toString() {
        return getFormattedDisplayName();
    }
}