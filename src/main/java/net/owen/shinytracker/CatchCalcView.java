package net.owen.shinytracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CatchCalcView {

    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00");

    private CatchCalcView() {
    }

    public static VBox build() {
        VBox container = new VBox(22);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(8, 0, 0, 0));
        container.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label("Catch Calculator");
        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("Advanced catch calculator with game-specific mechanics.");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(Double.MAX_VALUE);
        subtitle.setStyle(
                "-fx-text-fill: #e2e2e2;" +
                        "-fx-font-size: 16px;"
        );

        HBox topRow = new HBox(24);
        topRow.setAlignment(Pos.TOP_LEFT);
        topRow.setMaxWidth(Double.MAX_VALUE);

        VBox basicInfoCard = buildSectionCard("Basic Info", 390, 430);
        VBox ballCard = buildSectionCard("Ball & Status", 390, 430);
        VBox specialCard = buildSectionCard("Special Effects", 430, 430);

        HBox.setHgrow(basicInfoCard, Priority.ALWAYS);
        HBox.setHgrow(ballCard, Priority.ALWAYS);
        HBox.setHgrow(specialCard, Priority.ALWAYS);

        ComboBox<String> gameBox = new ComboBox<>();
        gameBox.getItems().addAll(
                "Gold / Silver / Crystal",
                "Ruby / Sapphire / Emerald",
                "FireRed / LeafGreen",
                "Diamond / Pearl / Platinum",
                "HeartGold / SoulSilver",
                "Black / White",
                "Black 2 / White 2",
                "X / Y",
                "Omega Ruby / Alpha Sapphire",
                "Sun / Moon",
                "Ultra Sun / Ultra Moon",
                "Sword / Shield",
                "Brilliant Diamond / Shining Pearl",
                "Legends: Arceus",
                "Scarlet / Violet",
                "Legends: Z-A"
        );
        gameBox.setPromptText("Select Game");
        styleComboBox(gameBox);

        TextField pokemonSearchField = new TextField();
        pokemonSearchField.setPromptText("Search by Pokémon name or Dex number");
        styleTextField(pokemonSearchField);

        ComboBox<PokemonOption> pokemonBox = new ComboBox<>();
        pokemonBox.setPromptText("Select Pokémon");
        styleComboBox(pokemonBox);
        pokemonBox.setEditable(false);
        pokemonBox.setVisibleRowCount(12);
        pokemonBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PokemonOption object) {
                return object == null ? "" : object.getDisplayName();
            }

            @Override
            public PokemonOption fromString(String string) {
                return null;
            }
        });

        ObservableList<PokemonOption> allPokemon = FXCollections.observableArrayList(loadPokemonOptions());
        ObservableList<PokemonOption> filteredPokemon = FXCollections.observableArrayList(allPokemon);
        pokemonBox.setItems(filteredPokemon);

        Runnable refreshPokemonDropdown = () -> {
            filteredPokemon.setAll(filterPokemon(allPokemon, pokemonSearchField.getText()));
            pokemonBox.setVisibleRowCount(Math.min(12, Math.max(1, filteredPokemon.size())));

            PokemonOption selected = pokemonBox.getValue();
            if (selected != null && filteredPokemon.stream().noneMatch(option -> option.getDexNumber() == selected.getDexNumber())) {
                pokemonBox.setValue(null);
            }

            if (pokemonSearchField.isFocused()) {
                if (filteredPokemon.isEmpty()) {
                    pokemonBox.hide();
                } else {
                    pokemonBox.show();
                }
            }
        };

        pokemonSearchField.textProperty().addListener((obs, oldValue, newValue) -> refreshPokemonDropdown.run());
        pokemonSearchField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                refreshPokemonDropdown.run();
            } else {
                pokemonBox.hide();
            }
        });

        pokemonBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) {
                filteredPokemon.setAll(filterPokemon(allPokemon, pokemonSearchField.getText()));
                pokemonBox.setVisibleRowCount(Math.min(12, Math.max(1, filteredPokemon.size())));
            }
        });

        pokemonBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                pokemonSearchField.setText(newValue.getDisplayName());
                pokemonBox.hide();
            }
        });

        ComboBox<String> ballBox = new ComboBox<>();
        ballBox.getItems().addAll(
                "Poké Ball",
                "Great Ball",
                "Ultra Ball",
                "Premier Ball",
                "Luxury Ball",
                "Repeat Ball",
                "Timer Ball",
                "Dusk Ball",
                "Quick Ball",
                "Net Ball",
                "Dive Ball",
                "Nest Ball",
                "Heal Ball",
                "Level Ball",
                "Lure Ball",
                "Moon Ball",
                "Friend Ball",
                "Heavy Ball",
                "Fast Ball",
                "Love Ball",
                "Dream Ball",
                "Beast Ball",
                "Master Ball"
        );
        ballBox.setPromptText("Select Ball");
        styleComboBox(ballBox);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(
                "None",
                "Sleep",
                "Freeze",
                "Paralysis",
                "Burn",
                "Poison"
        );
        statusBox.setPromptText("Status Condition");
        statusBox.setValue("None");
        styleComboBox(statusBox);

        Slider hpSlider = new Slider(1, 100, 100);
        hpSlider.setShowTickLabels(true);
        hpSlider.setShowTickMarks(true);
        hpSlider.setMajorTickUnit(25);
        hpSlider.setMinorTickCount(4);
        hpSlider.setBlockIncrement(1);
        hpSlider.setMaxWidth(Double.MAX_VALUE);

        Label hpLabel = new Label("Current HP: 100%");
        hpLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );
        hpSlider.valueProperty().addListener((obs, oldVal, newVal) -> hpLabel.setText("Current HP: " + newVal.intValue() + "%"));

        CheckBox catchingCharmBox = buildCheckBox("Catching Charm");
        CheckBox offGuardBox = buildCheckBox("Off Guard / Unaware");
        CheckBox backStrikeBox = buildCheckBox("Back Strike");
        CheckBox staticEncounterBox = buildCheckBox("Static Encounter");

        ComboBox<String> oPowerBox = new ComboBox<>();
        oPowerBox.getItems().addAll(
                "None",
                "Capture O-Power Lv. 1",
                "Capture O-Power Lv. 2",
                "Capture O-Power Lv. 3"
        );
        oPowerBox.setValue("None");
        oPowerBox.setPromptText("Capture O-Power");
        styleComboBox(oPowerBox);

        ComboBox<String> rotoCatchBox = new ComboBox<>();
        rotoCatchBox.getItems().addAll("None", "Roto Catch");
        rotoCatchBox.setValue("None");
        rotoCatchBox.setPromptText("Roto Catch");
        styleComboBox(rotoCatchBox);

        ComboBox<String> catchingPowerBox = new ComboBox<>();
        catchingPowerBox.getItems().addAll(
                "None",
                "Catching Power Lv. 1",
                "Catching Power Lv. 2",
                "Catching Power Lv. 3"
        );
        catchingPowerBox.setValue("None");
        catchingPowerBox.setPromptText("Catching Power");
        styleComboBox(catchingPowerBox);

        VBox generalModifierBox = new VBox(10);
        generalModifierBox.setAlignment(Pos.TOP_LEFT);
        generalModifierBox.getChildren().addAll(
                buildFieldLabel("General"),
                catchingCharmBox,
                offGuardBox,
                backStrikeBox,
                staticEncounterBox
        );

        VBox threeDsModifierBox = new VBox(10);
        threeDsModifierBox.setAlignment(Pos.TOP_LEFT);
        threeDsModifierBox.getChildren().addAll(
                buildFieldLabel("3DS Effects"),
                oPowerBox,
                rotoCatchBox
        );

        VBox switchModifierBox = new VBox(10);
        switchModifierBox.setAlignment(Pos.TOP_LEFT);
        switchModifierBox.getChildren().addAll(
                buildFieldLabel("Switch Effects"),
                catchingPowerBox
        );

        Label specialHelpLabel = new Label();
        specialHelpLabel.setWrapText(true);
        specialHelpLabel.setMaxWidth(Double.MAX_VALUE);
        specialHelpLabel.setStyle(
                "-fx-text-fill: #d0d0d0;" +
                        "-fx-font-size: 13px;"
        );

        VBox resultCard = buildSectionCard("Result", 720, 360);
        resultCard.setAlignment(Pos.TOP_CENTER);

        Label resultTitle = new Label("Catch Chance");
        resultTitle.setStyle(
                "-fx-text-fill: #f0f0f0;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );

        Label resultValue = new Label("--");
        resultValue.setWrapText(true);
        resultValue.setAlignment(Pos.CENTER);
        resultValue.setMaxWidth(Double.MAX_VALUE);
        resultValue.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 34px;" +
                        "-fx-font-weight: bold;"
        );

        Label fiftyLabel = buildResultLine("Balls for 50%: --");
        Label seventyFiveLabel = buildResultLine("Balls for 75%: --");
        Label ninetyLabel = buildResultLine("Balls for 90%: --");
        Label ninetyFiveLabel = buildResultLine("Balls for 95%: --");
        Label ninetyNineLabel = buildResultLine("Balls for 99%: --");

        HBox oddsRow = new HBox(26);
        oddsRow.setAlignment(Pos.CENTER);
        oddsRow.setMaxWidth(Double.MAX_VALUE);

        VBox leftOddsColumn = new VBox(8, fiftyLabel, seventyFiveLabel, ninetyLabel);
        VBox rightOddsColumn = new VBox(8, ninetyFiveLabel, ninetyNineLabel);
        leftOddsColumn.setAlignment(Pos.CENTER_LEFT);
        rightOddsColumn.setAlignment(Pos.CENTER_LEFT);
        oddsRow.getChildren().addAll(leftOddsColumn, rightOddsColumn);

        Label resultNote = new Label(
                "Select your game, Pokémon, ball, HP, and modifiers, then calculate the catch odds."
        );
        resultNote.setWrapText(true);
        resultNote.setAlignment(Pos.CENTER);
        resultNote.setMaxWidth(640);
        resultNote.setStyle(
                "-fx-text-fill: #e2e2e2;" +
                        "-fx-font-size: 14px;"
        );

        Button calculateButton = new Button("Calculate");
        UiStyles.stylePrimaryButton(calculateButton);
        calculateButton.setMinWidth(220);
        calculateButton.setMaxWidth(220);

        Runnable refreshGameSpecificUi = () -> {
            String game = gameBox.getValue();

            boolean supportsCatchingCharm = usesCatchingCharm(game);
            boolean supportsOffGuard = usesOffGuard(game);
            boolean supportsBackStrike = usesBackStrike(game);
            boolean supportsStaticEncounter = usesStaticEncounter(game);
            boolean supportsOPower = usesOPower(game);
            boolean supportsRotoCatch = usesRotoCatch(game);
            boolean supportsMealPower = usesMealPower(game);

            setManagedVisible(catchingCharmBox, supportsCatchingCharm);
            setManagedVisible(offGuardBox, supportsOffGuard);
            setManagedVisible(backStrikeBox, supportsBackStrike);
            setManagedVisible(staticEncounterBox, supportsStaticEncounter);
            setManagedVisible(oPowerBox, supportsOPower);
            setManagedVisible(rotoCatchBox, supportsRotoCatch);
            setManagedVisible(catchingPowerBox, supportsMealPower);

            setManagedVisible(generalModifierBox, supportsCatchingCharm || supportsOffGuard || supportsBackStrike || supportsStaticEncounter);
            setManagedVisible(threeDsModifierBox, supportsOPower || supportsRotoCatch);
            setManagedVisible(switchModifierBox, supportsMealPower);

            if (!supportsCatchingCharm) {
                catchingCharmBox.setSelected(false);
            }
            if (!supportsOffGuard) {
                offGuardBox.setSelected(false);
            }
            if (!supportsBackStrike) {
                backStrikeBox.setSelected(false);
            }
            if (!supportsStaticEncounter) {
                staticEncounterBox.setSelected(false);
            }
            if (!supportsOPower) {
                oPowerBox.setValue("None");
            }
            if (!supportsRotoCatch) {
                rotoCatchBox.setValue("None");
            }
            if (!supportsMealPower) {
                catchingPowerBox.setValue("None");
            }

            specialHelpLabel.setText(buildSpecialEffectsHelp(game));
        };

        gameBox.valueProperty().addListener((obs, oldValue, newValue) -> refreshGameSpecificUi.run());
        refreshGameSpecificUi.run();

        calculateButton.setOnAction(e -> {
            String game = gameBox.getValue();
            PokemonOption pokemon = pokemonBox.getValue();
            String ball = ballBox.getValue();
            String status = statusBox.getValue();

            if (game == null || pokemon == null || ball == null || status == null) {
                resultValue.setText("--");
                fiftyLabel.setText("Balls for 50%: --");
                seventyFiveLabel.setText("Balls for 75%: --");
                ninetyLabel.setText("Balls for 90%: --");
                ninetyFiveLabel.setText("Balls for 95%: --");
                ninetyNineLabel.setText("Balls for 99%: --");
                resultNote.setText("Please select a game, Pokémon, ball, and status first.");
                return;
            }

            double probability = calculateCatchChance(
                    game,
                    pokemon,
                    ball,
                    status,
                    hpSlider.getValue(),
                    catchingCharmBox.isSelected(),
                    offGuardBox.isSelected(),
                    backStrikeBox.isSelected(),
                    staticEncounterBox.isSelected(),
                    oPowerBox.getValue(),
                    rotoCatchBox.getValue(),
                    catchingPowerBox.getValue()
            );

            resultValue.setText(PERCENT_FORMAT.format(probability * 100.0) + "%");
            fiftyLabel.setText("Balls for 50%: " + formatBallCount(requiredBalls(probability, 0.50)));
            seventyFiveLabel.setText("Balls for 75%: " + formatBallCount(requiredBalls(probability, 0.75)));
            ninetyLabel.setText("Balls for 90%: " + formatBallCount(requiredBalls(probability, 0.90)));
            ninetyFiveLabel.setText("Balls for 95%: " + formatBallCount(requiredBalls(probability, 0.95)));
            ninetyNineLabel.setText("Balls for 99%: " + formatBallCount(requiredBalls(probability, 0.99)));

            resultNote.setText(
                    "Catch rate: " + pokemon.getCaptureRate() +
                            " • Dex: " + pokemon.getDexNumber() +
                            " • Based on the selected game rules, ball, HP, status, and visible special effects."
            );
        });

        basicInfoCard.getChildren().addAll(
                buildFieldLabel("Game"),
                gameBox,
                buildFieldLabel("Pokémon Search"),
                pokemonSearchField,
                buildFieldLabel("Selected Pokémon"),
                pokemonBox,
                buildFieldLabel("HP Remaining"),
                hpLabel,
                hpSlider
        );

        ballCard.getChildren().addAll(
                buildFieldLabel("Ball"),
                ballBox,
                buildFieldLabel("Status"),
                statusBox
        );

        VBox specialContent = new VBox(14);
        specialContent.setAlignment(Pos.TOP_LEFT);
        specialContent.getChildren().addAll(
                generalModifierBox,
                new Separator(),
                threeDsModifierBox,
                new Separator(),
                switchModifierBox,
                new Separator(),
                specialHelpLabel
        );
        specialCard.getChildren().add(specialContent);

        Region resultSpacer = new Region();
        VBox.setVgrow(resultSpacer, Priority.ALWAYS);

        resultCard.getChildren().addAll(
                resultTitle,
                resultValue,
                oddsRow,
                resultNote,
                resultSpacer,
                calculateButton
        );

        HBox resultRow = new HBox(resultCard);
        resultRow.setAlignment(Pos.CENTER);
        resultRow.setMaxWidth(Double.MAX_VALUE);
        resultRow.setPadding(new Insets(4, 0, 0, 0));

        topRow.getChildren().addAll(basicInfoCard, ballCard, specialCard);

        Label footer = new Label(
                "The special effects panel changes by game so only relevant modifiers stay visible."
        );
        footer.setWrapText(true);
        footer.setMaxWidth(Double.MAX_VALUE);
        footer.setStyle(
                "-fx-text-fill: #d0d0d0;" +
                        "-fx-font-size: 13px;"
        );

        container.getChildren().addAll(
                title,
                subtitle,
                topRow,
                resultRow,
                footer
        );

        return container;
    }

    private static double calculateCatchChance(
            String game,
            PokemonOption pokemon,
            String ball,
            String status,
            double hpPercent,
            boolean catchingCharm,
            boolean offGuard,
            boolean backStrike,
            boolean staticEncounter,
            String oPower,
            String rotoCatch,
            String catchingPower
    ) {
        if ("Master Ball".equals(ball)) {
            return 1.0;
        }

        double catchRate = Math.max(1.0, pokemon.getCaptureRate());
        double ballMultiplier = getBallMultiplier(game, ball, catchRate);
        double statusMultiplier = getStatusMultiplier(game, status);
        double specialMultiplier = getSpecialMultiplier(
                game,
                catchingCharm,
                offGuard,
                backStrike,
                staticEncounter,
                oPower,
                rotoCatch,
                catchingPower
        );

        double hpFactor = Math.max(0.01, (300.0 - (2.0 * hpPercent)) / 300.0);
        double a = catchRate * ballMultiplier * statusMultiplier * specialMultiplier * hpFactor;

        if (isLegendsGame(game)) {
            return clamp(a / 255.0, 0.0, 1.0);
        }

        if (a >= 255.0) {
            return 1.0;
        }

        if (a <= 0.0) {
            return 0.0;
        }

        double b = 1048560.0 / Math.sqrt(Math.sqrt(16711680.0 / a));
        double shakeProbability = b / 65536.0;
        return clamp(Math.pow(shakeProbability, 4), 0.0, 1.0);
    }

    private static double getStatusMultiplier(String game, String status) {
        if (status == null || "None".equals(status)) {
            return 1.0;
        }

        boolean majorStatus = "Sleep".equals(status) || "Freeze".equals(status);
        if ("Gold / Silver / Crystal".equals(game)) {
            return majorStatus ? 2.0 : 1.5;
        }
        return majorStatus ? 2.5 : 1.5;
    }

    private static double getSpecialMultiplier(
            String game,
            boolean catchingCharm,
            boolean offGuard,
            boolean backStrike,
            boolean staticEncounter,
            String oPower,
            String rotoCatch,
            String catchingPower
    ) {
        double multiplier = 1.0;

        if (catchingCharm && usesCatchingCharm(game)) {
            multiplier *= 1.1;
        }

        if (offGuard && usesOffGuard(game)) {
            if ("Scarlet / Violet".equals(game)) {
                multiplier *= 2.0;
            } else if ("Legends: Arceus".equals(game)) {
                multiplier *= 1.6;
            } else if ("Legends: Z-A".equals(game)) {
                multiplier *= 1.5;
            }
        }

        if (backStrike && usesBackStrike(game)) {
            if ("Scarlet / Violet".equals(game)) {
                multiplier *= 1.2;
            } else if ("Legends: Arceus".equals(game)) {
                multiplier *= 1.3;
            } else if ("Legends: Z-A".equals(game)) {
                multiplier *= 1.25;
            }
        }

        if (staticEncounter && usesStaticEncounter(game)) {
            multiplier *= 0.85;
        }

        if (usesOPower(game) && oPower != null) {
            switch (oPower) {
                case "Capture O-Power Lv. 1" -> multiplier *= 1.5;
                case "Capture O-Power Lv. 2" -> multiplier *= 2.0;
                case "Capture O-Power Lv. 3" -> multiplier *= 2.5;
                default -> {
                }
            }
        }

        if (usesRotoCatch(game) && "Roto Catch".equals(rotoCatch)) {
            multiplier *= 2.0;
        }

        if (usesMealPower(game) && catchingPower != null) {
            switch (catchingPower) {
                case "Catching Power Lv. 1" -> multiplier *= 1.1;
                case "Catching Power Lv. 2" -> multiplier *= 1.25;
                case "Catching Power Lv. 3" -> multiplier *= 2.0;
                default -> {
                }
            }
        }

        return multiplier;
    }

    private static double getBallMultiplier(String game, String ball, double catchRate) {
        if (ball == null) {
            return 1.0;
        }

        return switch (ball) {
            case "Poké Ball" -> 1.0;
            case "Great Ball" -> 1.5;
            case "Ultra Ball" -> 2.0;
            case "Premier Ball" -> 1.0;
            case "Luxury Ball" -> 1.0;
            case "Heal Ball" -> 1.0;
            case "Quick Ball" -> 5.0;
            case "Repeat Ball" -> 3.0;
            case "Timer Ball" -> 4.0;
            case "Dusk Ball" -> 3.0;
            case "Net Ball" -> 3.5;
            case "Dive Ball" -> 3.5;
            case "Nest Ball" -> 1.8;
            case "Level Ball" -> 2.0;
            case "Lure Ball" -> 4.0;
            case "Moon Ball" -> 4.0;
            case "Friend Ball" -> 1.0;
            case "Heavy Ball" -> getHeavyBallMultiplier(game, catchRate);
            case "Fast Ball" -> 4.0;
            case "Love Ball" -> 8.0;
            case "Dream Ball" -> 4.0;
            case "Beast Ball" -> 5.0;
            default -> 1.0;
        };
    }

    private static double getHeavyBallMultiplier(String game, double catchRate) {
        if ("Gold / Silver / Crystal".equals(game) || "HeartGold / SoulSilver".equals(game)) {
            if (catchRate < 30) {
                return 3.0;
            }
            if (catchRate < 70) {
                return 2.0;
            }
            return 1.0;
        }

        if (catchRate < 30) {
            return 1.6;
        }
        if (catchRate < 70) {
            return 1.3;
        }
        return 1.0;
    }

    private static boolean isLegendsGame(String game) {
        return "Legends: Arceus".equals(game) || "Legends: Z-A".equals(game);
    }

    private static boolean usesCatchingCharm(String game) {
        return "Sword / Shield".equals(game)
                || "Brilliant Diamond / Shining Pearl".equals(game)
                || "Scarlet / Violet".equals(game)
                || "Legends: Z-A".equals(game);
    }

    private static boolean usesOffGuard(String game) {
        return "Legends: Arceus".equals(game)
                || "Scarlet / Violet".equals(game)
                || "Legends: Z-A".equals(game);
    }

    private static boolean usesBackStrike(String game) {
        return "Legends: Arceus".equals(game)
                || "Scarlet / Violet".equals(game)
                || "Legends: Z-A".equals(game);
    }

    private static boolean usesStaticEncounter(String game) {
        return "Scarlet / Violet".equals(game);
    }

    private static boolean usesOPower(String game) {
        return "X / Y".equals(game) || "Omega Ruby / Alpha Sapphire".equals(game);
    }

    private static boolean usesRotoCatch(String game) {
        return "Ultra Sun / Ultra Moon".equals(game);
    }

    private static boolean usesMealPower(String game) {
        return "Scarlet / Violet".equals(game);
    }

    private static String buildSpecialEffectsHelp(String game) {
        if (game == null || game.isBlank()) {
            return "Select a game to show the relevant special effects for that generation.";
        }

        if (usesMealPower(game)) {
            return "Scarlet / Violet supports Catching Power and encounter-specific effects like Off Guard and Back Strike.";
        }
        if (usesRotoCatch(game)) {
            return "Ultra Sun / Ultra Moon supports Roto Catch as its extra temporary catch modifier.";
        }
        if (usesOPower(game)) {
            return "Generation VI uses Capture O-Power instead of later catch-power style effects.";
        }
        if (usesOffGuard(game) || usesBackStrike(game) || usesCatchingCharm(game)) {
            return "This game supports only the visible general modifiers shown above.";
        }
        return "This game uses the standard catch inputs here without extra catch-power style effects.";
    }

    private static int requiredBalls(double singleCatchProbability, double targetProbability) {
        if (singleCatchProbability >= 1.0) {
            return 1;
        }
        if (singleCatchProbability <= 0.0) {
            return Integer.MAX_VALUE;
        }

        return (int) Math.ceil(Math.log(1.0 - targetProbability) / Math.log(1.0 - singleCatchProbability));
    }

    private static String formatBallCount(int count) {
        if (count == Integer.MAX_VALUE) {
            return "--";
        }
        return String.valueOf(Math.max(1, count));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static List<PokemonOption> filterPokemon(List<PokemonOption> allPokemon, String queryText) {
        String query = queryText == null ? "" : queryText.trim().toLowerCase(Locale.ROOT);
        return allPokemon.stream()
                .filter(option -> option.matches(query))
                .limit(250)
                .toList();
    }

    private static void setManagedVisible(Region node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private static VBox buildSectionCard(String title, double width, double height) {
        VBox card = new VBox(14);
        card.setMinSize(width, height);
        card.setPrefSize(width, height);
        card.setMaxSize(Double.MAX_VALUE, height);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(
                "-fx-background-color: #202020;" +
                        "-fx-background-radius: 18px;" +
                        "-fx-border-color: #3d3d3d;" +
                        "-fx-border-radius: 18px;" +
                        "-fx-border-width: 1px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;"
        );

        card.getChildren().add(titleLabel);
        return card;
    }

    private static Label buildFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-text-fill: #f0f0f0;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );
        return label;
    }

    private static Label buildResultLine(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );
        return label;
    }

    private static CheckBox buildCheckBox(String text) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;"
        );
        return checkBox;
    }

    private static void styleTextField(TextField textField) {
        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setStyle(
                "-fx-background-color: #2b2b2b;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #aaaaaa;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8px;"
        );
    }

    private static <T> void styleComboBox(ComboBox<T> comboBox) {
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setButtonCell(createComboCell());
        comboBox.setCellFactory(listView -> createComboCell());
        comboBox.setStyle(
                "-fx-background-color: #2b2b2b;" +
                        "-fx-font-size: 14px;" +
                        "-fx-mark-color: white;"
        );
    }

    private static <T> ListCell<T> createComboCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setStyle(
                        "-fx-background-color: #2b2b2b;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;"
                );

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        };
    }

    private static List<PokemonOption> loadPokemonOptions() {
        List<PokemonOption> options = new ArrayList<>();

        try (InputStream input = CatchCalcView.class.getResourceAsStream("/national_dex.txt")) {
            if (input == null) {
                return options;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                String line;
                boolean firstLine = true;

                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty()) {
                        continue;
                    }

                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }

                    String[] parts = trimmed.split(",", -1);
                    if (parts.length <= 9 || !parts[0].matches("\\d+")) {
                        continue;
                    }

                    int dexNumber = Integer.parseInt(parts[0]);
                    String identifier = parts[1].trim();
                    int captureRate = parseIntSafe(parts[9], 45);
                    String displayName = formatPokemonName(identifier);

                    if (!displayName.isBlank()) {
                        options.add(new PokemonOption(dexNumber, displayName, captureRate));
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return options;
    }

    private static int parseIntSafe(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private static String formatPokemonName(String rawIdentifier) {
        if (rawIdentifier == null) {
            return "";
        }

        String value = rawIdentifier.trim();
        if (value.isEmpty()) {
            return "";
        }

        value = value.replace('-', ' ');
        value = value.replace('_', ' ');

        String[] words = value.split("\\s+");
        StringBuilder builder = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (!builder.isEmpty()) {
                builder.append(' ');
            }

            builder.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                builder.append(word.substring(1).toLowerCase(Locale.ROOT));
            }
        }

        return builder.toString();
    }

    private static final class PokemonOption {
        private final int dexNumber;
        private final String displayName;
        private final int captureRate;

        private PokemonOption(int dexNumber, String displayName, int captureRate) {
            this.dexNumber = dexNumber;
            this.displayName = displayName;
            this.captureRate = captureRate;
        }

        private boolean matches(String query) {
            if (query == null || query.isBlank()) {
                return true;
            }

            String lowerName = displayName.toLowerCase(Locale.ROOT);
            String dexText = String.valueOf(dexNumber);
            return lowerName.contains(query) || dexText.startsWith(query);
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getDexNumber() {
            return dexNumber;
        }

        public int getCaptureRate() {
            return captureRate;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
