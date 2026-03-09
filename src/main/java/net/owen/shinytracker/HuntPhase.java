package net.owen.shinytracker;

public class HuntPhase {

    private int phaseNumber;
    private int encounters;
    private String shinyPokemonName;
    private int shinyDexNumber;
    private boolean targetPhase;
    private String timestamp;

    public HuntPhase() {
    }

    public HuntPhase(int phaseNumber, int encounters, String shinyPokemonName, int shinyDexNumber, boolean targetPhase, String timestamp) {
        this.phaseNumber = phaseNumber;
        this.encounters = encounters;
        this.shinyPokemonName = shinyPokemonName;
        this.shinyDexNumber = shinyDexNumber;
        this.targetPhase = targetPhase;
        this.timestamp = timestamp;
    }

    public int getPhaseNumber() {
        return phaseNumber;
    }

    public void setPhaseNumber(int phaseNumber) {
        this.phaseNumber = phaseNumber;
    }

    public int getEncounters() {
        return encounters;
    }

    public void setEncounters(int encounters) {
        this.encounters = encounters;
    }

    public String getShinyPokemonName() {
        return shinyPokemonName;
    }

    public void setShinyPokemonName(String shinyPokemonName) {
        this.shinyPokemonName = shinyPokemonName;
    }

    public int getShinyDexNumber() {
        return shinyDexNumber;
    }

    public void setShinyDexNumber(int shinyDexNumber) {
        this.shinyDexNumber = shinyDexNumber;
    }

    public boolean isTargetPhase() {
        return targetPhase;
    }

    public void setTargetPhase(boolean targetPhase) {
        this.targetPhase = targetPhase;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}