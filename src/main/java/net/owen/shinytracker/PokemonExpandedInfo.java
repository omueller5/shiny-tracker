package net.owen.shinytracker;

import java.util.List;

public class PokemonExpandedInfo {

    private final int hp;
    private final int attack;
    private final int defense;
    private final int specialAttack;
    private final int specialDefense;
    private final int speed;
    private final int bst;

    private final List<String> evolutionChain;
    private final List<String> evolutionDetails;

    public PokemonExpandedInfo(
            int hp,
            int attack,
            int defense,
            int specialAttack,
            int specialDefense,
            int speed,
            List<String> evolutionChain,
            List<String> evolutionDetails
    ) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
        this.bst = hp + attack + defense + specialAttack + specialDefense + speed;
        this.evolutionChain = evolutionChain == null ? List.of() : List.copyOf(evolutionChain);
        this.evolutionDetails = evolutionDetails == null ? List.of() : List.copyOf(evolutionDetails);
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpecialAttack() {
        return specialAttack;
    }

    public int getSpecialDefense() {
        return specialDefense;
    }

    public int getSpeed() {
        return speed;
    }

    public int getBst() {
        return bst;
    }

    public List<String> getEvolutionChain() {
        return evolutionChain;
    }

    public List<String> getEvolutionDetails() {
        return evolutionDetails;
    }

    public boolean hasStats() {
        return hp > 0 || attack > 0 || defense > 0 || specialAttack > 0 || specialDefense > 0 || speed > 0;
    }

    public boolean hasEvolutionChain() {
        return !evolutionChain.isEmpty();
    }

    public boolean hasEvolutionDetails() {
        return !evolutionDetails.isEmpty();
    }
}