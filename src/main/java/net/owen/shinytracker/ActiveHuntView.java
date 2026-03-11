
package net.owen.shinytracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.FlowPane;
import javafx.stage.Window;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ActiveHuntView extends VBox {

    private static final DateTimeFormatter PHASE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy - h:mm a");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00");

    private static final String PAGE_STYLE =
            "-fx-background-color: transparent;";

    private static final String BACK_BUTTON_STYLE =
            "-fx-background-color: #242424;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #3b3b3b;" +
            "-fx-padding: 10 16 10 16;" +
            "-fx-cursor: hand;";

    private static final String CARD_STYLE =
            "-fx-background-color: #18181c;" +
            "-fx-background-radius: 22px;" +
            "-fx-border-radius: 22px;" +
            "-fx-border-color: #2f2f36;" +
            "-fx-border-width: 1px;" +
            "-fx-padding: 22;";

    private static final String INNER_CARD_STYLE =
            "-fx-background-color: #101014;" +
            "-fx-background-radius: 18px;" +
            "-fx-border-radius: 18px;" +
            "-fx-border-color: #26262d;" +
            "-fx-border-width: 1px;" +
            "-fx-padding: 16;";

    private static final String TITLE_STYLE =
            "-fx-text-fill: white;" +
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;";

    private static final String SECTION_TITLE_STYLE =
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;";

    private static final String LABEL_STYLE =
            "-fx-text-fill: #d6d6dc;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;";

    private static final String VALUE_STYLE =
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;";

    private static final String SMALL_MUTED_STYLE =
            "-fx-text-fill: #c8cfdd;" +
            "-fx-font-size: 14px;";

    private static final String COUNTER_VALUE_STYLE =
            "-fx-text-fill: white;" +
            "-fx-font-size: 54px;" +
            "-fx-font-weight: bold;";

    private static final String NEUTRAL_BUTTON_STYLE =
            "-fx-background-color: #374151;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 14px;" +
            "-fx-border-radius: 14px;" +
            "-fx-border-color: #4b5563;" +
            "-fx-padding: 12 22 12 22;" +
            "-fx-cursor: hand;";

    private static final String PLUS_BUTTON_STYLE =
            "-fx-background-color: #166534;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 14px;" +
            "-fx-border-radius: 14px;" +
            "-fx-border-color: #22c55e;" +
            "-fx-padding: 12 22 12 22;" +
            "-fx-cursor: hand;";

    private static final String MINUS_BUTTON_STYLE =
            "-fx-background-color: #7f1d1d;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 14px;" +
            "-fx-border-radius: 14px;" +
            "-fx-border-color: #ef4444;" +
            "-fx-padding: 12 22 12 22;" +
            "-fx-cursor: hand;";

    private static final String MILESTONE_TITLE_STYLE =
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;";

    private static final String MILESTONE_VALUE_STYLE =
            "-fx-text-fill: #d4d4da;" +
            "-fx-font-size: 14px;";

    private static final String PHASE_CARD_STYLE =
            "-fx-background-color: #060a12;" +
            "-fx-background-radius: 16px;" +
            "-fx-border-radius: 16px;" +
            "-fx-border-color: #1a2333;" +
            "-fx-padding: 16;";

    private static final List<String> BALLS = List.of(
            "Poké Ball", "Great Ball", "Ultra Ball", "Master Ball",
            "Premier Ball", "Luxury Ball", "Heal Ball",
            "Quick Ball", "Dusk Ball", "Timer Ball", "Repeat Ball",
            "Net Ball", "Dive Ball", "Nest Ball", "Level Ball"
    );

    private static final List<String> STATUS_OPTIONS = List.of(
            "None", "Sleep", "Freeze", "Paralysis", "Burn", "Poison"
    );

    private static final List<String> HP_OPTIONS = List.of(
            "Full HP", "Half HP", "Red HP", "1 HP"
    );

    private final Hunt hunt;
    private final HuntStorage huntStorage;
    private final AppNavigator navigator;
    private final AppSettings settings;
    private final ThemePalette tabPalette;

    private final Label oddsValueLabel = new Label();
    private final Label shinyCharmValueLabel = new Label();
    private final Label estimatedTimeValueLabel = new Label();
    private final Label resetCountLabel = new Label();
    private final CheckBox completedCheckBox = new CheckBox("Hunt Done");

    private final ImageView spriteView = new ImageView();
    private final VBox milestoneList = new VBox(10);
    private final VBox phaseListBox = new VBox(12);
    private final VBox phaseSummaryBox = new VBox(10);
    private final ScrollPane phaseScrollPane = new ScrollPane(phaseListBox);

    private VBox pokemonColumnRef;
    private VBox centerColumnRef;
    private VBox rightColumnRef;
    private HBox mainContentRef;
    private VBox pokemonInfoCardRef;
    private StackPane spritePaneRef;
    private VBox huntDetailsCardRef;
    private VBox counterCardRef;
    private TabPane tabPaneRef;

    private final PokemonDexEntry dexEntry;
    private final PokemonJsonEntry jsonEntry;

    private final PokemonExpandedInfo expandedInfo;

    private final KeybindManager keybindManager = new KeybindManager();

    private static final String PHASE_SCROLL_STYLE =
            "-fx-background: #101014;" +
            "-fx-background-color: #101014;" +
            "-fx-control-inner-background: #101014;" +
            "-fx-border-color: transparent;";
    private static final String COMPACT_COUNTER_VALUE_STYLE = "";

    public ActiveHuntView(Hunt hunt, HuntStorage huntStorage, AppNavigator navigator) {
        this.hunt = hunt;
        this.huntStorage = huntStorage;
        this.navigator = navigator;
        this.settings = SettingsStorage.loadSettings();
        this.dexEntry = loadDexEntry(hunt.getDexNumber());
        this.expandedInfo = PokemonExpandedInfoLoader.load(hunt.getDexNumber());
        this.jsonEntry = loadJsonEntry(hunt.getDexNumber());
        this.tabPalette = ThemePalette.fromThemeName(readThemeName(settings));

        setSpacing(18);
        setPadding(new Insets(20));
        setStyle(PAGE_STYLE);

        HBox topBar = buildTopBar();
        TabPane tabPane = buildTabPane();

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getChildren().addAll(topBar, tabPane);

        keybindManager.loadFromSettings(settings);

        setupKeybinds();

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                updateResponsiveLayout(newScene.getWidth());
                newScene.widthProperty().addListener((widthObs, oldWidth, newWidth) ->
                        updateResponsiveLayout(newWidth.doubleValue()));
            }
        });

        refresh();
    }

    public static Parent build(Hunt hunt, HuntStorage huntStorage, AppNavigator navigator) {
        return new ActiveHuntView(hunt, huntStorage, navigator);
    }

    private HBox buildTopBar() {
        HBox topBar = new HBox(18);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 110, 6, 0));

        Button backButton = new Button("← Back");
        backButton.setStyle(BACK_BUTTON_STYLE);
        backButton.setOnAction(e -> navigator.showDashboard());

        Label pageTitle = new Label("Active Hunt");
        pageTitle.setStyle(TITLE_STYLE);

        topBar.getChildren().addAll(backButton, pageTitle);
        return topBar;
    }

    private TabPane buildTabPane() {
        tabPaneRef = new TabPane();
        tabPaneRef.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPaneRef.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        Tab huntTab = new Tab();
        huntTab.setGraphic(createTabLabel("Hunt"));
        huntTab.setContent(buildHuntTabContent());

        Tab pokemonTab = new Tab();
        pokemonTab.setGraphic(createTabLabel("Pokémon"));
        pokemonTab.setContent(buildPokemonTabContent());

        Tab catchTab = new Tab();
        catchTab.setGraphic(createTabLabel("Catch"));
        catchTab.setContent(buildCatchTabContent());

        tabPaneRef.getTabs().addAll(huntTab, pokemonTab, catchTab);

        tabPaneRef.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                tabPaneRef.applyCss();
                applyTabTheme();
            }
        });

        tabPaneRef.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> applyTabTheme());

        return tabPaneRef;
    }


    private VBox createCard(double spacing, Pos alignment, String style) {
        VBox card = new VBox(spacing);
        card.setAlignment(alignment);
        card.setStyle(style);
        return card;
    }

    private Label createSectionTitle(String text) {
        Label title = new Label(text);
        title.setStyle(SECTION_TITLE_STYLE);
        return title;
    }

    private Label createValueLabel(String text, String style, boolean wrapText) {
        Label label = new Label(text);
        label.setStyle(style);
        label.setWrapText(wrapText);
        return label;
    }

    private Label createValueLabel(String text, String style) {
        return createValueLabel(text, style, false);
    }

    private HBox createLabeledRow(String labelText, Node valueNode, double spacing, double labelMinWidth, String labelStyle) {
        HBox row = new HBox(spacing);
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText + ":");
        label.setMinWidth(labelMinWidth);
        label.setStyle(labelStyle);

        row.getChildren().addAll(label, valueNode);
        return row;
    }

    private FlowPane createChipPane() {
        FlowPane pane = new FlowPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setAlignment(Pos.CENTER_LEFT);
        return pane;
    }

    private ScrollPane createPhaseScrollPane(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle(PHASE_SCROLL_STYLE);
        scrollPane.setPrefViewportHeight(520);
        return scrollPane;
    }

    private void applyDarkScrollPaneTheme(ScrollPane scrollPane) {
        if (scrollPane == null) {
            return;
        }

        scrollPane.setStyle(PHASE_SCROLL_STYLE);

        scrollPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                scrollPane.applyCss();

                Node viewport = scrollPane.lookup(".viewport");
                if (viewport != null) {
                    viewport.setStyle("-fx-background-color: #101014;");
                }

                Node corner = scrollPane.lookup(".corner");
                if (corner != null) {
                    corner.setStyle("-fx-background-color: #101014;");
                }

                Node content = scrollPane.getContent();
                if (content != null) {
                    content.setStyle("-fx-background-color: #101014;");
                }
            }
        });
    }

    private void setShown(Node node, boolean shown) {
        if (node != null) {
            node.setManaged(shown);
            node.setVisible(shown);
        }
    }

    private void setSpriteSize(double size) {
        spriteView.setFitWidth(size);
        spriteView.setFitHeight(size);
    }

    private void setWidthConstraints(Region region, double minWidth, double prefWidth, double maxWidth) {
        if (region != null) {
            region.setMinWidth(minWidth);
            region.setPrefWidth(prefWidth);
            region.setMaxWidth(maxWidth);
        }
    }

    private void playShinyCompletionSoundIfNeeded(boolean wasCompleted, boolean isDone) {
        if (!wasCompleted && isDone) {
            ShinySoundPlayer.playForGame(hunt.getGame());
            playPokemonCardFlare();
        }
    }

    private void playPokemonCardFlare() {
        DropShadow outerFlare = new DropShadow();
        outerFlare.setColor(Color.web("#6ee16e"));
        outerFlare.setRadius(0);
        outerFlare.setSpread(0.25);
        outerFlare.setOffsetX(0);
        outerFlare.setOffsetY(0);

        DropShadow innerFlare = new DropShadow();
        innerFlare.setColor(Color.web("#4aa3ff"));
        innerFlare.setRadius(0);
        innerFlare.setSpread(0.25);
        innerFlare.setOffsetX(0);
        innerFlare.setOffsetY(0);

        if (spritePaneRef != null) {
            spritePaneRef.setEffect(outerFlare);
        }

        if (spriteView != null) {
            spriteView.setEffect(innerFlare);
        }

        Timeline flareTimeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(outerFlare.radiusProperty(), 0),
                        new KeyValue(outerFlare.spreadProperty(), 0.25),
                        new KeyValue(innerFlare.radiusProperty(), 0),
                        new KeyValue(innerFlare.spreadProperty(), 0.25),
                        new KeyValue(spriteView.scaleXProperty(), 1.0),
                        new KeyValue(spriteView.scaleYProperty(), 1.0),
                        new KeyValue(spriteView.translateYProperty(), 0)
                ),
                new KeyFrame(
                        Duration.millis(160),
                        new KeyValue(outerFlare.radiusProperty(), 34),
                        new KeyValue(outerFlare.spreadProperty(), 0.45),
                        new KeyValue(innerFlare.radiusProperty(), 22),
                        new KeyValue(innerFlare.spreadProperty(), 0.45),
                        new KeyValue(spriteView.scaleXProperty(), 1.14),
                        new KeyValue(spriteView.scaleYProperty(), 1.14),
                        new KeyValue(spriteView.translateYProperty(), -10)
                ),
                new KeyFrame(
                        Duration.millis(320),
                        new KeyValue(outerFlare.radiusProperty(), 18),
                        new KeyValue(outerFlare.spreadProperty(), 0.25),
                        new KeyValue(innerFlare.radiusProperty(), 12),
                        new KeyValue(innerFlare.spreadProperty(), 0.22),
                        new KeyValue(spriteView.scaleXProperty(), 1.05),
                        new KeyValue(spriteView.scaleYProperty(), 1.05),
                        new KeyValue(spriteView.translateYProperty(), -4)
                ),
                new KeyFrame(
                        Duration.millis(700),
                        new KeyValue(outerFlare.radiusProperty(), 0),
                        new KeyValue(outerFlare.spreadProperty(), 0.0),
                        new KeyValue(innerFlare.radiusProperty(), 0),
                        new KeyValue(innerFlare.spreadProperty(), 0.0),
                        new KeyValue(spriteView.scaleXProperty(), 1.0),
                        new KeyValue(spriteView.scaleYProperty(), 1.0),
                        new KeyValue(spriteView.translateYProperty(), 0)
                )
        );

        flareTimeline.setOnFinished(e -> {
            if (spritePaneRef != null) {
                spritePaneRef.setEffect(null);
            }
            if (spriteView != null) {
                spriteView.setEffect(null);
                spriteView.setScaleX(1.0);
                spriteView.setScaleY(1.0);
                spriteView.setTranslateY(0);
            }
        });

        flareTimeline.play();
    }

    private void configurePhaseCompletionState(boolean isDone) {
        boolean wasCompleted = hunt.isCompleted();

        hunt.setCompleted(isDone);

        if (isDone && !hasTargetPhaseLogged()) {
            int currentPhaseEncounters = hunt.getResetCount();

            HuntPhase phase = new HuntPhase(
                    hunt.getCurrentPhaseNumber(),
                    currentPhaseEncounters,
                    hunt.getPokemonName(),
                    hunt.getDexNumber(),
                    true,
                    LocalDateTime.now().toString()
            );

            hunt.getPhases().add(phase);
            hunt.setTotalEncounters(hunt.getTotalEncounters() + currentPhaseEncounters);
            hunt.setResetCount(0);
        }

        playShinyCompletionSoundIfNeeded(wasCompleted, isDone);
        onHuntUpdated();
    }

    private Label createTabLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 18 8 18;"
        );
        return label;
    }

    private void applyTabTheme() {
        if (tabPaneRef == null) {
            return;
        }

        Node background = tabPaneRef.lookup(".tab-header-background");
        if (background != null) {
            background.setStyle("-fx-background-color: transparent;");
        }

        Node headerArea = tabPaneRef.lookup(".tab-header-area");
        if (headerArea != null) {
            headerArea.setStyle("-fx-padding: 0 0 12 0; -fx-background-color: transparent;");
        }

        Node headersRegion = tabPaneRef.lookup(".headers-region");
        if (headersRegion != null) {
            headersRegion.setStyle("-fx-background-color: transparent;");
        }

        Node contentArea = tabPaneRef.lookup(".tab-content-area");
        if (contentArea != null) {
            contentArea.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        }

        for (Node node : tabPaneRef.lookupAll(".tab")) {
            boolean selected = node.getStyleClass().contains("selected");
            if (selected) {
                node.setStyle(
                        "-fx-background-color: " + tabPalette.selectedTabBackground + ";" +
                        "-fx-background-radius: 18px 18px 0 0;" +
                        "-fx-border-radius: 18px 18px 0 0;" +
                        "-fx-border-color: " + tabPalette.selectedTabBorder + ";" +
                        "-fx-border-width: 1px;"
                );
            } else {
                node.setStyle(
                        "-fx-background-color: " + tabPalette.unselectedTabBackground + ";" +
                        "-fx-background-radius: 16px 16px 0 0;" +
                        "-fx-border-radius: 16px 16px 0 0;" +
                        "-fx-border-color: " + tabPalette.unselectedTabBorder + ";" +
                        "-fx-border-width: 1px; -fx-opacity: 0.96;"
                );
            }
        }
    }

    private Node buildHuntTabContent() {
        mainContentRef = new HBox(26);
        mainContentRef.setAlignment(Pos.TOP_CENTER);

        pokemonColumnRef = buildPokemonColumn();
        centerColumnRef = buildCenterColumn();
        rightColumnRef = buildRightColumn();

        pokemonColumnRef.setMinWidth(320);
        pokemonColumnRef.setPrefWidth(340);

        centerColumnRef.setMinWidth(560);
        centerColumnRef.setPrefWidth(640);
        centerColumnRef.setMaxWidth(Double.MAX_VALUE);

        rightColumnRef.setMinWidth(340);
        rightColumnRef.setPrefWidth(360);
        rightColumnRef.setMaxWidth(380);

        HBox.setHgrow(pokemonColumnRef, Priority.NEVER);
        HBox.setHgrow(centerColumnRef, Priority.ALWAYS);
        HBox.setHgrow(rightColumnRef, Priority.NEVER);

        mainContentRef.getChildren().addAll(pokemonColumnRef, centerColumnRef, rightColumnRef);
        return mainContentRef;
    }

    private Node buildPokemonTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(18));

        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.TOP_LEFT);

        VBox heroCard = buildPokemonHeroCard();
        VBox entryCard = buildPokemonEntryCard();
        HBox.setHgrow(entryCard, Priority.ALWAYS);

        topRow.getChildren().addAll(heroCard, entryCard);

        VBox extraCard = buildPokemonExtraCard();

        content.getChildren().addAll(topRow, extraCard);

        return createTransparentScrollPane(content);
    }

    private VBox buildPokemonHeroCard() {
        VBox card = createCard(16, Pos.TOP_LEFT, CARD_STYLE);
        card.setPrefWidth(410);
        card.setMinWidth(380);

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label dexBadge = createValueLabel(
                "#" + String.format("%03d", hunt.getDexNumber()),
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #0f172a;" +
                        "-fx-background-radius: 999px;" +
                        "-fx-border-color: #334155;" +
                        "-fx-border-radius: 999px;" +
                        "-fx-padding: 8 14 8 14;"
        );
        headerRow.getChildren().add(dexBadge);

        Label speciesTitle = createValueLabel(getDisplayName(), TITLE_STYLE);
        Label speciesSub = createValueLabel(getSpeciesLabel(), SMALL_MUTED_STYLE, true);

        ImageView infoSpriteView = new ImageView();
        infoSpriteView.setPreserveRatio(true);
        infoSpriteView.setFitWidth(250);
        infoSpriteView.setFitHeight(250);
        infoSpriteView.setImage(loadCurrentSprite());

        StackPane spritePane = new StackPane(infoSpriteView);
        spritePane.setMinHeight(300);
        spritePane.setPrefHeight(320);
        spritePane.setStyle(INNER_CARD_STYLE);
        spritePane.setPickOnBounds(false);

        card.getChildren().addAll(
                headerRow,
                speciesTitle,
                speciesSub,
                spritePane,
                buildTypeChipPane(getTypes())
        );

        return card;
    }

    private VBox buildPokemonEntryCard() {
        VBox card = createCard(18, Pos.TOP_LEFT, CARD_STYLE);

        Label japaneseName = createValueLabel(
                "Japanese Name: " + getJapaneseNameLabel(),
                "-fx-text-fill: #dbe4ff;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;",
                true
        );

        Label description = createValueLabel(
                getDescriptionLabel(),
                "-fx-text-fill: white;" +
                        "-fx-font-size: 17px;" +
                        "-fx-line-spacing: 4px;",
                true
        );

        HBox metaTopRow = new HBox(32);
        metaTopRow.setAlignment(Pos.CENTER_LEFT);
        metaTopRow.getChildren().addAll(
                buildMetaBox("Introduced In", createGameValueNode(getIntroducedInGame())),
                buildMetaBox("Generation", createGenerationValueLabel())
        );

        HBox infoColumns = new HBox(32);
        infoColumns.setAlignment(Pos.TOP_LEFT);

        VBox leftColumn = new VBox(12);
        leftColumn.getChildren().addAll(
                createPokedexRow("National Dex", "#" + hunt.getDexNumber()),
                createPokedexRow("Height", getHeightLabel()),
                createPokedexRow("Weight", getWeightLabel())
        );

        VBox rightColumn = new VBox(12);
        rightColumn.getChildren().addAll(
                createPokedexRow("Base Catch Rate", getBaseCatchRateLabel()),
                createPokedexRow("Primary Type", getPrimaryTypeLabel()),
                createPokedexRow("Secondary Type", getSecondaryTypeLabel())
        );

        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);
        infoColumns.getChildren().addAll(leftColumn, rightColumn);

        card.getChildren().addAll(
                createSectionTitle("Pokédex Entry"),
                japaneseName,
                description,
                new Separator(),
                metaTopRow,
                new Separator(),
                infoColumns
        );

        return card;
    }

    private VBox buildPokemonExtraCard() {
        VBox card = createCard(18, Pos.TOP_LEFT, CARD_STYLE);

        HBox contentRow = new HBox(32);
        contentRow.setAlignment(Pos.TOP_LEFT);

        VBox leftColumn = createCard(14, Pos.TOP_LEFT, "");
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        Label abilitiesValue = createValueLabel(joinList(getAbilities()), VALUE_STYLE, true);
        FlowPane weaknessChips = buildWeaknessChipPane(getWeaknesses());

        leftColumn.getChildren().addAll(
                createValueLabel("Abilities", LABEL_STYLE),
                abilitiesValue,
                createValueLabel("Weaknesses", LABEL_STYLE),
                weaknessChips
        );

        VBox middleColumn = createCard(12, Pos.TOP_LEFT, "");
        HBox.setHgrow(middleColumn, Priority.ALWAYS);
        middleColumn.getChildren().addAll(
                createPokedexRow("Gender Ratio", getGenderLabel()),
                createPokedexRow("Egg Group", getEggGroupLabel()),
                createPokedexRow("Hatch Counter", getHatchCounterLabel()),
                createPokedexRow("Legendary", booleanLabel(dexEntry != null && dexEntry.legendary)),
                createPokedexRow("Mythical", booleanLabel(dexEntry != null && dexEntry.mythical))
        );

        VBox rightColumn = createCard(12, Pos.TOP_LEFT, "");
        HBox.setHgrow(rightColumn, Priority.ALWAYS);

        VBox statsList = new VBox(6);
        statsList.getChildren().addAll(
                createPokedexRow("HP", String.valueOf(expandedInfo.getHp())),
                createPokedexRow("Attack", String.valueOf(expandedInfo.getAttack())),
                createPokedexRow("Defense", String.valueOf(expandedInfo.getDefense())),
                createPokedexRow("Sp. Atk", String.valueOf(expandedInfo.getSpecialAttack())),
                createPokedexRow("Sp. Def", String.valueOf(expandedInfo.getSpecialDefense())),
                createPokedexRow("Speed", String.valueOf(expandedInfo.getSpeed())),
                createPokedexRow("BST", String.valueOf(expandedInfo.getBst()))
        );

        Label evoChain = createValueLabel(
                expandedInfo.getEvolutionChain().isEmpty()
                        ? "None"
                        : String.join(" → ", expandedInfo.getEvolutionChain()),
                VALUE_STYLE,
                true
        );

        VBox evoDetailsList = buildEvolutionDetailsList();

        rightColumn.getChildren().addAll(
                createValueLabel("Base Stats", LABEL_STYLE),
                statsList,
                new Separator(),
                createValueLabel("Evolution Chain", LABEL_STYLE),
                evoChain,
                createValueLabel("Evolution Details", LABEL_STYLE),
                evoDetailsList
        );

        contentRow.getChildren().addAll(leftColumn, middleColumn, rightColumn);
        card.getChildren().addAll(createSectionTitle("Field Notes"), contentRow);

        return card;
    }


    private VBox buildMetaBox(String titleText, Node valueNode) {
        VBox box = new VBox(8);
        box.getChildren().addAll(createValueLabel(titleText, LABEL_STYLE), valueNode);
        return box;
    }

    private Label createGenerationValueLabel() {
        return createValueLabel(
                getGenerationLabel(),
                "-fx-text-fill: white;" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #121826;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #2d3748;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-padding: 8 14 8 14;"
        );
    }

    private VBox buildEvolutionDetailsList() {
        VBox detailsList = new VBox(4);

        if (expandedInfo.getEvolutionDetails().isEmpty()) {
            detailsList.getChildren().add(createValueLabel("None", VALUE_STYLE));
            return detailsList;
        }

        for (String detail : expandedInfo.getEvolutionDetails()) {
            detailsList.getChildren().add(createValueLabel(detail, VALUE_STYLE, true));
        }

        return detailsList;
    }

    private void setupKeybinds() {
        keybindManager.registerAction(KeybindAction.INCREMENT_COUNTER, () -> {
            if (isCurrentActiveView()) {
                incrementCounter();
            }
        });

        keybindManager.registerAction(KeybindAction.DECREMENT_COUNTER, () -> {
            if (isCurrentActiveView()) {
                decrementCounter();
            }
        });

        keybindManager.registerAction(KeybindAction.OPEN_HUNT_TAB, () -> {
            if (isCurrentActiveView()) {
                tabPaneRef.getSelectionModel().select(0);
            }
        });

        keybindManager.registerAction(KeybindAction.OPEN_POKEMON_TAB, () -> {
            if (isCurrentActiveView()) {
                tabPaneRef.getSelectionModel().select(1);
            }
        });

        keybindManager.registerAction(KeybindAction.OPEN_CATCH_TAB, () -> {
            if (isCurrentActiveView()) {
                tabPaneRef.getSelectionModel().select(2);
            }
        });

        if (getScene() != null) {
            keybindManager.install(getScene());
        }

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                keybindManager.install(newScene);
            }
        });
    }

    private boolean isCurrentActiveView() {
        Scene scene = getScene();
        if (scene == null) {
            return false;
        }

        if (scene.getRoot() == null) {
            return false;
        }

        Window window = scene.getWindow();
        if (window != null && !window.isFocused()) {
            return false;
        }

        return scene.getRoot() == this || isDescendantOf(scene.getRoot(), this);
    }

    private boolean isDescendantOf(Parent parent, Node target) {
        if (parent == null) {
            return false;
        }

        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child == target) {
                return true;
            }

            if (child instanceof Parent childParent && isDescendantOf(childParent, target)) {
                return true;
            }
        }

        return false;
    }

    private void incrementCounter() {
        if (!isCurrentActiveView()) {
            return;
        }

        hunt.setResetCount(hunt.getResetCount() + 1);
        onHuntUpdated();
    }

    private void decrementCounter() {
        if (!isCurrentActiveView()) {
            return;
        }

        if (hunt.getResetCount() <= 0) {
            return;
        }

        hunt.setResetCount(hunt.getResetCount() - 1);
        onHuntUpdated();
    }

    private HBox createPokedexRow(String labelText, String valueText) {
        Label valueLabel = new Label(valueText);
        valueLabel.setWrapText(true);
        valueLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;"
        );
        return createPokedexRow(labelText, valueLabel);
    }

    private HBox createPokedexRow(String labelText, Node valueNode) {
        return createLabeledRow(
                labelText,
                valueNode,
                12,
                145,
                "-fx-text-fill: #cfd6e6;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );
    }

    private FlowPane buildTypeChipPane(List<String> types) {
        FlowPane pane = createChipPane();

        if (types == null || types.isEmpty()) {
            pane.getChildren().add(createTypeChip("-"));
            return pane;
        }

        for (String type : types) {
            pane.getChildren().add(createTypeChip(type));
        }

        return pane;
    }

    private FlowPane buildWeaknessChipPane(List<String> weaknesses) {
        FlowPane pane = createChipPane();

        if (weaknesses == null || weaknesses.isEmpty()) {
            pane.getChildren().add(createValueLabel("-", VALUE_STYLE));
            return pane;
        }

        for (String weakness : weaknesses) {
            pane.getChildren().add(createWeaknessChip(weakness));
        }

        return pane;
    }

    private Label createTypeChip(String type) {
        String[] colors = getTypeChipColors(type);

        Label chip = new Label(type);
        chip.setStyle(
                "-fx-text-fill: " + colors[2] + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: " + colors[0] + ";" +
                        "-fx-background-radius: 999px;" +
                        "-fx-border-color: " + colors[1] + ";" +
                        "-fx-border-radius: 999px;" +
                        "-fx-padding: 7 14 7 14;"
        );
        return chip;
    }

    private Label createWeaknessChip(String weakness) {
        Label chip = new Label(weakness);
        chip.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #1f2937;" +
                        "-fx-background-radius: 999px;" +
                        "-fx-border-color: #334155;" +
                        "-fx-border-radius: 999px;" +
                        "-fx-padding: 7 14 7 14;"
        );
        return chip;
    }

    private String[] getTypeChipColors(String type) {
        if (type == null) {
            return new String[] { "#1f2937", "#334155", "white" };
        }

        return switch (type.trim().toLowerCase()) {
            case "normal" -> new String[] { "#3f3f46", "#52525b", "white" };
            case "fire" -> new String[] { "#7c2d12", "#ea580c", "white" };
            case "water" -> new String[] { "#1d4ed8", "#60a5fa", "white" };
            case "electric" -> new String[] { "#854d0e", "#facc15", "white" };
            case "grass" -> new String[] { "#166534", "#4ade80", "white" };
            case "ice" -> new String[] { "#155e75", "#67e8f9", "white" };
            case "fighting" -> new String[] { "#7f1d1d", "#ef4444", "white" };
            case "poison" -> new String[] { "#581c87", "#c084fc", "white" };
            case "ground" -> new String[] { "#78350f", "#f59e0b", "white" };
            case "flying" -> new String[] { "#1e3a8a", "#93c5fd", "white" };
            case "psychic" -> new String[] { "#9d174d", "#f472b6", "white" };
            case "bug" -> new String[] { "#365314", "#a3e635", "white" };
            case "rock" -> new String[] { "#44403c", "#a8a29e", "white" };
            case "ghost" -> new String[] { "#312e81", "#818cf8", "white" };
            case "dragon" -> new String[] { "#172554", "#60a5fa", "white" };
            case "dark" -> new String[] { "#111827", "#4b5563", "white" };
            case "steel" -> new String[] { "#374151", "#9ca3af", "white" };
            case "fairy" -> new String[] { "#9d174d", "#f9a8d4", "white" };
            default -> new String[] { "#1f2937", "#334155", "white" };
        };
    }

    private String getPrimaryTypeLabel() {
        List<String> types = getTypes();
        return types.isEmpty() ? "-" : types.get(0);
    }

    private String getSecondaryTypeLabel() {
        List<String> types = getTypes();
        return types.size() >= 2 ? types.get(1) : "-";
    }

    private Node buildCatchTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(18));

        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.TOP_LEFT);

        VBox controlsCard = new VBox(14);
        controlsCard.setStyle(CARD_STYLE);
        controlsCard.setPrefWidth(430);

        Label controlsTitle = new Label("Catch Calculator");
        controlsTitle.setStyle(SECTION_TITLE_STYLE);

        ComboBox<String> ballBox = new ComboBox<>();
        ballBox.getItems().addAll(BALLS);
        ballBox.setValue("Ultra Ball");
        ballBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(STATUS_OPTIONS);
        statusBox.setValue("None");
        statusBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> hpBox = new ComboBox<>();
        hpBox.getItems().addAll(HP_OPTIONS);
        hpBox.setValue("1 HP");
        hpBox.setMaxWidth(Double.MAX_VALUE);

        TextField targetLevelField = new TextField("70");
        TextField yourLevelField = new TextField("70");

        controlsCard.getChildren().addAll(
                controlsTitle,
                buildInputRow("Ball", ballBox),
                buildInputRow("Status", statusBox),
                buildInputRow("HP State", hpBox),
                buildInputRow("Target Level", targetLevelField),
                buildInputRow("Your Level", yourLevelField)
        );

        VBox outputCard = new VBox(14);
        outputCard.setStyle(CARD_STYLE);
        HBox.setHgrow(outputCard, Priority.ALWAYS);

        Label outputTitle = new Label("Results");
        outputTitle.setStyle(SECTION_TITLE_STYLE);

        Label chanceLabel = new Label();
        chanceLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;"
        );

        ProgressBar chanceBar = new ProgressBar(0);
        chanceBar.setMaxWidth(Double.MAX_VALUE);
        chanceBar.setPrefHeight(22);

        Label fiftyLabel = new Label();
        fiftyLabel.setStyle(VALUE_STYLE);
        Label seventyFiveLabel = new Label();
        seventyFiveLabel.setStyle(VALUE_STYLE);
        Label ninetyLabel = new Label();
        ninetyLabel.setStyle(VALUE_STYLE);
        Label ninetyFiveLabel = new Label();
        ninetyFiveLabel.setStyle(VALUE_STYLE);
        Label noteLabel = new Label();
        noteLabel.setWrapText(true);
        noteLabel.setStyle(SMALL_MUTED_STYLE);

        outputCard.getChildren().addAll(
                outputTitle,
                chanceLabel,
                chanceBar,
                fiftyLabel,
                seventyFiveLabel,
                ninetyLabel,
                ninetyFiveLabel,
                noteLabel
        );

        topRow.getChildren().addAll(controlsCard, outputCard);

        VBox factsCard = new VBox(12);
        factsCard.setStyle(CARD_STYLE);

        Label factsTitle = new Label("Target Facts Used");
        factsTitle.setStyle(SECTION_TITLE_STYLE);

        factsCard.getChildren().addAll(
                factsTitle,
                createInfoRow("Name", getDisplayName()),
                createInfoRow("Base Catch Rate", getBaseCatchRateLabel()),
                createInfoRow("Types", joinList(getTypes())),
                createInfoRow("Weight", getWeightLabel()),
                createInfoRow("Level Ball Input", "Uses your level vs target level when selected"),
                createInfoRow("Formula", "Simplified universal Gen 3+ style catch calculation")
        );

        Runnable recalc = () -> {
            int catchRate = dexEntry != null ? dexEntry.captureRate : -1;
            int targetLevel = parseIntSafe(targetLevelField.getText(), 70);
            int yourLevel = parseIntSafe(yourLevelField.getText(), 70);
            String ball = safeString(ballBox.getValue());
            String status = safeString(statusBox.getValue());
            String hpState = safeString(hpBox.getValue());

            double chance = calculateCatchChance(catchRate, targetLevel, yourLevel, hpState, status, ball);
            chanceLabel.setText("Per-ball capture chance: " + PERCENT_FORMAT.format(chance * 100.0) + "%");
            chanceBar.setProgress(chance);
            chanceBar.setStyle("-fx-accent: " + getChanceBarColor(chance) + ";");

            fiftyLabel.setText("50% chance in " + ballsForTargetChance(chance, 0.50) + " balls");
            seventyFiveLabel.setText("75% chance in " + ballsForTargetChance(chance, 0.75) + " balls");
            ninetyLabel.setText("90% chance in " + ballsForTargetChance(chance, 0.90) + " balls");
            ninetyFiveLabel.setText("95% chance in " + ballsForTargetChance(chance, 0.95) + " balls");
            noteLabel.setText(getBallNote(ball, targetLevel, yourLevel));
        };

        ballBox.setOnAction(e -> recalc.run());
        statusBox.setOnAction(e -> recalc.run());
        hpBox.setOnAction(e -> recalc.run());
        targetLevelField.textProperty().addListener((obs, oldVal, newVal) -> recalc.run());
        yourLevelField.textProperty().addListener((obs, oldVal, newVal) -> recalc.run());
        recalc.run();

        content.getChildren().addAll(topRow, factsCard);

        return createTransparentScrollPane(content);
    }

    private HBox buildInputRow(String labelText, Node inputNode) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText + ":");
        label.setMinWidth(110);
        label.setStyle(LABEL_STYLE);

        HBox.setHgrow(inputNode, Priority.ALWAYS);
        row.getChildren().addAll(label, inputNode);
        return row;
    }

    private ScrollPane createTransparentScrollPane(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scrollPane;
    }

    private double calculateCatchChance(int catchRate, int targetLevel, int yourLevel, String hpState, String status, String ball) {
        if (catchRate < 0) {
            return 0.0;
        }

        if ("Master Ball".equals(ball)) {
            return 1.0;
        }

        double maxHp = 100.0;
        double currentHp = switch (hpState) {
            case "Half HP" -> 50.0;
            case "Red HP" -> 20.0;
            case "1 HP" -> 1.0;
            default -> 100.0;
        };

        double ballBonus = getBallBonus(ball, targetLevel, yourLevel);
        double statusBonus = getStatusBonus(status);

        double a = (((3.0 * maxHp - 2.0 * currentHp) * catchRate * ballBonus) / (3.0 * maxHp)) * statusBonus;

        if (a >= 255.0) {
            return 1.0;
        }

        if (a <= 0.0) {
            return 0.0;
        }

        double b = 1048560.0 / Math.sqrt(Math.sqrt(16711680.0 / a));
        double p = Math.pow(b / 65535.0, 4);

        if (Double.isNaN(p) || Double.isInfinite(p)) {
            return 0.0;
        }

        return Math.max(0.0, Math.min(1.0, p));
    }

    private double getStatusBonus(String status) {
        return switch (status) {
            case "Sleep", "Freeze" -> 2.5;
            case "Paralysis", "Burn", "Poison" -> 1.5;
            default -> 1.0;
        };
    }

    private double getBallBonus(String ball, int targetLevel, int yourLevel) {
        return switch (ball) {
            case "Great Ball" -> 1.5;
            case "Ultra Ball" -> 2.0;
            case "Quick Ball" -> 5.0;
            case "Dusk Ball" -> 3.0;
            case "Timer Ball" -> 4.0;
            case "Repeat Ball" -> 3.5;
            case "Net Ball" -> isBugOrWaterTarget() ? 3.5 : 1.0;
            case "Dive Ball" -> isWaterTarget() ? 3.5 : 1.0;
            case "Nest Ball" -> getNestBallBonus(targetLevel);
            case "Level Ball" -> getLevelBallBonus(targetLevel, yourLevel);
            default -> 1.0;
        };
    }

    private boolean isBugOrWaterTarget() {
        for (String type : getTypes()) {
            if ("Bug".equalsIgnoreCase(type) || "Water".equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWaterTarget() {
        for (String type : getTypes()) {
            if ("Water".equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    private double getNestBallBonus(int targetLevel) {
        if (targetLevel <= 0) {
            return 1.0;
        }
        if (targetLevel >= 30) {
            return 1.0;
        }
        return Math.max(1.0, (41.0 - targetLevel) / 10.0);
    }

    private double getLevelBallBonus(int targetLevel, int yourLevel) {
        if (targetLevel <= 0 || yourLevel <= 0) {
            return 1.0;
        }
        if (yourLevel >= targetLevel * 4) {
            return 8.0;
        }
        if (yourLevel >= targetLevel * 2) {
            return 4.0;
        }
        if (yourLevel > targetLevel) {
            return 2.0;
        }
        return 1.0;
    }

    private String getBallNote(String ball, int targetLevel, int yourLevel) {
        return switch (ball) {
            case "Quick Ball" -> "Assumes first-turn Quick Ball bonus.";
            case "Dusk Ball" -> "Assumes cave/night-style Dusk Ball conditions.";
            case "Timer Ball" -> "Uses Timer Ball's max late-battle bonus.";
            case "Repeat Ball" -> "Assumes the species has already been caught before.";
            case "Net Ball" -> isBugOrWaterTarget()
                    ? "Net Ball bonus applied because this target is Bug or Water type."
                    : "No Net Ball bonus applied because this target is not Bug/Water type.";
            case "Dive Ball" -> isWaterTarget()
                    ? "Dive Ball bonus approximated for Water-type targets."
                    : "No Dive Ball bonus applied for this target.";
            case "Nest Ball" -> "Nest Ball bonus scales from target level " + targetLevel + ".";
            case "Level Ball" -> "Level Ball bonus uses your level " + yourLevel + " vs target level " + targetLevel + ".";
            case "Master Ball" -> "Master Ball is treated as a guaranteed catch.";
            default -> "Uses a simplified universal catch formula with the selected status and HP state.";
        };
    }

    private String getChanceBarColor(double chance) {
        if (chance >= 0.75) {
            return "#22c55e";
        }
        if (chance >= 0.40) {
            return "#eab308";
        }
        if (chance >= 0.20) {
            return "#f97316";
        }
        return "#ef4444";
    }

    private int ballsForTargetChance(double perBallChance, double targetChance) {
        if (perBallChance >= 1.0) {
            return 1;
        }
        if (perBallChance <= 0.0) {
            return Integer.MAX_VALUE;
        }
        return (int) Math.ceil(Math.log(1.0 - targetChance) / Math.log(1.0 - perBallChance));
    }

    private void updateResponsiveLayout(double width) {
        if (mainContentRef == null || pokemonColumnRef == null || centerColumnRef == null || rightColumnRef == null) {
            return;
        }

        boolean hideRight = width < 1200;
        boolean compact = width < 900;

        setShown(rightColumnRef, !hideRight);

        if (compact) {
            mainContentRef.setSpacing(20);
            mainContentRef.setAlignment(Pos.TOP_CENTER);

            setWidthConstraints(pokemonColumnRef, 250, 280, 300);
            setWidthConstraints(centerColumnRef, 320, 420, 460);
            setWidthConstraints(counterCardRef, 260, 320, 380);

            setShown(huntDetailsCardRef, false);
            setShown(pokemonInfoCardRef, false);

            setSpriteSize(190);
            resetCountLabel.setStyle(COMPACT_COUNTER_VALUE_STYLE);
            return;
        }

        mainContentRef.setSpacing(26);
        mainContentRef.setAlignment(Pos.TOP_LEFT);

        setWidthConstraints(counterCardRef, 520, 640, Double.MAX_VALUE);
        setWidthConstraints(pokemonColumnRef, 320, 340, Double.MAX_VALUE);
        setWidthConstraints(centerColumnRef, 560, 640, Double.MAX_VALUE);

        setShown(huntDetailsCardRef, true);
        setShown(pokemonInfoCardRef, true);

        setSpriteSize(250);
        resetCountLabel.setStyle(COUNTER_VALUE_STYLE);
    }

    private VBox buildPokemonColumn() {
        VBox column = createCard(18, Pos.TOP_CENTER, CARD_STYLE);

        spritePaneRef = new StackPane();
        spritePaneRef.setMinHeight(320);
        spritePaneRef.setPrefHeight(340);
        spritePaneRef.setStyle(INNER_CARD_STYLE);

        spriteView.setPreserveRatio(true);
        setSpriteSize(250);
        spritePaneRef.getChildren().add(spriteView);

        pokemonInfoCardRef = createCard(10, Pos.TOP_LEFT, INNER_CARD_STYLE);
        pokemonInfoCardRef.getChildren().addAll(
                createSectionTitle("Pokémon Info"),
                createInfoRow("Name", safeString(hunt.getPokemonName())),
                createInfoRow("National Dex", "#" + hunt.getDexNumber()),
                createInfoRow("Introduced In", createGameValueNode(getIntroducedInGame())),
                createInfoRow("Generation", getGenerationLabel()),
                createInfoRow("Base Catch Rate", getBaseCatchRateLabel())
        );

        column.getChildren().addAll(spritePaneRef, pokemonInfoCardRef);
        return column;
    }

    private VBox buildCenterColumn() {
        VBox column = new VBox(24);
        column.setAlignment(Pos.TOP_LEFT);

        huntDetailsCardRef = createCard(16, Pos.TOP_LEFT, CARD_STYLE);
        huntDetailsCardRef.getChildren().addAll(
                createValueLabel(
                        "Hunt Details",
                        "-fx-text-fill: white;" +
                                "-fx-font-size: 20px;" +
                                "-fx-font-weight: bold;"
                ),
                createStatRow("Game", createGameValueNode(safeString(hunt.getGame()))),
                createStatRow("Hunt Method", safeString(hunt.getMethod())),
                createStatRow("Odds", oddsValueLabel),
                createStatRow("Shiny Charm", shinyCharmValueLabel),
                createStatRow("Estimated Time", estimatedTimeValueLabel)
        );

        counterCardRef = createCard(22, Pos.CENTER, CARD_STYLE);
        VBox.setVgrow(counterCardRef, Priority.ALWAYS);

        resetCountLabel.setStyle(COUNTER_VALUE_STYLE);

        HBox buttonRow = new HBox(14);
        buttonRow.setAlignment(Pos.CENTER);
        buildCounterButtons(buttonRow);

        completedCheckBox.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;"
        );
        completedCheckBox.setSelected(hunt.isCompleted());
        completedCheckBox.setOnAction(e -> configurePhaseCompletionState(completedCheckBox.isSelected()));

        counterCardRef.getChildren().addAll(
                createValueLabel(
                        "Reset Counter",
                        "-fx-text-fill: white;" +
                                "-fx-font-size: 20px;" +
                                "-fx-font-weight: bold;"
                ),
                resetCountLabel,
                buttonRow,
                completedCheckBox
        );

        column.getChildren().addAll(huntDetailsCardRef, counterCardRef);
        return column;
    }

    private void buildCounterButtons(HBox buttonRow) {
        Button minusOneButton = buildAdjustButton("-1", MINUS_BUTTON_STYLE, -1);
        Button plusOneButton = buildAdjustButton("+1", PLUS_BUTTON_STYLE, 1);

        buttonRow.getChildren().add(minusOneButton);

        if (isPlusMinusTenEnabled()) {
            Button minusTenButton = buildAdjustButton("-10", MINUS_BUTTON_STYLE, -10);
            buttonRow.getChildren().add(minusTenButton);
        }

        Button logPhaseButton = new Button("Log Phase");
        logPhaseButton.setMinWidth(138);
        logPhaseButton.setPrefHeight(54);
        logPhaseButton.setStyle(NEUTRAL_BUTTON_STYLE);
        logPhaseButton.setOnAction(e -> logPhaseShiny());
        buttonRow.getChildren().add(logPhaseButton);

        if (isPlusMinusTenEnabled()) {
            Button plusTenButton = buildAdjustButton("+10", PLUS_BUTTON_STYLE, 10);
            buttonRow.getChildren().add(plusTenButton);
        }

        buttonRow.getChildren().add(plusOneButton);
    }

    private Button buildAdjustButton(String text, String style, int delta) {
        Button button = new Button(text);
        button.setMinWidth(94);
        button.setPrefHeight(54);
        button.setStyle(style);
        button.setOnAction(e -> {
            hunt.setResetCount(hunt.getResetCount() + delta);
            onHuntUpdated();
        });
        return button;
    }

    private VBox buildRightColumn() {
        VBox column = createCard(14, Pos.TOP_LEFT, CARD_STYLE);

        phaseSummaryBox.setStyle(PHASE_CARD_STYLE);
        phaseListBox.setPadding(new Insets(4));
        milestoneList.setPadding(new Insets(4));

        phaseScrollPane.setFitToWidth(true);
        phaseScrollPane.setPannable(true);
        phaseScrollPane.setStyle(PHASE_SCROLL_STYLE);
        phaseScrollPane.setPrefViewportHeight(520);
        VBox.setVgrow(phaseScrollPane, Priority.ALWAYS);
        applyDarkScrollPaneTheme(phaseScrollPane);

        ScrollPane milestoneScrollPane = createPhaseScrollPane(milestoneList);
        applyDarkScrollPaneTheme(milestoneScrollPane);

        TitledPane titledPane = new TitledPane();
        titledPane.setText("Show Milestone Time Estimates");
        titledPane.setExpanded(false);
        titledPane.setCollapsible(true);
        titledPane.setContent(milestoneScrollPane);
        titledPane.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-background-color: #18181c;" +
                        "-fx-border-color: transparent;"
        );

        titledPane.expandedProperty().addListener((obs, oldValue, expanded) -> {
            setShown(phaseSummaryBox, !expanded);
            setShown(phaseScrollPane, !expanded);
        });

        column.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                column.applyCss();

                Node titleNode = titledPane.lookup(".title");
                if (titleNode != null) {
                    titleNode.setStyle(
                            "-fx-background-color: #23232a;" +
                                    "-fx-background-radius: 14px 14px 0 0;"
                    );
                }

                Node contentNode = titledPane.lookup(".content");
                if (contentNode != null) {
                    contentNode.setStyle(
                            "-fx-background-color: #18181c;" +
                                    "-fx-background-radius: 0 0 18px 18px;"
                    );
                }
            }
        });

        column.getChildren().addAll(createSectionTitle("Phases"), phaseSummaryBox, phaseScrollPane, titledPane);
        return column;
    }

    private HBox createStatRow(String labelText, String valueText) {
        Label valueLabel = new Label(valueText);
        valueLabel.setStyle(VALUE_STYLE);
        valueLabel.setWrapText(true);
        valueLabel.setMaxWidth(320);
        valueLabel.setAlignment(Pos.CENTER_RIGHT);
        return createStatRow(labelText, valueLabel);
    }

    private HBox createStatRow(String labelText, Node valueNode) {
        HBox row = new HBox(18);
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText + ":");
        label.setStyle(LABEL_STYLE);
        label.setMinWidth(170);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(label, spacer, valueNode);
        return row;
    }

    private HBox createInfoRow(String labelText, String valueText) {
        Label value = new Label(valueText);
        value.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        value.setWrapText(true);
        return createInfoRow(labelText, value);
    }

    private HBox createInfoRow(String labelText, Node valueNode) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText + ":");
        label.setStyle(
                "-fx-text-fill: #cfcfd6;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );
        label.setMinWidth(110);

        row.getChildren().addAll(label, valueNode);
        return row;
    }

    private TextFlow createGameValueNode(String displayName) {
        TextFlow flow = GameDisplayUtil.createStyledGameTextFromDisplayName(displayName);
        flow.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
        brightenDarkGameSegments(flow);
        return flow;
    }

    private void brightenDarkGameSegments(TextFlow flow) {
        for (Node child : flow.getChildren()) {
            if (child instanceof Text text) {
                Color color = (Color) text.getFill();
                if (color != null && color.getBrightness() < 0.38) {
                    text.setFill(Color.web("#d9d9d9"));
                }
                text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            }
        }
    }

    private void refresh() {
        updateSprite();
        resetCountLabel.setText(String.valueOf(hunt.getResetCount()));
        updateValueLabel(oddsValueLabel, "1 / " + ShinyOddsCalculator.getDisplayedOddsDenominator(hunt));
        updateValueLabel(shinyCharmValueLabel, hunt.isShinyCharmEnabled() ? "Yes" : "No");
        updateValueLabel(estimatedTimeValueLabel, TimeEstimateCalculator.getAverageTimeForAnyShiny(hunt));

        rebuildMilestones();
        rebuildPhaseSummary();
        rebuildPhaseList();
    }

    private void updateValueLabel(Label label, String value) {
        label.setText(value);
        label.setStyle(VALUE_STYLE);
    }

    private void updateSprite() {
        Image image = loadCurrentSprite();
        if (image != null) {
            spriteView.setImage(image);
        }
    }

    private Image loadCurrentSprite() {
        return SpriteResolver.loadSprite(
                ActiveHuntView.class,
                settings,
                hunt.getDexNumber(),
                shouldShowShinySprite()
        );
    }

    private boolean shouldShowShinySprite() {
        String mode = readSpriteMode();

        if ("always shiny".equals(mode)) {
            return true;
        }

        if ("always normal".equals(mode)) {
            return false;
        }

        return hunt.isCompleted();
    }

    private String readSpriteMode() {
        return invokeStringGetter(settings,
                "getSpriteMode",
                "getSelectedSpriteMode",
                "getSpritePreference",
                "getSpriteSetting")
                .replace('_', ' ')
                .trim()
                .toLowerCase();
    }

    private String readThemeName(Object target) {
        return invokeStringGetter(target,
                "getTheme",
                "getThemeName",
                "getCurrentTheme",
                "getSelectedTheme",
                "getAppearance",
                "getThemeMode",
                "getSelectedThemeName",
                "getThemeId")
                .replace('_', ' ')
                .replace('-', ' ')
                .trim()
                .toLowerCase();
    }

    private boolean isPlusMinusTenEnabled() {
        return invokeBooleanGetter(settings,
                "isShowPlusMinusTenButtons",
                "getShowPlusMinusTenButtons",
                "isShowPlusMinusTen",
                "getShowPlusMinusTen");
    }

    private String invokeStringGetter(Object target, String... methodNames) {
        if (target == null) {
            return "";
        }

        for (String methodName : methodNames) {
            try {
                Method method = target.getClass().getMethod(methodName);
                Object value = method.invoke(target);
                if (value != null) {
                    return String.valueOf(value);
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return "";
    }

    private boolean invokeBooleanGetter(Object target, String... methodNames) {
        if (target == null) {
            return false;
        }

        for (String methodName : methodNames) {
            try {
                Method method = target.getClass().getMethod(methodName);
                Object value = method.invoke(target);
                if (value instanceof Boolean booleanValue) {
                    return booleanValue;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return false;
    }

    private void rebuildMilestones() {
        milestoneList.getChildren().clear();
        milestoneList.setFillWidth(true);
        milestoneList.setStyle("-fx-background-color: #101014; -fx-background-radius: 18px;");

        Map<Double, String> milestones = new LinkedHashMap<>();
        milestones.put(0.01, "1%");
        milestones.put(0.10, "10%");
        milestones.put(0.25, "25%");
        milestones.put(0.50, "50%");
        milestones.put(0.632, "63.2%");
        milestones.put(0.80, "80%");
        milestones.put(0.90, "90%");
        milestones.put(0.99, "99%");

        boolean first = true;

        for (Map.Entry<Double, String> entry : milestones.entrySet()) {
            if (!first) {
                Separator separator = new Separator();
                separator.setStyle("-fx-background-color: #303038;");
                milestoneList.getChildren().add(separator);
            }

            double targetProbability = entry.getKey();
            String percentLabel = entry.getValue();

            int attempts = TimeEstimateCalculator.getMilestoneAttempts(hunt, targetProbability);
            String time = TimeEstimateCalculator.getMilestoneTime(hunt, targetProbability);

            VBox item = new VBox(6);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(10, 6, 10, 6));

            Label titleLabel = new Label("Probability Milestone: " + percentLabel);
            titleLabel.setStyle(MILESTONE_TITLE_STYLE);

            Label attemptsLabel = new Label("Estimated resets: " + attempts);
            attemptsLabel.setStyle(MILESTONE_VALUE_STYLE);

            Label timeLabel = new Label("Estimated time: " + time);
            timeLabel.setStyle(MILESTONE_VALUE_STYLE);

            item.getChildren().addAll(titleLabel, attemptsLabel, timeLabel);
            milestoneList.getChildren().add(item);

            first = false;
        }
    }

    private void rebuildPhaseSummary() {
        phaseSummaryBox.getChildren().clear();

        Label currentPhase = new Label("Current Phase: " + hunt.getResetCount());
        currentPhase.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");

        Label totalEncounters = new Label("Total Encounters: " + hunt.getTotalEncounters());
        totalEncounters.setStyle(VALUE_STYLE);

        Label loggedPhases = new Label("Logged Phases: " + hunt.getPhases().size());
        loggedPhases.setStyle(VALUE_STYLE);

        Label longestPhase = new Label("Longest Phase: " + getLongestPhaseEncounters());
        longestPhase.setStyle(VALUE_STYLE);

        phaseSummaryBox.getChildren().addAll(currentPhase, totalEncounters, loggedPhases, longestPhase);
    }

    private int getLongestPhaseEncounters() {
        int longest = 0;
        for (HuntPhase phase : hunt.getPhases()) {
            if (phase.getEncounters() > longest) {
                longest = phase.getEncounters();
            }
        }
        return longest;
    }

    private void rebuildPhaseList() {
        phaseListBox.getChildren().clear();
        phaseListBox.setFillWidth(true);

        List<HuntPhase> phases = hunt.getPhases();
        if (phases == null || phases.isEmpty()) {
            Label empty = new Label("No phases logged yet.");
            empty.setStyle("-fx-text-fill: #d4dbea; -fx-font-size: 15px;");
            phaseListBox.getChildren().add(empty);
            return;
        }

        for (HuntPhase phase : phases) {
            VBox card = new VBox(10);
            card.setStyle(PHASE_CARD_STYLE);

            String phaseTitleText = "Phase " + phase.getPhaseNumber();
            if (phase.isTargetPhase()) {
                phaseTitleText += " • Target";
            }

            Label phaseTitle = new Label(phaseTitleText);
            phaseTitle.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");

            Label shinyLabel = new Label("Shiny: " + safeString(phase.getShinyPokemonName()));
            shinyLabel.setStyle(VALUE_STYLE);

            Label encountersLabel = new Label("Encounters: " + phase.getEncounters());
            encountersLabel.setStyle(VALUE_STYLE);

            Label timestampLabel = new Label("Logged: " + formatPhaseTimestamp(phase.getTimestamp()));
            timestampLabel.setWrapText(true);
            timestampLabel.setStyle(VALUE_STYLE);

            Button editButton = new Button("Edit");
            editButton.setFocusTraversable(false);
            editButton.setStyle(
                    "-fx-background-color: #215f7a;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8px;" +
                            "-fx-padding: 6 12 6 12;" +
                            "-fx-cursor: hand;"
            );
            editButton.setOnAction(e -> editPhaseShiny(phase));

            HBox buttonRow = new HBox(editButton);
            buttonRow.setAlignment(Pos.CENTER_LEFT);

            card.getChildren().addAll(
                    phaseTitle,
                    shinyLabel,
                    encountersLabel,
                    timestampLabel,
                    buttonRow
            );

            phaseListBox.getChildren().add(card);
        }
    }

    private String formatPhaseTimestamp(String raw) {
        if (raw == null || raw.isBlank()) {
            return "-";
        }

        try {
            return LocalDateTime.parse(raw).format(PHASE_TIME_FORMAT);
        } catch (Exception ignored) {
            return raw;
        }
    }

    private void logPhaseShiny() {
        int currentPhaseEncounters = hunt.getResetCount();
        if (currentPhaseEncounters <= 0) {
            return;
        }

        HuntPhase phase = new HuntPhase(
                hunt.getCurrentPhaseNumber(),
                currentPhaseEncounters,
                "Unknown Phase Shiny",
                -1,
                false,
                LocalDateTime.now().toString()
        );

        hunt.getPhases().add(phase);
        hunt.setTotalEncounters(hunt.getTotalEncounters() + currentPhaseEncounters);
        hunt.setResetCount(0);

        onHuntUpdated();
    }

    private void editPhaseShiny(HuntPhase phase) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Phase Shiny");
        dialog.setHeaderText("Select the Pokémon for this phase.");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));
        content.setStyle("-fx-background-color: #1e1e1e;");

        Label searchLabel = new Label("Phase Shiny Pokémon");
        searchLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );

        ObservableList<PokemonEntry> allPokemon = FXCollections.observableArrayList();
        allPokemon.add(null);
        allPokemon.addAll(PokemonData.getAllPokemon());

        TextField pokemonSearchField = new TextField();
        pokemonSearchField.setPromptText("Search by name or dex number...");
        pokemonSearchField.setPrefWidth(360);
        pokemonSearchField.setStyle(
                "-fx-background-color: #2b2b2b;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #888888;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-border-color: #444444;" +
                        "-fx-padding: 8 10 8 10;"
        );

        ListView<PokemonEntry> pokemonListView = new ListView<>();
        pokemonListView.setItems(FXCollections.observableArrayList(allPokemon));
        pokemonListView.setPrefWidth(360);
        pokemonListView.setPrefHeight(260);
        pokemonListView.setStyle(
                "-fx-control-inner-background: #2b2b2b;" +
                        "-fx-background-color: #2b2b2b;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-border-color: #444444;"
        );

        pokemonListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(PokemonEntry item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    return;
                }

                if (item == null) {
                    setText("Unknown Phase Shiny");
                } else {
                    setText(item.getFormattedDisplayName());
                }

                if (isSelected()) {
                    setStyle(
                            "-fx-background-color: #2a3f5f;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-border-color: #4aa3ff;" +
                                    "-fx-border-width: 2px;" +
                                    "-fx-border-radius: 6px;" +
                                    "-fx-background-radius: 6px;" +
                                    "-fx-font-weight: bold;"
                    );
                } else {
                    setStyle(
                            "-fx-background-color: transparent;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-border-color: transparent;" +
                                    "-fx-border-width: 2px;" +
                                    "-fx-border-radius: 6px;" +
                                    "-fx-background-radius: 6px;"
                    );
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);

                if (isEmpty()) {
                    return;
                }

                if (selected) {
                    setStyle(
                            "-fx-background-color: #2a3f5f;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-border-color: #4aa3ff;" +
                                    "-fx-border-width: 2px;" +
                                    "-fx-border-radius: 6px;" +
                                    "-fx-background-radius: 6px;" +
                                    "-fx-font-weight: bold;"
                    );
                } else {
                    setStyle(
                            "-fx-background-color: transparent;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-border-color: transparent;" +
                                    "-fx-border-width: 2px;" +
                                    "-fx-border-radius: 6px;" +
                                    "-fx-background-radius: 6px;"
                    );
                }
            }
        });

        Runnable applyFilter = () -> {
            String query = pokemonSearchField.getText() == null
                    ? ""
                    : pokemonSearchField.getText().trim().toLowerCase();

            ObservableList<PokemonEntry> filtered = FXCollections.observableArrayList();
            filtered.add(null);

            for (PokemonEntry entry : PokemonData.getAllPokemon()) {
                if (entry == null) {
                    continue;
                }

                String formatted = entry.getFormattedDisplayName().toLowerCase();
                String display = entry.getDisplayName().toLowerCase();
                String dex = String.valueOf(entry.getDexNumber());

                if (query.isBlank()
                        || formatted.contains(query)
                        || display.contains(query)
                        || dex.contains(query)
                        || query.equals("#" + dex)) {
                    filtered.add(entry);
                }
            }

            PokemonEntry currentSelection = pokemonListView.getSelectionModel().getSelectedItem();
            pokemonListView.setItems(filtered);

            if (currentSelection != null && filtered.contains(currentSelection)) {
                pokemonListView.getSelectionModel().select(currentSelection);
                pokemonListView.scrollTo(currentSelection);
            } else if (currentSelection == null) {
                pokemonListView.getSelectionModel().select(null);
                pokemonListView.scrollTo(0);
            } else if (!filtered.isEmpty()) {
                pokemonListView.getSelectionModel().selectFirst();
                pokemonListView.scrollTo(0);
            }
        };

        pokemonSearchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilter.run());

        if (phase.getShinyDexNumber() > 0) {
            for (PokemonEntry entry : allPokemon) {
                if (entry != null && entry.getDexNumber() == phase.getShinyDexNumber()) {
                    pokemonListView.getSelectionModel().select(entry);
                    pokemonListView.scrollTo(entry);
                    pokemonSearchField.setText(entry.getFormattedDisplayName());
                    break;
                }
            }
        } else {
            pokemonListView.getSelectionModel().select(null);
        }

        pokemonListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                dialog.setResult(saveButtonType);
                dialog.close();
            }
        });

        content.getChildren().addAll(searchLabel, pokemonSearchField, pokemonListView);
        dialog.getDialogPane().setContent(content);

        Platform.runLater(() -> {
            pokemonSearchField.requestFocus();
            pokemonSearchField.selectAll();
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButtonType) {
            PokemonEntry selectedPokemon = pokemonListView.getSelectionModel().getSelectedItem();

            if (selectedPokemon == null) {
                phase.setShinyPokemonName("Unknown");
                phase.setShinyDexNumber(0);
            } else {
                phase.setShinyPokemonName(selectedPokemon.getDisplayName());
                phase.setShinyDexNumber(selectedPokemon.getDexNumber());
            }

            onHuntUpdated();
        }
    }

    private boolean hasTargetPhaseLogged() {
        List<HuntPhase> phases = hunt.getPhases();
        if (phases == null || phases.isEmpty()) {
            return false;
        }

        for (HuntPhase phase : phases) {
            if (phase.isTargetPhase()) {
                return true;
            }
        }

        return false;
    }

    private void onHuntUpdated() {
        refresh();
        huntStorage.saveHunt(hunt);
    }

    private String getDisplayName() {
        if (jsonEntry != null && !jsonEntry.englishName.isBlank()) {
            return jsonEntry.englishName;
        }
        return safeString(hunt.getPokemonName());
    }

    private String getJapaneseNameLabel() {
        if (jsonEntry != null && !jsonEntry.japaneseName.isBlank()) {
            return jsonEntry.japaneseName;
        }
        return "-";
    }

    private String getSpeciesLabel() {
        if (jsonEntry != null && !jsonEntry.species.isBlank()) {
            return jsonEntry.species;
        }
        return "Unknown species";
    }

    private String getDescriptionLabel() {
        if (jsonEntry != null && !jsonEntry.description.isBlank()) {
            return jsonEntry.description;
        }
        return "No description available.";
    }

    private String getIntroducedInGame() {
        if (dexEntry == null) {
            return "-";
        }

        return switch (dexEntry.generationId) {
            case 1 -> "Red / Blue";
            case 2 -> "Gold / Silver / Crystal";
            case 3 -> "Ruby / Sapphire / Emerald";
            case 4 -> "Diamond / Pearl / Platinum";
            case 5 -> "Black / White";
            case 6 -> "X / Y";
            case 7 -> "Sun / Moon";
            case 8 -> "Sword / Shield";
            case 9 -> "Scarlet / Violet";
            default -> "-";
        };
    }

    private String getGenerationLabel() {
        if (dexEntry == null || dexEntry.generationId <= 0) {
            return jsonEntry != null && !jsonEntry.generation.isBlank() ? jsonEntry.generation : "-";
        }
        return "Gen " + dexEntry.generationId;
    }

    private String getBaseCatchRateLabel() {
        if (dexEntry == null || dexEntry.captureRate < 0) {
            return "-";
        }
        return String.valueOf(dexEntry.captureRate);
    }

    private String getIdentifierLabel() {
        if (dexEntry != null && dexEntry.identifier != null && !dexEntry.identifier.isBlank()) {
            return dexEntry.identifier;
        }
        return "-";
    }

    private String getGenderLabel() {
        if (jsonEntry != null && !jsonEntry.gender.isBlank()) {
            return jsonEntry.gender;
        }
        if (dexEntry == null) {
            return "-";
        }
        if (dexEntry.genderRate < 0) {
            return "Genderless";
        }
        double female = dexEntry.genderRate * 12.5;
        double male = 100.0 - female;
        if (female == 0.0) {
            return "100% male";
        }
        if (male == 0.0) {
            return "100% female";
        }
        return PERCENT_FORMAT.format(male) + "% male, " + PERCENT_FORMAT.format(female) + "% female";
    }

    private String getEggGroupLabel() {
        if (jsonEntry != null && !jsonEntry.eggGroup.isBlank()) {
            return jsonEntry.eggGroup;
        }
        return "-";
    }

    private String getHatchCounterLabel() {
        if (dexEntry == null || dexEntry.hatchCounter < 0) {
            return "-";
        }
        return dexEntry.hatchCounter + " cycles";
    }

    private String getHeightLabel() {
        if (jsonEntry != null && !jsonEntry.height.isBlank()) {
            return jsonEntry.height;
        }
        return "-";
    }

    private String getWeightLabel() {
        if (jsonEntry != null && !jsonEntry.weight.isBlank()) {
            return jsonEntry.weight;
        }
        return "-";
    }

    private List<String> getTypes() {
        if (jsonEntry != null && !jsonEntry.types.isEmpty()) {
            return jsonEntry.types;
        }
        return List.of();
    }

    private List<String> getWeaknesses() {
        if (jsonEntry != null && !jsonEntry.weaknesses.isEmpty()) {
            return jsonEntry.weaknesses;
        }
        return List.of();
    }

    private List<String> getAbilities() {
        if (jsonEntry != null && !jsonEntry.abilities.isEmpty()) {
            return jsonEntry.abilities;
        }
        return List.of();
    }

    private String joinList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "-";
        }
        return String.join(", ", values);
    }

    private String booleanLabel(boolean value) {
        return value ? "Yes" : "No";
    }

    private PokemonDexEntry loadDexEntry(int dexNumber) {
        try (InputStream stream = getClass().getResourceAsStream("/national_dex.txt")) {
            if (stream == null) {
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String headerLine = reader.readLine();
                if (headerLine == null) {
                    return null;
                }

                String[] headers = headerLine.split(",");
                Map<String, Integer> headerIndexes = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    headerIndexes.put(headers[i].trim(), i);
                }

                Integer idIndex = headerIndexes.get("id");
                Integer generationIndex = headerIndexes.get("generation_id");
                Integer captureRateIndex = headerIndexes.get("capture_rate");
                Integer identifierIndex = headerIndexes.get("identifier");
                Integer genderRateIndex = headerIndexes.get("gender_rate");
                Integer hatchCounterIndex = headerIndexes.get("hatch_counter");
                Integer legendaryIndex = headerIndexes.get("is_legendary");
                Integer mythicalIndex = headerIndexes.get("is_mythical");

                if (idIndex == null || generationIndex == null || captureRateIndex == null) {
                    return null;
                }

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length <= Math.max(idIndex, Math.max(generationIndex, captureRateIndex))) {
                        continue;
                    }

                    int id = parseIntSafe(parts[idIndex], -1);
                    if (id == dexNumber) {
                        return new PokemonDexEntry(
                                id,
                                identifierIndex != null && identifierIndex < parts.length ? parts[identifierIndex] : "",
                                parseIntSafeSafe(parts, generationIndex, -1),
                                parseIntSafeSafe(parts, captureRateIndex, -1),
                                parseIntSafeSafe(parts, genderRateIndex, -1),
                                parseIntSafeSafe(parts, hatchCounterIndex, -1),
                                parseIntSafeSafe(parts, legendaryIndex, 0) == 1,
                                parseIntSafeSafe(parts, mythicalIndex, 0) == 1
                        );
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private int parseIntSafeSafe(String[] parts, Integer index, int fallback) {
        if (index == null || index < 0 || index >= parts.length) {
            return fallback;
        }
        return parseIntSafe(parts[index], fallback);
    }

    private PokemonJsonEntry loadJsonEntry(int dexNumber) {
        try (InputStream stream = getClass().getResourceAsStream("/pokemon/pokedex.json")) {
            if (stream == null) {
                return null;
            }

            StringBuilder jsonText = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonText.append(line);
                }
            }

            JsonArray array = JsonParser.parseReader(new StringReader(jsonText.toString())).getAsJsonArray();
            String expectedId = String.format("#%04d", dexNumber);

            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                String id = getString(obj, "id");
                if (!expectedId.equals(id)) {
                    continue;
                }

                PokemonJsonEntry entry = new PokemonJsonEntry();
                entry.id = id;
                entry.englishName = getString(obj.getAsJsonObject("name"), "english");
                entry.japaneseName = getString(obj.getAsJsonObject("name"), "japanese");
                entry.species = getString(obj, "species");
                entry.description = getString(obj, "description");
                entry.generation = getString(obj, "generation");

                JsonArray formData = obj.getAsJsonArray("formData");
                if (formData != null && !formData.isEmpty()) {
                    JsonObject firstForm = formData.get(0).getAsJsonObject();
                    entry.types = getStringList(firstForm.getAsJsonArray("type"));
                    entry.weaknesses = getStringList(firstForm.getAsJsonArray("weaknessTypes"));
                    entry.height = getString(firstForm, "height");
                    entry.weight = getString(firstForm, "weight");
                }

                JsonObject profile = obj.getAsJsonObject("profile");
                if (profile != null) {
                    entry.abilities = getStringList(profile.getAsJsonArray("ability"));
                    entry.eggGroup = getString(profile, "egg");
                    entry.gender = getString(profile, "gender");
                    entry.catchRateDisplay = getString(profile, "catchRate");
                }

                return entry;
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private String getString(JsonObject obj, String key) {
        if (obj == null || key == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return "";
        }
        try {
            return obj.get(key).getAsString();
        } catch (Exception ignored) {
            return "";
        }
    }

    private List<String> getStringList(JsonArray array) {
        List<String> values = new ArrayList<>();
        if (array == null) {
            return values;
        }
        for (JsonElement element : array) {
            try {
                values.add(element.getAsString());
            } catch (Exception ignored) {
            }
        }
        return values;
    }

    private int parseIntSafe(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private String safeString(String value) {
        return value == null || value.isBlank() ? "-" : value;
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

    private static final class PokemonDexEntry {
        private final int id;
        private final String identifier;
        private final int generationId;
        private final int captureRate;
        private final int genderRate;
        private final int hatchCounter;
        private final boolean legendary;
        private final boolean mythical;

        private PokemonDexEntry(
                int id,
                String identifier,
                int generationId,
                int captureRate,
                int genderRate,
                int hatchCounter,
                boolean legendary,
                boolean mythical
        ) {
            this.id = id;
            this.identifier = identifier;
            this.generationId = generationId;
            this.captureRate = captureRate;
            this.genderRate = genderRate;
            this.hatchCounter = hatchCounter;
            this.legendary = legendary;
            this.mythical = mythical;
        }
    }

    private static final class PokemonJsonEntry {
        private String id = "";
        private String englishName = "";
        private String japaneseName = "";
        private String species = "";
        private String description = "";
        private String generation = "";
        private List<String> types = List.of();
        private List<String> weaknesses = List.of();
        private String height = "";
        private String weight = "";
        private List<String> abilities = List.of();
        private String eggGroup = "";
        private String gender = "";
        private String catchRateDisplay = "";
    }

    private static final class ThemePalette {
        private final String selectedTabBackground;
        private final String selectedTabBorder;
        private final String unselectedTabBackground;
        private final String unselectedTabBorder;

        private ThemePalette(
                String selectedTabBackground,
                String selectedTabBorder,
                String unselectedTabBackground,
                String unselectedTabBorder
        ) {
            this.selectedTabBackground = selectedTabBackground;
            this.selectedTabBorder = selectedTabBorder;
            this.unselectedTabBackground = unselectedTabBackground;
            this.unselectedTabBorder = unselectedTabBorder;
        }

        private static ThemePalette fromThemeName(String rawTheme) {
            String normalized = rawTheme == null ? "" : rawTheme.trim().toLowerCase();

            return switch (normalized) {
                case "oled" -> new ThemePalette("#000000", "#1f2937", "#111111", "#1f2937");
                case "slate" -> new ThemePalette("#18181c", "#2f2f36", "#20232b", "#2f3440");
                default -> new ThemePalette("#18181c", "#2f2f36", "#20232b", "#2f3440");
            };
        }
    }
}
