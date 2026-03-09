package net.owen.shinytracker;

public enum KeybindAction {
    INCREMENT_COUNTER("Increment Counter"),
    DECREMENT_COUNTER("Decrement Counter"),
    OPEN_HUNT_TAB("Open Hunt Tab"),
    OPEN_POKEMON_TAB("Open Pokémon Tab"),
    OPEN_CATCH_TAB("Open Catch Tab");

    private final String displayName;

    KeybindAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}