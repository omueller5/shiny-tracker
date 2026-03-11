package net.owen.shinytracker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.scene.image.Image;
import java.net.URL;

import java.lang.reflect.Method;
import java.util.List;

public final class DashboardView {

    private DashboardView() {
    }

    public static ScrollPane build(List<Hunt> hunts, AppNavigator navigator) {
        VBox outerContainer = new VBox(18);
        outerContainer.setPadding(new Insets(28));
        outerContainer.setAlignment(Pos.TOP_LEFT);

        Label header = new Label("Dashboard");
        header.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #2a2a2a;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 10 16 10 16;"
        );

        VBox topControls = new VBox(12);
        topControls.setAlignment(Pos.TOP_LEFT);

        boolean hasHunts = hunts != null && !hunts.isEmpty();

        HBox primaryActions = new HBox(12);
        primaryActions.setAlignment(Pos.CENTER_LEFT);

        Button selectionModeButton = new Button(
                navigator.isSelectionMode() ? "Exit Selection Mode" : "Select Hunts"
        );
        UiStyles.styleSecondaryButton(selectionModeButton);
        selectionModeButton.setOnAction(e -> navigator.toggleSelectionMode());

        Button importButton = new Button("Import Hunts");
        UiStyles.styleSecondaryButton(importButton);
        importButton.setOnAction(e -> invokeNavigatorNoArg(navigator, "importHunts"));

        Button exportAllButton = new Button("Export All Hunts");
        UiStyles.styleSecondaryButton(exportAllButton);
        exportAllButton.setOnAction(e -> invokeNavigatorNoArg(navigator, "exportAllHunts"));

        primaryActions.getChildren().addAll(selectionModeButton, importButton, exportAllButton);

        if (hasHunts) {
            Button newHuntButton = new Button("+ New Hunt");
            UiStyles.stylePrimaryButton(newHuntButton);
            newHuntButton.setOnAction(e -> navigator.showCreateHunt());
            primaryActions.getChildren().add(newHuntButton);
        }

        topControls.getChildren().add(primaryActions);

        if (navigator.isSelectionMode()) {
            HBox selectionActions = new HBox(12);
            selectionActions.setAlignment(Pos.CENTER_LEFT);

            Button deleteSelectedButton = new Button("Delete Selected Hunts");
            UiStyles.stylePrimaryButton(deleteSelectedButton);
            deleteSelectedButton.setOnAction(e -> navigator.deleteSelectedHunts());

            Button exportSelectedButton = new Button("Export Selected Hunts");
            UiStyles.styleSecondaryButton(exportSelectedButton);
            exportSelectedButton.setOnAction(e -> invokeNavigatorNoArg(navigator, "exportSelectedHunts"));

            selectionActions.getChildren().addAll(deleteSelectedButton, exportSelectedButton);
            topControls.getChildren().add(selectionActions);
        }

        FlowPane cardGrid = new FlowPane();
        cardGrid.setHgap(18);
        cardGrid.setVgap(18);
        cardGrid.setPrefWrapLength(1200);
        cardGrid.setAlignment(Pos.TOP_LEFT);

        for (Hunt hunt : hunts) {
            cardGrid.getChildren().add(buildHuntCard(hunt, navigator));
        }

        if (!hasHunts) {
            cardGrid.getChildren().add(buildNewHuntCard(navigator));
        }

        outerContainer.getChildren().addAll(header, topControls, cardGrid);

        ScrollPane scrollPane = new ScrollPane(outerContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;"
        );

        outerContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));

        scrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            updateResponsiveCardLayout(cardGrid, newBounds.getWidth());
        });

        return scrollPane;
    }

    private static void updateResponsiveCardLayout(FlowPane cardGrid, double width) {
        double usableWidth = Math.max(700, width - 56);

        int columns;
        if (usableWidth >= 1350) {
            columns = 5;
        } else if (usableWidth >= 1080) {
            columns = 4;
        } else if (usableWidth >= 820) {
            columns = 3;
        } else if (usableWidth >= 560) {
            columns = 2;
        } else {
            columns = 1;
        }

        double gap = 18;
        double totalGap = gap * (columns - 1);
        double cardWidth = (usableWidth - totalGap) / columns;
        cardWidth = clamp(cardWidth, 190, 250);

        double cardHeight = clamp(cardWidth * 1.38, 275, 340);

        cardGrid.setPrefWrapLength(usableWidth);
        cardGrid.setHgap(gap);
        cardGrid.setVgap(gap);

        for (javafx.scene.Node node : cardGrid.getChildren()) {
            if (node instanceof StackPane wrapper) {
                applyResponsiveCardSize(wrapper, cardWidth, cardHeight);
            } else if (node instanceof VBox newHuntCard) {
                applyResponsiveCardSize(newHuntCard, cardWidth, cardHeight);
            }
        }
    }

    private static void applyResponsiveCardSize(Region cardNode, double width, double height) {
        cardNode.setMinSize(width, height);
        cardNode.setPrefSize(width, height);
        cardNode.setMaxSize(width, height);

        if (cardNode instanceof StackPane wrapper && !wrapper.getChildren().isEmpty()) {
            javafx.scene.Node inner = wrapper.getChildren().get(0);
            if (inner instanceof VBox card) {
                card.setMinSize(width, height);
                card.setPrefSize(width, height);
                card.setMaxSize(width, height);

                double fontSize = width >= 235 ? 24 : width >= 215 ? 21 : 18;
                double plusFontSize = width >= 235 ? 56 : width >= 215 ? 48 : 42;

                for (javafx.scene.Node child : card.getChildren()) {
                    if (child instanceof Label label) {
                        String text = label.getText();
                        if (text == null) {
                            continue;
                        }

                        if (text.equals("+")) {
                            label.setStyle(
                                    "-fx-text-fill: white;" +
                                            "-fx-font-size: " + plusFontSize + "px;" +
                                            "-fx-font-weight: bold;"
                            );
                        } else if (text.equals("Start a New Hunt")) {
                            label.setStyle(
                                    "-fx-text-fill: white;" +
                                            "-fx-font-size: " + Math.max(18, fontSize - 2) + "px;" +
                                            "-fx-font-weight: bold;"
                            );
                        } else if (text.equals("DONE!")) {
                            label.setStyle(
                                    "-fx-text-fill: #6ee16e;" +
                                            "-fx-font-size: " + Math.max(15, fontSize - 5) + "px;" +
                                            "-fx-font-weight: bold;"
                            );
                        }
                    }
                }
            }
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static StackPane buildHuntCard(Hunt hunt, AppNavigator navigator) {
        VBox card = new VBox(12);
        card.setPrefSize(230, 320);
        card.setMinSize(230, 320);
        card.setMaxSize(230, 320);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18));
        card.setStyle(buildSelectableCardStyle(false, navigator.isHuntSelected(hunt)));

        StackPane spritePlaceholder = new StackPane();
        spritePlaceholder.setPrefSize(150, 120);
        spritePlaceholder.setMinSize(150, 120);
        spritePlaceholder.setMaxSize(150, 120);
        spritePlaceholder.setStyle(
                "-fx-background-color: #1b1b1b;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #333333;" +
                        "-fx-border-radius: 12px;"
        );

        Image sprite = SpriteResolver.loadSprite(
                DashboardView.class,
                SettingsStorage.loadSettings(),
                hunt.getDexNumber(),
                shouldShowShinySprite(hunt)
        );

        ImageView spriteView = new ImageView(sprite);
        spriteView.setPreserveRatio(true);
        spriteView.setFitWidth(120);
        spriteView.setFitHeight(100);

        Image spriteImage = loadDashboardSprite(hunt);
        if (spriteImage != null) {
            spriteView.setImage(spriteImage);
        }

        spritePlaceholder.getChildren().add(spriteView);

        Label nameLabel = new Label(hunt.getPokemonName());
        nameLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );

        TextFlow gameLabel = GameDisplayUtil.createStyledGameTextFromDisplayName(hunt.getGame());
        gameLabel.setMaxWidth(Double.MAX_VALUE);
        gameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        HuntMethod method = HuntMethod.fromDisplayName(hunt.getMethod());

        Label resetLabel = new Label(
                hunt.getResetCount() + " " + method.getShortName()
        );
        resetLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;"
        );

        Label doneLabel = new Label(hunt.isCompleted() ? "DONE!" : "");
        doneLabel.setStyle(
                "-fx-text-fill: #6ee16e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );
        doneLabel.setAlignment(Pos.CENTER);
        doneLabel.setMaxWidth(Double.MAX_VALUE);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(
                spritePlaceholder,
                nameLabel,
                gameLabel,
                resetLabel,
                spacer,
                doneLabel
        );

        StackPane wrapper = new StackPane(card);
        wrapper.setPrefSize(230, 320);
        wrapper.setMinSize(230, 320);
        wrapper.setMaxSize(230, 320);

        if (navigator.isSelectionMode()) {
            Label selectedBadge = new Label(navigator.isHuntSelected(hunt) ? "SELECTED" : "");
            selectedBadge.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: #2f5f2f;" +
                            "-fx-background-radius: 8px;" +
                            "-fx-padding: 4 8 4 8;"
            );

            StackPane.setAlignment(selectedBadge, Pos.TOP_LEFT);
            StackPane.setMargin(selectedBadge, new Insets(8));
            wrapper.getChildren().add(selectedBadge);
        } else {
            Button exportButton = new Button("Export");
            exportButton.setFocusTraversable(false);
            exportButton.setStyle(
                    "-fx-background-color: #215f7a;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 11px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8px;" +
                            "-fx-padding: 4 10 4 10;" +
                            "-fx-cursor: hand;"
            );
            exportButton.setOnAction(e -> invokeNavigatorWithHunt(navigator, "exportHunt", hunt));

            Button deleteButton = new Button("X");
            deleteButton.setFocusTraversable(false);
            deleteButton.setStyle(
                    "-fx-background-color: #7a1f1f;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8px;" +
                            "-fx-padding: 4 8 4 8;" +
                            "-fx-cursor: hand;"
            );
            deleteButton.setOnAction(e -> navigator.deleteHunt(hunt));

            StackPane.setAlignment(exportButton, Pos.TOP_LEFT);
            StackPane.setMargin(exportButton, new Insets(8));
            StackPane.setAlignment(deleteButton, Pos.TOP_RIGHT);
            StackPane.setMargin(deleteButton, new Insets(8));
            wrapper.getChildren().addAll(exportButton, deleteButton);
        }

        card.setOnMouseEntered(e -> card.setStyle(buildSelectableCardStyle(true, navigator.isHuntSelected(hunt))));
        card.setOnMouseExited(e -> card.setStyle(buildSelectableCardStyle(false, navigator.isHuntSelected(hunt))));
        card.setOnMouseClicked(e -> {
            if (navigator.isSelectionMode()) {
                navigator.toggleHuntSelection(hunt);
            } else {
                navigator.showActiveHunt(hunt);
            }
        });

        return wrapper;
    }

    private static VBox buildNewHuntCard(AppNavigator navigator) {
        VBox card = new VBox(18);
        card.setPrefSize(230, 320);
        card.setMinSize(230, 320);
        card.setMaxSize(230, 320);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18));
        card.setStyle(UiStyles.buildCardStyle(false));

        Label plusLabel = new Label("+");
        plusLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 56px;" +
                        "-fx-font-weight: bold;"
        );

        Label textLabel = new Label("Start a New Hunt");
        textLabel.setWrapText(true);
        textLabel.setAlignment(Pos.CENTER);
        textLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );

        card.getChildren().addAll(plusLabel, textLabel);

        card.setOnMouseEntered(e -> card.setStyle(UiStyles.buildCardStyle(true)));
        card.setOnMouseExited(e -> card.setStyle(UiStyles.buildCardStyle(false)));
        card.setOnMouseClicked(e -> navigator.showCreateHunt());

        return card;
    }

    private static void invokeNavigatorNoArg(AppNavigator navigator, String methodName) {
        if (navigator == null || methodName == null || methodName.isBlank()) {
            return;
        }

        try {
            Method method = navigator.getClass().getMethod(methodName);
            method.invoke(navigator);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private static void invokeNavigatorWithHunt(AppNavigator navigator, String methodName, Hunt hunt) {
        if (navigator == null || methodName == null || methodName.isBlank()) {
            return;
        }

        try {
            Method method = navigator.getClass().getMethod(methodName, Hunt.class);
            method.invoke(navigator, hunt);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private static boolean shouldShowShinySprite(Hunt hunt) {
        AppSettings settings = SettingsStorage.loadSettings();
        String mode = readSpriteMode(settings);

        if ("always shiny".equals(mode)) {
            return true;
        }

        if ("always normal".equals(mode)) {
            return false;
        }

        return hunt.isCompleted();
    }

    private static Image loadDashboardSprite(Hunt hunt) {
        int dex = hunt.getDexNumber();
        boolean shiny = shouldShowShinySprite(hunt);

        AppSettings settings = SettingsStorage.loadSettings();
        String spritePackMode = readSpritePackMode(settings);

        String[] paths;

        switch (spritePackMode) {
            case "sprites3d" -> paths = buildThreeDSpritePaths(dex, shiny);
            case "gen2", "gen3", "gen4", "gen5", "gen6", "gen7" -> {
                paths = buildGenerationSpritePaths(spritePackMode, dex, shiny);

                Image generationImage = loadImageFromPaths(paths);
                if (generationImage != null) {
                    return generationImage;
                }

                paths = buildDefaultSpritePaths(dex, shiny);
            }
            default -> paths = buildDefaultSpritePaths(dex, shiny);
        }

        return loadImageFromPaths(paths);
    }

    private static String readSpritePackMode(Object settings) {
        if (settings == null) {
            return "";
        }

        String[] methodNames = {
                "getSpritePackMode",
                "getSelectedSpritePackMode",
                "getSpritePackSetting",
                "getSpriteStyle"
        };

        for (String methodName : methodNames) {
            try {
                Method method = settings.getClass().getMethod(methodName);
                Object value = method.invoke(settings);
                if (value != null) {
                    return String.valueOf(value)
                            .replace('_', ' ')
                            .trim()
                            .toLowerCase();
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return "";
    }

    private static Image loadImageFromPaths(String[] paths) {
        for (String path : paths) {
            URL url = DashboardView.class.getResource(path);
            if (url != null) {
                return new Image(url.toExternalForm(), true);
            }
        }
        return null;
    }

    private static String[] buildDefaultSpritePaths(int dex, boolean shiny) {
        if (shiny) {
            return new String[] {
                    "/pokemon/sprites/pokemon/shiny/" + dex + ".png",
                    "pokemon/sprites/pokemon/shiny/" + dex + ".png",
                    "/pokemon/sprites/pokemon/" + dex + ".png",
                    "pokemon/sprites/pokemon/" + dex + ".png"
            };
        }

        return new String[] {
                "/pokemon/sprites/pokemon/" + dex + ".png",
                "pokemon/sprites/pokemon/" + dex + ".png",
                "/pokemon/sprites/pokemon/shiny/" + dex + ".png",
                "pokemon/sprites/pokemon/shiny/" + dex + ".png"
        };
    }

    private static String[] buildThreeDSpritePaths(int dex, boolean shiny) {
        if (shiny) {
            return new String[] {
                    "/pokemon/sprites/pokemon/other/home/shiny/" + dex + ".png",
                    "pokemon/sprites/pokemon/other/home/shiny/" + dex + ".png",
                    "/pokemon/sprites/pokemon/other/home/" + dex + ".png",
                    "pokemon/sprites/pokemon/other/home/" + dex + ".png"
            };
        }

        return new String[] {
                "/pokemon/sprites/pokemon/other/home/" + dex + ".png",
                "pokemon/sprites/pokemon/other/home/" + dex + ".png",
                "/pokemon/sprites/pokemon/other/home/shiny/" + dex + ".png",
                "pokemon/sprites/pokemon/other/home/shiny/" + dex + ".png"
        };
    }

    private static String[] buildGenerationSpritePaths(String spritePackMode, int dex, boolean shiny) {
        String baseFolder = resolveGenerationBaseFolder(spritePackMode, dex);
        if (baseFolder == null || baseFolder.isBlank()) {
            return new String[0];
        }

        if (shiny) {
            return new String[] {
                    baseFolder + "/shiny/" + dex + ".png",
                    trimLeadingSlash(baseFolder) + "/shiny/" + dex + ".png",
                    baseFolder + "/" + dex + ".png",
                    trimLeadingSlash(baseFolder) + "/" + dex + ".png"
            };
        }

        return new String[] {
                baseFolder + "/" + dex + ".png",
                trimLeadingSlash(baseFolder) + "/" + dex + ".png",
                baseFolder + "/shiny/" + dex + ".png",
                trimLeadingSlash(baseFolder) + "/shiny/" + dex + ".png"
        };
    }

    private static String resolveGenerationBaseFolder(String spritePackMode, int dex) {
        String[] folders = switch (spritePackMode) {
            case "gen2" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-ii/gold",
                    "/pokemon/sprites/pokemon/versions/generation-ii/silver",
                    "/pokemon/sprites/pokemon/versions/generation-ii/crystal"
            };
            case "gen3" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-iii/ruby-sapphire",
                    "/pokemon/sprites/pokemon/versions/generation-iii/emerald",
                    "/pokemon/sprites/pokemon/versions/generation-iii/firered-leafgreen"
            };
            case "gen4" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-iv/diamond-pearl",
                    "/pokemon/sprites/pokemon/versions/generation-iv/platinum",
                    "/pokemon/sprites/pokemon/versions/generation-iv/heartgold-soulsilver"
            };
            case "gen5" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-v/black-white"
            };
            case "gen6" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-vi/x-y",
                    "/pokemon/sprites/pokemon/versions/generation-vi/omegaruby-alphasapphire"
            };
            case "gen7" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-vii/ultra-sun-ultra-moon"
            };
            default -> null;
        };

        if (folders == null || folders.length == 0) {
            return null;
        }

        int index = Math.floorMod(dex - 1, folders.length);
        return folders[index];
    }

    private static String trimLeadingSlash(String value) {
        if (value == null) {
            return "";
        }
        return value.startsWith("/") ? value.substring(1) : value;
    }

    private static String readSpriteMode(Object settings) {
        if (settings == null) {
            return "";
        }

        String[] methodNames = {
                "getSpriteMode",
                "getSelectedSpriteMode",
                "getSpritePreference",
                "getSpriteSetting"
        };

        for (String methodName : methodNames) {
            try {
                Method method = settings.getClass().getMethod(methodName);
                Object value = method.invoke(settings);
                if (value != null) {
                    return String.valueOf(value)
                            .replace('_', ' ')
                            .trim()
                            .toLowerCase();
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return "";
    }

    private static String buildSelectableCardStyle(boolean hovered, boolean selected) {
        if (selected) {
            return "-fx-background-color: #1f2a1f;" +
                    "-fx-background-radius: 16px;" +
                    "-fx-border-color: #6ee16e;" +
                    "-fx-border-radius: 16px;" +
                    "-fx-border-width: 2px;" +
                    "-fx-cursor: hand;";
        }

        return UiStyles.buildCardStyle(hovered);
    }
}