package net.owen.shinytracker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public final class StatsView {

    private StatsView() {}

    public static VBox build(List<Hunt> hunts) {

        VBox container = new VBox(36);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(8, 0, 0, 0));

        FlowPane overviewGrid = new FlowPane();
        overviewGrid.setHgap(18);
        overviewGrid.setVgap(18);
        overviewGrid.setPrefWrapLength(1200);
        overviewGrid.setAlignment(Pos.TOP_LEFT);

        FlowPane recordGrid = new FlowPane();
        recordGrid.setHgap(18);
        recordGrid.setVgap(18);
        recordGrid.setPrefWrapLength(1200);
        recordGrid.setAlignment(Pos.TOP_LEFT);

        int totalHunts = hunts == null ? 0 : hunts.size();
        int completedHunts = 0;
        int totalEncounters = 0;
        int totalPhases = 0;
        int totalPhaseEncounters = 0;

        int longestHunt = 0;
        int shortestHunt = Integer.MAX_VALUE;

        int longestPhase = 0;
        int fastestShiny = Integer.MAX_VALUE;

        String longestHuntPokemon = "";
        String shortestHuntPokemon = "";
        String longestPhasePokemon = "";
        String fastestShinyPokemon = "";

        int mostHuntedEncounters = 0;
        String mostHuntedPokemon = "";

        if (hunts != null) {

            for (Hunt hunt : hunts) {

                int combinedEncounters =
                        hunt.getTotalEncounters() + hunt.getResetCount();

                totalEncounters += combinedEncounters;
                totalPhases += hunt.getPhaseCount();

                if (hunt.isCompleted() && combinedEncounters < fastestShiny) {
                    completedHunts++;
                    fastestShiny = combinedEncounters;
                    fastestShinyPokemon = safePokemonName(hunt);
                } else if (hunt.isCompleted()) {
                    completedHunts++;
                }

                if (combinedEncounters > longestHunt) {
                    longestHunt = combinedEncounters;
                    longestHuntPokemon = safePokemonName(hunt);
                }

                if (combinedEncounters > 0 && combinedEncounters < shortestHunt) {
                    shortestHunt = combinedEncounters;
                    shortestHuntPokemon = safePokemonName(hunt);
                }

                if (combinedEncounters > mostHuntedEncounters) {
                    mostHuntedEncounters = combinedEncounters;
                    mostHuntedPokemon = safePokemonName(hunt);
                }

                for (HuntPhase phase : hunt.getPhases()) {
                    totalPhaseEncounters += phase.getEncounters();

                    if (phase.getEncounters() > longestPhase) {
                        longestPhase = phase.getEncounters();
                        longestPhasePokemon = safePokemonName(hunt);
                    }
                }
            }
        }

        int avgEncountersPerHunt =
                totalHunts > 0 ? totalEncounters / totalHunts : 0;

        int avgPhaseEncounters =
                totalPhases > 0 ? totalPhaseEncounters / totalPhases : 0;

        int totalShinies = completedHunts;

        if (shortestHunt == Integer.MAX_VALUE) {
            shortestHunt = 0;
        }

        if (fastestShiny == Integer.MAX_VALUE) {
            fastestShiny = 0;
        }

        overviewGrid.getChildren().addAll(
                buildStatCard("Total Hunts", String.valueOf(totalHunts)),
                buildStatCard("Completed Hunts", String.valueOf(completedHunts)),
                buildStatCard("Total Shinies", String.valueOf(totalShinies)),
                buildStatCard("Total Encounters", format(totalEncounters)),
                buildStatCard("Total Phases", String.valueOf(totalPhases)),
                buildStatCard("Avg Encounters / Hunt", format(avgEncountersPerHunt)),
                buildStatCard("Avg Phase Encounters", format(avgPhaseEncounters)),
                buildStatCard("Longest Hunt", format(longestHunt)),
                buildStatCard("Shortest Hunt", format(shortestHunt)),
                buildStatCard("Longest Phase", format(longestPhase)),
                buildStatCard("Fastest Shiny", format(fastestShiny))
        );

        Label recordHeader = new Label("Pokémon Records");
        recordHeader.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;"
        );

        Label recordSubheader = new Label("Named record cards for your biggest hunts and fastest wins.");
        recordSubheader.setStyle(
                "-fx-text-fill: #b8b8b8;" +
                "-fx-font-size: 14px;"
        );

        recordGrid.getChildren().addAll(
                buildRecordCard("Longest Hunt Pokémon", longestHuntPokemon, longestHunt, "encounters"),
                buildRecordCard("Shortest Hunt Pokémon", shortestHuntPokemon, shortestHunt, "encounters"),
                buildRecordCard("Longest Phase Pokémon", longestPhasePokemon, longestPhase, "encounters"),
                buildRecordCard("Fastest Shiny Pokémon", fastestShinyPokemon, fastestShiny, "encounters"),
                buildRecordCard("Most Hunted Pokémon", mostHuntedPokemon, mostHuntedEncounters, "encounters")
        );

        /* Records moved ABOVE overview stats for quicker viewing */
        container.getChildren().addAll(
                recordHeader,
                recordSubheader,
                recordGrid,
                overviewGrid
        );

        return container;
    }

    private static VBox buildStatCard(String title, String value) {

        VBox card = new VBox(10);
        card.setPrefSize(230, 120);
        card.setMinSize(230, 120);
        card.setMaxSize(230, 120);

        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));

        card.setStyle(
                "-fx-background-color: #232323;" +
                "-fx-background-radius: 14px;" +
                "-fx-border-color: #3a3a3a;" +
                "-fx-border-radius: 14px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setStyle(
                "-fx-text-fill: #cfcfcf;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setWrapText(true);
        valueLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;"
        );

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private static VBox buildRecordCard(String title, String pokemonName, int encounters, String suffix) {

        VBox card = new VBox(8);
        card.setPrefSize(230, 120);
        card.setMinSize(230, 120);
        card.setMaxSize(230, 120);

        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));

        card.setStyle(
                "-fx-background-color: #232323;" +
                "-fx-background-radius: 14px;" +
                "-fx-border-color: #3a3a3a;" +
                "-fx-border-radius: 14px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setStyle(
                "-fx-text-fill: #cfcfcf;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        String displayPokemon = (pokemonName == null || pokemonName.isBlank()) ? "No hunts yet" : pokemonName;

        Label pokemonLabel = new Label(displayPokemon);
        pokemonLabel.setWrapText(true);
        pokemonLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;"
        );

        String detailText = encounters > 0 ? format(encounters) + " " + suffix : "No data yet";

        Label detailLabel = new Label(detailText);
        detailLabel.setWrapText(true);
        detailLabel.setStyle(
                "-fx-text-fill: #b8b8b8;" +
                "-fx-font-size: 13px;"
        );

        card.getChildren().addAll(titleLabel, pokemonLabel, detailLabel);
        return card;
    }

    private static String safePokemonName(Hunt hunt) {
        if (hunt == null || hunt.getPokemonName() == null || hunt.getPokemonName().isBlank()) {
            return "Unknown";
        }
        return hunt.getPokemonName();
    }

    private static String format(int value) {
        return String.format("%,d", value);
    }
}
