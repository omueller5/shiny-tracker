package net.owen.shinytracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.util.List;

public final class CreateHuntView {

    private CreateHuntView() {
    }

    public static ScrollPane build(List<Hunt> hunts,
                                   HuntStorage huntStorage,
                                   AppNavigator navigator) {

        VBox wrapper = new VBox(24);
        wrapper.setPadding(new Insets(28));
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setStyle("-fx-background-color: " + AppTheme.BACKGROUND_MAIN + ";");

        Label header = new Label("Create New Hunt");
        header.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;"
        );

        VBox panel = new VBox(18);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(24));
        panel.setPrefWidth(700);
        panel.setMaxWidth(700);
        panel.setStyle(
                "-fx-background-color: #111111;" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-color: #2e2e2e;" +
                        "-fx-border-radius: 16px;"
        );

        Label pokemonLabel = new Label("Pokémon");
        pokemonLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        TextField pokemonSearchField = new TextField();
        pokemonSearchField.setPromptText("Search Pokémon by name or dex number...");
        pokemonSearchField.setPrefWidth(420);
        pokemonSearchField.setStyle(
                "-fx-background-color: #1d1d1d;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #888888;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-border-color: #333333;"
        );

        ObservableList<PokemonEntry> pokemonList =
                FXCollections.observableArrayList(PokemonData.getAllPokemon());

        FilteredList<PokemonEntry> filteredPokemon =
                new FilteredList<>(pokemonList, p -> true);

        ComboBox<PokemonEntry> pokemonComboBox = new ComboBox<>();
        pokemonComboBox.setItems(filteredPokemon);
        pokemonComboBox.setPrefWidth(420);
        pokemonComboBox.setVisibleRowCount(12);
        stylePokemonComboBox(pokemonComboBox);

        PokemonEntry defaultPokemon = findDefaultPokemon(16);
        if (defaultPokemon != null) {
            pokemonComboBox.setValue(defaultPokemon);
        }

        pokemonSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String search = newValue == null ? "" : newValue.toLowerCase().trim();

            filteredPokemon.setPredicate(pokemon -> {
                if (search.isEmpty()) {
                    return true;
                }

                String name = pokemon.getDisplayName().toLowerCase();
                String dex = String.valueOf(pokemon.getDexNumber());
                String formatted = pokemon.getFormattedDisplayName().toLowerCase();

                return name.contains(search)
                        || dex.contains(search)
                        || formatted.contains(search);
            });

            if (!filteredPokemon.isEmpty()) {
                if (!filteredPokemon.contains(pokemonComboBox.getValue())) {
                    pokemonComboBox.setValue(filteredPokemon.get(0));
                }
            } else {
                pokemonComboBox.setValue(null);
            }
        });

        Label selectedPokemonInfoLabel = new Label();
        selectedPokemonInfoLabel.setStyle("-fx-text-fill: #d0d0d0; -fx-font-size: 18px;");

        Label gameLabel = new Label("Game");
        gameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        ComboBox<GameInfo> gameComboBox = new ComboBox<>();
        gameComboBox.setItems(FXCollections.observableArrayList(GameDisplayUtil.ALL_GAMES));
        gameComboBox.setValue(GameDisplayUtil.FRLG);
        gameComboBox.setPrefWidth(420);
        gameComboBox.setVisibleRowCount(12);
        styleComboBox(gameComboBox);

        Label methodLabel = new Label("Hunt Method");
        methodLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        ComboBox<HuntMethod> methodComboBox = new ComboBox<>();
        methodComboBox.setPrefWidth(420);
        methodComboBox.setVisibleRowCount(12);
        styleComboBox(methodComboBox);

        Label zaExtrasLabel = new Label("Z-A Extras");
        zaExtrasLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        zaExtrasLabel.setVisible(false);
        zaExtrasLabel.setManaged(false);

        CheckBox hyperspaceCheckBox = new CheckBox("Hyperspace Hunt");
        hyperspaceCheckBox.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );
        hyperspaceCheckBox.setVisible(false);
        hyperspaceCheckBox.setManaged(false);

        Label doughnutLabel = new Label("Doughnut Level");
        doughnutLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        doughnutLabel.setVisible(false);
        doughnutLabel.setManaged(false);

        ComboBox<String> doughnutComboBox = new ComboBox<>();
        doughnutComboBox.setItems(FXCollections.observableArrayList(
                "None",
                "Level 1 (+1 roll)",
                "Level 2 (+2 rolls)",
                "Level 3 (+3 rolls)"
        ));
        doughnutComboBox.setValue("None");
        doughnutComboBox.setPrefWidth(420);
        styleComboBox(doughnutComboBox);
        doughnutComboBox.setVisible(false);
        doughnutComboBox.setManaged(false);

        Label resetPresetLabel = new Label("Reset Preset");
        resetPresetLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        resetPresetLabel.setVisible(false);
        resetPresetLabel.setManaged(false);

        ComboBox<String> resetPresetComboBox = new ComboBox<>();
        resetPresetComboBox.setPrefWidth(420);
        styleComboBox(resetPresetComboBox);
        resetPresetComboBox.setVisible(false);
        resetPresetComboBox.setManaged(false);

        Separator zaSeparator = new Separator();
        zaSeparator.setVisible(false);
        zaSeparator.setManaged(false);

        CheckBox shinyCharmCheckBox = new CheckBox("Enable Shiny Charm");
        shinyCharmCheckBox.setSelected(false);
        shinyCharmCheckBox.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );

        Label oddsLabel = new Label();
        oddsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        Label charmLabel = new Label();
        charmLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        Runnable updateMethodsForGame = () -> {
            GameInfo selectedGame = gameComboBox.getValue();
            List<HuntMethod> methods = HuntMethodUtil.getMethodsForGame(selectedGame);

            methodComboBox.setItems(FXCollections.observableArrayList(methods));

            if (!methods.isEmpty()) {
                if (!methods.contains(methodComboBox.getValue())) {
                    methodComboBox.setValue(methods.get(0));
                }
            } else {
                methodComboBox.setValue(null);
            }
        };

        Runnable updateZaExtras = () -> {
            GameInfo selectedGame = gameComboBox.getValue();
            HuntMethod selectedMethod = methodComboBox.getValue();

            boolean isZa = selectedGame != null
                    && "Legends: Z-A".equals(selectedGame.getDisplayName());

            zaExtrasLabel.setVisible(isZa);
            zaExtrasLabel.setManaged(isZa);
            zaSeparator.setVisible(isZa);
            zaSeparator.setManaged(isZa);

            if (!isZa) {
                hyperspaceCheckBox.setVisible(false);
                hyperspaceCheckBox.setManaged(false);
                doughnutLabel.setVisible(false);
                doughnutLabel.setManaged(false);
                doughnutComboBox.setVisible(false);
                doughnutComboBox.setManaged(false);
                resetPresetLabel.setVisible(false);
                resetPresetLabel.setManaged(false);
                resetPresetComboBox.setVisible(false);
                resetPresetComboBox.setManaged(false);

                hyperspaceCheckBox.setSelected(false);
                doughnutComboBox.setValue("None");
                resetPresetComboBox.getItems().clear();
                return;
            }

            boolean fossilMethod = selectedMethod == HuntMethod.FOSSIL_RESET;

            hyperspaceCheckBox.setVisible(true);
            hyperspaceCheckBox.setManaged(true);

            resetPresetLabel.setVisible(true);
            resetPresetLabel.setManaged(true);
            resetPresetComboBox.setVisible(true);
            resetPresetComboBox.setManaged(true);

            boolean hyperspaceEnabled = hyperspaceCheckBox.isSelected();

            if (hyperspaceEnabled) {
                doughnutLabel.setVisible(true);
                doughnutLabel.setManaged(true);
                doughnutComboBox.setVisible(true);
                doughnutComboBox.setManaged(true);

                resetPresetComboBox.setItems(FXCollections.observableArrayList(
                        "Fast Travel (4.5s)",
                        "Warp Pad (6s)",
                        "Custom"
                ));

                if (selectedMethod == HuntMethod.WARP_PAD_RESET) {
                    resetPresetComboBox.setValue("Warp Pad (6s)");
                } else if (resetPresetComboBox.getValue() == null
                        || !resetPresetComboBox.getItems().contains(resetPresetComboBox.getValue())) {
                    resetPresetComboBox.setValue("Fast Travel (4.5s)");
                }
            } else {
                doughnutLabel.setVisible(false);
                doughnutLabel.setManaged(false);
                doughnutComboBox.setVisible(false);
                doughnutComboBox.setManaged(false);
                doughnutComboBox.setValue("None");

                resetPresetComboBox.setItems(FXCollections.observableArrayList(
                        "Bench (19s)",
                        "Fast Travel (5s)",
                        "Custom"
                ));

                if (selectedMethod == HuntMethod.BENCH_RESET) {
                    resetPresetComboBox.setValue("Bench (19s)");
                } else if (selectedMethod == HuntMethod.FAST_TRAVEL_RESET) {
                    resetPresetComboBox.setValue("Fast Travel (5s)");
                } else if (resetPresetComboBox.getValue() == null
                        || !resetPresetComboBox.getItems().contains(resetPresetComboBox.getValue())) {
                    resetPresetComboBox.setValue("Bench (19s)");
                }
            }

            if (fossilMethod) {
                shinyCharmCheckBox.setSelected(false);
                shinyCharmCheckBox.setDisable(true);

                doughnutLabel.setVisible(false);
                doughnutLabel.setManaged(false);
                doughnutComboBox.setVisible(false);
                doughnutComboBox.setManaged(false);
                doughnutComboBox.setValue("None");

                resetPresetComboBox.setItems(FXCollections.observableArrayList("Custom"));
                resetPresetComboBox.setValue("Custom");
            }
        };

        Runnable updateCreateDetails = () -> {
            PokemonEntry selectedPokemon = pokemonComboBox.getValue();
            GameInfo selectedGame = gameComboBox.getValue();

            if (selectedPokemon != null) {
                selectedPokemonInfoLabel.setText(
                        "Selected: " + selectedPokemon.getFormattedDisplayName()
                );
            } else {
                selectedPokemonInfoLabel.setText("Selected: -");
            }

            if (selectedGame == null) {
                oddsLabel.setText("Odds: -");
                charmLabel.setText("Shiny Charm: -");
                shinyCharmCheckBox.setDisable(true);
                shinyCharmCheckBox.setSelected(false);
                return;
            }

            boolean fossilMethod = methodComboBox.getValue() == HuntMethod.FOSSIL_RESET;
            boolean charmSupported = selectedGame.isShinyCharmSupported() && !fossilMethod;

            if (!charmSupported) {
                shinyCharmCheckBox.setSelected(false);
            }

            shinyCharmCheckBox.setDisable(!charmSupported);

            int displayedOdds = selectedGame.getShinyOddsDenominator();

            if ("Legends: Z-A".equals(selectedGame.getDisplayName()) && !fossilMethod) {
                int bonusRolls = 0;

                if (shinyCharmCheckBox.isSelected()) {
                    bonusRolls += 3;
                }

                if (hyperspaceCheckBox.isSelected()) {
                    String doughnutValue = doughnutComboBox.getValue();
                    if ("Level 1 (+1 roll)".equals(doughnutValue)) {
                        bonusRolls += 1;
                    } else if ("Level 2 (+2 rolls)".equals(doughnutValue)) {
                        bonusRolls += 2;
                    } else if ("Level 3 (+3 rolls)".equals(doughnutValue)) {
                        bonusRolls += 3;
                    }
                }

                displayedOdds = Math.max(1, displayedOdds / (1 + bonusRolls));
            } else if (charmSupported && shinyCharmCheckBox.isSelected()) {
                displayedOdds = Math.max(1, displayedOdds / 2);
            }

            oddsLabel.setText("Odds: 1 / " + displayedOdds);
            charmLabel.setText("Shiny Charm: " + (charmSupported ? "Available" : "Not Available"));
        };

        updateMethodsForGame.run();
        updateZaExtras.run();
        updateCreateDetails.run();

        pokemonComboBox.setOnAction(e -> updateCreateDetails.run());

        gameComboBox.setOnAction(e -> {
            updateMethodsForGame.run();
            updateZaExtras.run();
            updateCreateDetails.run();
        });

        methodComboBox.setOnAction(e -> {
            updateZaExtras.run();
            updateCreateDetails.run();
        });

        hyperspaceCheckBox.setOnAction(e -> {
            updateZaExtras.run();
            updateCreateDetails.run();
        });

        doughnutComboBox.setOnAction(e -> updateCreateDetails.run());
        resetPresetComboBox.setOnAction(e -> updateCreateDetails.run());
        shinyCharmCheckBox.setOnAction(e -> updateCreateDetails.run());

        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        Button createButton = new Button("Create Hunt");
        UiStyles.stylePrimaryButton(createButton);

        Button backButton = new Button("Back");
        UiStyles.styleSecondaryButton(backButton);

        createButton.setOnAction(e -> {
            PokemonEntry selectedPokemon = pokemonComboBox.getValue();
            GameInfo selectedGame = gameComboBox.getValue();
            HuntMethod selectedMethod = methodComboBox.getValue();

            if (selectedPokemon == null || selectedGame == null || selectedMethod == null) {
                return;
            }

            boolean shinyCharmEnabled =
                    selectedGame.isShinyCharmSupported()
                            && selectedMethod != HuntMethod.FOSSIL_RESET
                            && shinyCharmCheckBox.isSelected();

            Hunt newHunt = new Hunt(
                    selectedPokemon.getDisplayName(),
                    selectedPokemon.getDexNumber(),
                    selectedGame.getDisplayName(),
                    selectedMethod.getDisplayName(),
                    0,
                    false,
                    LocalDateTime.now().toString(),
                    null,
                    shinyCharmEnabled,
                    hyperspaceCheckBox.isSelected(),
                    doughnutComboBox.getValue(),
                    resetPresetComboBox.getValue()
            );

            hunts.add(newHunt);
            huntStorage.saveHunt(newHunt);

            navigator.showActiveHunt(newHunt);
        });

        backButton.setOnAction(e -> navigator.showDashboard());

        buttonRow.getChildren().addAll(createButton, backButton);

        panel.getChildren().addAll(
                pokemonLabel,
                pokemonSearchField,
                pokemonComboBox,
                selectedPokemonInfoLabel,
                gameLabel,
                gameComboBox,
                methodLabel,
                methodComboBox,
                zaSeparator,
                zaExtrasLabel,
                hyperspaceCheckBox,
                resetPresetLabel,
                resetPresetComboBox,
                doughnutLabel,
                doughnutComboBox,
                shinyCharmCheckBox,
                oddsLabel,
                charmLabel,
                buttonRow
        );

        wrapper.getChildren().addAll(header, panel);

        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;"
        );

        wrapper.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));

        return scrollPane;
    }

    private static <T> void styleComboBox(ComboBox<T> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: #1d1d1d;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #3a3a3a;"
        );

        comboBox.setCellFactory(listView -> {
            listView.setStyle(
                    "-fx-background-color: #1d1d1d;" +
                            "-fx-control-inner-background: #1d1d1d;" +
                            "-fx-border-color: transparent;" +
                            "-fx-background-insets: 0;" +
                            "-fx-padding: 0;"
            );

            return new ListCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(empty || item == null ? null : item.toString());
                    setStyle(
                            "-fx-text-fill: white;" +
                                    "-fx-background-color: #1d1d1d;" +
                                    "-fx-font-size: 16px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-border-width: 0;" +
                                    "-fx-border-color: transparent;" +
                                    "-fx-background-insets: 0;" +
                                    "-fx-padding: 8 10 8 10;"
                    );
                }
            };
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty || item == null ? null : item.toString());
                setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-background-color: #1d1d1d;" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-width: 0;" +
                                "-fx-border-color: transparent;" +
                                "-fx-background-insets: 0;" +
                                "-fx-padding: 8 10 8 10;"
                );
            }
        });

        comboBox.setOnShowing(event -> {
            Node listView = comboBox.lookup(".list-view");
            if (listView != null) {
                listView.setStyle(
                        "-fx-background-color: #1d1d1d;" +
                                "-fx-control-inner-background: #1d1d1d;" +
                                "-fx-border-color: transparent;" +
                                "-fx-background-insets: 0;" +
                                "-fx-padding: 0;"
                );
            }
        });
    }

    private static void stylePokemonComboBox(ComboBox<PokemonEntry> comboBox) {
        styleComboBox(comboBox);

        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PokemonEntry pokemon) {
                return pokemon == null ? "" : pokemon.getFormattedDisplayName();
            }

            @Override
            public PokemonEntry fromString(String string) {
                return null;
            }
        });

        comboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(PokemonEntry item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFormattedDisplayName());
                setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-background-color: #1d1d1d;" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;"
                );
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(PokemonEntry item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFormattedDisplayName());
                setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-background-color: #1d1d1d;" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;"
                );
            }
        });
    }

    private static PokemonEntry findDefaultPokemon(int dexNumber) {
        for (PokemonEntry pokemon : PokemonData.getAllPokemon()) {
            if (pokemon.getDexNumber() == dexNumber) {
                return pokemon;
            }
        }
        return null;
    }
}