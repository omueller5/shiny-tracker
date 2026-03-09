package net.owen.shinytracker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

import java.util.ArrayList;
import java.util.List;

public class OptionsView extends VBox {

    private static final String PAGE_STYLE =
            "-fx-background-color: transparent;";

    private static final String CARD_STYLE =
            "-fx-background-color: #18181c;" +
                    "-fx-background-radius: 22px;" +
                    "-fx-border-radius: 22px;" +
                    "-fx-border-color: #2f2f36;" +
                    "-fx-border-width: 1px;" +
                    "-fx-padding: 22;";

    private static final String BUTTON_STYLE =
            "-fx-background-color: #242424;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 10px;" +
                    "-fx-border-radius: 10px;" +
                    "-fx-border-color: #3b3b3b;" +
                    "-fx-padding: 10 16 10 16;" +
                    "-fx-cursor: hand;";

    private static final String TITLE_STYLE =
            "-fx-text-fill: white;" +
                    "-fx-font-size: 28px;" +
                    "-fx-font-weight: bold;";

    private static final String SECTION_TITLE_STYLE =
            "-fx-text-fill: white;" +
                    "-fx-font-size: 20px;" +
                    "-fx-font-weight: bold;";

    private static final String LABEL_STYLE =
            "-fx-text-fill: #d6d6dc;" +
                    "-fx-font-size: 15px;" +
                    "-fx-font-weight: bold;";

    private static final String DESC_STYLE =
            "-fx-text-fill: #c7c7cf;" +
                    "-fx-font-size: 13px;";

    private final AppNavigator navigator;
    private final AppSettings settings;
    private final KeybindManager keybindManager;

    public OptionsView(AppNavigator navigator) {
        this.navigator = navigator;
        this.settings = SettingsStorage.loadSettings();
        this.keybindManager = new KeybindManager();
        this.keybindManager.loadFromSettings(settings);

        setSpacing(18);
        setPadding(new Insets(20));
        setStyle(PAGE_STYLE);

        VBox content = new VBox(18);
        content.getChildren().addAll(
                buildTopBar(),
                buildMainCard(),
                buildKeybindCard()
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;"
        );

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        getChildren().add(scrollPane);
    }

    public static OptionsView build(AppNavigator navigator) {
        return new OptionsView(navigator);
    }

    private HBox buildTopBar() {
        HBox topBar = new HBox(18);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 110, 6, 0));

        Button backButton = new Button("← Back");
        backButton.setStyle(BUTTON_STYLE);
        backButton.setOnAction(e -> navigator.showDashboard());

        Label pageTitle = new Label("Options");
        pageTitle.setStyle(TITLE_STYLE);

        topBar.getChildren().addAll(backButton, pageTitle);
        return topBar;
    }

    private VBox buildMainCard() {
        VBox card = new VBox(22);
        card.setStyle(CARD_STYLE);

        Label title = new Label("Tracker Settings");
        title.setStyle(SECTION_TITLE_STYLE);

        CheckBox plusMinusTenCheckBox = new CheckBox("Show +/-10 buttons on Active Hunt");
        plusMinusTenCheckBox.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");
        plusMinusTenCheckBox.setSelected(settings.isShowPlusMinusTenButtons());

        ComboBox<String> themeBox = new ComboBox<>();
        themeBox.getItems().addAll("Dark", "Slate", "OLED");
        themeBox.setValue(switch (settings.getThemeId()) {
            case "slate" -> "Slate";
            case "oled" -> "OLED";
            default -> "Dark";
        });
        themeBox.setPrefWidth(240);

        ComboBox<String> spriteModeBox = new ComboBox<>();
        spriteModeBox.getItems().addAll("Default", "Always Shiny", "Always Normal");
        spriteModeBox.setValue(switch (settings.getSpriteMode()) {
            case "always_shiny" -> "Always Shiny";
            case "always_normal" -> "Always Normal";
            default -> "Default";
        });
        spriteModeBox.setPrefWidth(240);

        ComboBox<String> spriteStyleBox = new ComboBox<>();
        spriteStyleBox.getItems().addAll(
                "Default",
                "Generation II",
                "Generation III",
                "Generation IV",
                "Generation V",
                "Generation VI",
                "Generation VII",
                "3D Sprites"
        );
        spriteStyleBox.setValue(switch (settings.getSpritePackMode()) {
            case "gen2" -> "Generation II";
            case "gen3" -> "Generation III";
            case "gen4" -> "Generation IV";
            case "gen5" -> "Generation V";
            case "gen6" -> "Generation VI";
            case "gen7" -> "Generation VII";
            case "sprites3d" -> "3D Sprites";
            default -> "Default";
        });
        spriteStyleBox.setPrefWidth(240);

        Button saveButton = new Button("Save Options");
        saveButton.setStyle(
                "-fx-background-color: #2f6f40;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );
        saveButton.setOnAction(e -> {
            settings.setShowPlusMinusTenButtons(plusMinusTenCheckBox.isSelected());
            settings.setThemeId(mapThemeSelection(themeBox.getValue()));
            settings.setSpriteMode(mapSpriteModeSelection(spriteModeBox.getValue()));
            settings.setSpritePackMode(mapSpritePackSelection(spriteStyleBox.getValue()));
            keybindManager.saveToSettings(settings);

            SettingsStorage.saveSettings(settings);
            navigator.showDashboard();
        });

        VBox rows = new VBox(18,
                buildOptionRow(
                        "Active Hunt Buttons",
                        "Turn the extra -10 and +10 buttons on or off.",
                        plusMinusTenCheckBox
                ),
                buildOptionRow(
                        "Theme",
                        "Choose the overall app background style.",
                        themeBox
                ),
                buildOptionRow(
                        "Sprite Mode",
                        "Default uses normal sprites until the hunt is completed. The other two are global overrides.",
                        spriteModeBox
                ),
                buildOptionRow(
                        "Sprite Style",
                        "Default keeps the current sprite behavior. Generation styles use stable per-Pokémon folder selection within that generation. If a Pokémon does not exist in that generation, it falls back to the default sprite. 3D Sprites uses the full HOME-style 3D set.",
                        spriteStyleBox
                )
        );

        card.getChildren().addAll(title, rows, saveButton);
        return card;
    }

    private VBox buildKeybindCard() {
        VBox card = new VBox(18);
        card.setStyle(CARD_STYLE);

        Label title = new Label("Keybinds");
        title.setStyle(SECTION_TITLE_STYLE);

        Label description = new Label("Click a binding, then press a new key or key combination. Press Escape while capturing to cancel.");
        description.setWrapText(true);
        description.setStyle(DESC_STYLE);

        VBox rows = new VBox(12);
        for (KeybindAction action : KeybindAction.values()) {
            rows.getChildren().add(buildKeybindRow(action));
        }

        Button resetButton = new Button("Reset Keybinds to Default");
        resetButton.setStyle(BUTTON_STYLE);
        resetButton.setOnAction(e -> {
            keybindManager.loadDefaultBindings();
            getChildren().setAll(
                    buildTopBar(),
                    buildMainCard(),
                    buildKeybindCard()
            );
        });

        card.getChildren().addAll(title, description, rows, resetButton);
        return card;
    }

    private HBox buildKeybindRow(KeybindAction action) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
                "-fx-background-color: #101014;" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-radius: 16px;" +
                        "-fx-border-color: #26262d;" +
                        "-fx-padding: 14 16 14 16;"
        );

        VBox textBox = new VBox(4);
        Label name = new Label(action.getDisplayName());
        name.setStyle(LABEL_STYLE);

        Label help = new Label("Current: " + keybindManager.getBindingDisplay(action));
        help.setStyle(DESC_STYLE);

        textBox.getChildren().addAll(name, help);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button captureButton = new Button(keybindManager.getBindingDisplay(action));
        captureButton.setStyle(BUTTON_STYLE);
        captureButton.setMinWidth(180);

        captureButton.setOnAction(e -> {
            captureButton.setText("Press a key...");
            captureButton.requestFocus();
        });

        captureButton.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (!"Press a key...".equals(captureButton.getText())) {
                return;
            }

            if (e.getCode() == KeyCode.ESCAPE) {
                captureButton.setText(keybindManager.getBindingDisplay(action));
                help.setText("Current: " + keybindManager.getBindingDisplay(action));
                e.consume();
                return;
            }

            if (isModifierOnly(e.getCode())) {
                e.consume();
                return;
            }

            KeyCombination combo = buildCombination(e);
            keybindManager.setBinding(action, combo);

            String display = keybindManager.getBindingDisplay(action);
            captureButton.setText(display);
            help.setText("Current: " + display);
            e.consume();
        });

        row.getChildren().addAll(textBox, spacer, captureButton);
        return row;
    }

    private KeyCombination buildCombination(KeyEvent event) {
        List<KeyCombination.Modifier> modifiers = new ArrayList<>();

        if (event.isControlDown()) {
            modifiers.add(KeyCombination.CONTROL_DOWN);
        }
        if (event.isAltDown()) {
            modifiers.add(KeyCombination.ALT_DOWN);
        }
        if (event.isShiftDown()) {
            modifiers.add(KeyCombination.SHIFT_DOWN);
        }
        if (event.isMetaDown()) {
            modifiers.add(KeyCombination.META_DOWN);
        }
        if (event.isShortcutDown()) {
            modifiers.add(KeyCombination.SHORTCUT_DOWN);
        }

        return new KeyCodeCombination(event.getCode(), modifiers.toArray(new KeyCombination.Modifier[0]));
    }

    private boolean isModifierOnly(KeyCode code) {
        return code == KeyCode.SHIFT
                || code == KeyCode.CONTROL
                || code == KeyCode.ALT
                || code == KeyCode.META
                || code == KeyCode.SHORTCUT;
    }

    private VBox buildOptionRow(String titleText, String descriptionText, Node control) {
        VBox row = new VBox(8);

        Label title = new Label(titleText);
        title.setStyle(LABEL_STYLE);

        Label description = new Label(descriptionText);
        description.setWrapText(true);
        description.setStyle(DESC_STYLE);

        HBox controlRow = new HBox();
        controlRow.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        controlRow.getChildren().addAll(control, spacer);

        row.getChildren().addAll(title, description, controlRow);
        row.setStyle(
                "-fx-background-color: #101014;" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-radius: 16px;" +
                        "-fx-border-color: #26262d;" +
                        "-fx-padding: 16;"
        );

        return row;
    }

    private String mapThemeSelection(String value) {
        if ("Slate".equalsIgnoreCase(value)) {
            return "slate";
        }
        if ("OLED".equalsIgnoreCase(value)) {
            return "oled";
        }
        return "dark";
    }

    private String mapSpriteModeSelection(String value) {
        if ("Always Shiny".equalsIgnoreCase(value)) {
            return "always_shiny";
        }
        if ("Always Normal".equalsIgnoreCase(value)) {
            return "always_normal";
        }
        return "default";
    }

    private String mapSpritePackSelection(String value) {
        if ("Generation II".equalsIgnoreCase(value)) {
            return "gen2";
        }
        if ("Generation III".equalsIgnoreCase(value)) {
            return "gen3";
        }
        if ("Generation IV".equalsIgnoreCase(value)) {
            return "gen4";
        }
        if ("Generation V".equalsIgnoreCase(value)) {
            return "gen5";
        }
        if ("Generation VI".equalsIgnoreCase(value)) {
            return "gen6";
        }
        if ("Generation VII".equalsIgnoreCase(value)) {
            return "gen7";
        }
        if ("3D Sprites".equalsIgnoreCase(value)) {
            return "sprites3d";
        }
        return "default";
    }
}
