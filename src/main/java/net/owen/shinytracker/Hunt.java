package net.owen.shinytracker;

public class Hunt {

    private String pokemonName;
    private int dexNumber;
    private String game;
    private String method;
    private int resetCount;
    private boolean completed;
    private String startTime;
    private String completedTime;
    private boolean shinyCharmEnabled;

    private boolean hyperspaceHunt;
    private String doughnutLevel;
    private String resetPreset;

    private int totalEncounters;
    private java.util.List<HuntPhase> phases = new java.util.ArrayList<>();

    private long lastUpdated;

    public int getCurrentPhaseNumber() {
        return getPhases().size() + 1;
    }

    public Hunt() {
        this.lastUpdated = System.currentTimeMillis();
    }

    public Hunt(String pokemonName,
                int dexNumber,
                String game,
                String method,
                int resetCount,
                boolean completed,
                String startTime,
                String completedTime,
                boolean shinyCharmEnabled,
                boolean hyperspaceHunt,
                String doughnutLevel,
                String resetPreset) {
        this.pokemonName = pokemonName;
        this.dexNumber = dexNumber;
        this.game = game;
        this.method = method;
        this.resetCount = resetCount;
        this.completed = completed;
        this.startTime = startTime;
        this.completedTime = completedTime;
        this.shinyCharmEnabled = shinyCharmEnabled;
        this.hyperspaceHunt = hyperspaceHunt;
        this.doughnutLevel = doughnutLevel;
        this.resetPreset = resetPreset;
        this.lastUpdated = System.currentTimeMillis();
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public void setPokemonName(String pokemonName) {
        this.pokemonName = pokemonName;
    }

    public int getDexNumber() {
        return dexNumber;
    }

    public void setDexNumber(int dexNumber) {
        this.dexNumber = dexNumber;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getResetCount() {
        return resetCount;
    }

    public void setResetCount(int resetCount) {
        this.resetCount = Math.max(0, resetCount);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(String completedTime) {
        this.completedTime = completedTime;
    }

    public boolean isShinyCharmEnabled() {
        return shinyCharmEnabled;
    }

    public void setShinyCharmEnabled(boolean shinyCharmEnabled) {
        this.shinyCharmEnabled = shinyCharmEnabled;
    }

    public boolean isHyperspaceHunt() {
        return hyperspaceHunt;
    }

    public void setHyperspaceHunt(boolean hyperspaceHunt) {
        this.hyperspaceHunt = hyperspaceHunt;
    }

    public String getDoughnutLevel() {
        return doughnutLevel;
    }

    public void setDoughnutLevel(String doughnutLevel) {
        this.doughnutLevel = doughnutLevel;
    }

    public String getResetPreset() {
        return resetPreset;
    }

    public void setResetPreset(String resetPreset) {
        this.resetPreset = resetPreset;
    }

    public int getTotalEncounters() {
        return totalEncounters;
    }

    public void setTotalEncounters(int totalEncounters) {
        this.totalEncounters = Math.max(0, totalEncounters);
    }

    public java.util.List<HuntPhase> getPhases() {
        if (phases == null) {
            phases = new java.util.ArrayList<>();
        }
        return phases;
    }

    public void setPhases(java.util.List<HuntPhase> phases) {
        this.phases = (phases == null) ? new java.util.ArrayList<>() : phases;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void touch() {
        this.lastUpdated = System.currentTimeMillis();
    }

    public int getPhaseCount() {
        return getPhases().size();
    }

    public boolean hasCompletedPhase() {
        for (HuntPhase phase : getPhases()) {
            if (phase.isTargetPhase()) {
                return true;
            }
        }
        return false;
    }

    public String getFileSafeName() {
        String base = (pokemonName == null ? "hunt" : pokemonName)
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");

        String gamePart = (game == null ? "game" : game)
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");

        return base + "_" + gamePart + ".json";
    }
}