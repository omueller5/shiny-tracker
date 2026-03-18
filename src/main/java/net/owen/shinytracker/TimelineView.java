package net.owen.shinytracker;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimelineView {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d, yyyy • h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    private static final SpeciesEffectRegistry SPECIES_EFFECT_REGISTRY = SpeciesEffectRegistry.load();

    private TimelineView() {
    }

    public static VBox build(List<Hunt> hunts) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(8, 0, 0, 0));

        Label header = new Label("Shiny Timeline");
        header.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );

        List<Hunt> completedHunts = new ArrayList<>();
        if (hunts != null) {
            for (Hunt hunt : hunts) {
                if (hunt != null && hunt.isCompleted()) {
                    completedHunts.add(hunt);
                }
            }
        }

        completedHunts.sort(Comparator
                .comparingLong(TimelineView::resolveTimelineSortValue)
                .reversed()
                .thenComparing(hunt -> safePokemonName(hunt).toLowerCase(Locale.ENGLISH)));

        FlowPane timelineGrid = new FlowPane();
        timelineGrid.setHgap(18);
        timelineGrid.setVgap(18);
        timelineGrid.setPrefWrapLength(1200);
        timelineGrid.setAlignment(Pos.TOP_LEFT);

        if (completedHunts.isEmpty()) {
            timelineGrid.getChildren().add(buildEmptyStateCard());
        } else {
            int index = 1;
            for (Hunt hunt : completedHunts) {
                timelineGrid.getChildren().add(buildTimelineCard(hunt, index++));
            }
        }

        container.getChildren().addAll(header, timelineGrid);
        return container;
    }


    private static Node buildTimelineCard(Hunt hunt, int order) {
        StackPane wrapper = new StackPane();
        wrapper.setAlignment(Pos.TOP_LEFT);
        wrapper.setPrefWidth(292);
        wrapper.setMinWidth(292);
        wrapper.setMaxWidth(292);

        VBox card = new VBox(10);
        card.setPrefWidth(292);
        card.setMinWidth(292);
        card.setMaxWidth(292);
        card.setMinHeight(220);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(16));
        card.setStyle(
                "-fx-background-color: #232323;" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-color: #3a3a3a;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 16px;"
        );

        Pane particleLayer = new Pane();
        particleLayer.setManaged(false);
        particleLayer.setMouseTransparent(true);
        particleLayer.setPickOnBounds(false);
        particleLayer.prefWidthProperty().bind(card.widthProperty());
        particleLayer.prefHeightProperty().bind(card.heightProperty());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(card.widthProperty());
        clip.heightProperty().bind(card.heightProperty());
        clip.setArcWidth(32);
        clip.setArcHeight(32);
        particleLayer.setClip(clip);

        Region accentBar = new Region();
        accentBar.setPrefHeight(4);
        accentBar.setMinHeight(4);
        accentBar.setMaxHeight(4);
        accentBar.setStyle(
                "-fx-background-color: #4d73ff;" +
                        "-fx-background-radius: 16px 16px 999px 999px;"
        );

        Label orderLabel = new Label(order == 1 ? "MOST RECENT" : "#" + order);
        orderLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #3f6ef0;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 4 8 4 8;"
        );

        LuckRating luckRating = resolveLuckRating(hunt);
        Label luckStar = buildLuckStar(luckRating);
        StackPane.setAlignment(luckStar, Pos.TOP_RIGHT);
        StackPane.setMargin(luckStar, new Insets(14, 14, 0, 0));

        Label nameLabel = new Label(safePokemonName(hunt));
        nameLabel.setWrapText(true);
        nameLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );

        TextFlow gameText = GameDisplayUtil.createStyledGameTextFromDisplayName(safeText(hunt.getGame(), "Unknown Game"));
        gameText.setMaxWidth(Double.MAX_VALUE);

        HBox infoRow = new HBox(8);
        infoRow.setAlignment(Pos.CENTER_LEFT);
        infoRow.getChildren().addAll(
                buildInfoPill(formatEncounters(hunt)),
                buildInfoPill(formatPhases(hunt))
        );

        Label completedLabel = new Label("Completed: " + resolveTimelineDisplayText(hunt));
        completedLabel.setWrapText(true);
        completedLabel.setStyle(
                "-fx-text-fill: #cfcfcf;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );

        Label luckLabel = buildLuckLabel(luckRating);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(accentBar, orderLabel, nameLabel, gameText, infoRow, spacer, completedLabel, luckLabel);
        wrapper.getChildren().addAll(card, particleLayer, luckStar);

        applySpecialTypeEffects(hunt, card, particleLayer, accentBar, orderLabel);
        return wrapper;
    }


    private static Label buildLuckStar(LuckRating rating) {
        Label star = new Label("★");

        double size;
        double glow;

        // scale based on luck
        if (rating.displayText().contains("Super Lucky")) {
            size = 26;
            glow = 18;
        } else if (rating.displayText().contains("Very Lucky")) {
            size = 24;
            glow = 16;
        } else if (rating.displayText().contains("Lucky")) {
            size = 22;
            glow = 14;
        } else if (rating.displayText().contains("About Odds")) {
            size = 20;
            glow = 12;
        } else if (rating.displayText().contains("Unlucky")) {
            size = 22;
            glow = 14;
        } else { // Super Unlucky
            size = 24;
            glow = 16;
        }

        star.setStyle(
                "-fx-text-fill: " + toHex(rating.color()) + ";" +
                        "-fx-font-size: " + size + "px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, " + toRgba(rating.color(), 0.9) + ", " + (glow + 4) + ", 0.6, 0, 0), " +
                        "dropshadow(gaussian, " + toRgba(rating.color(), 0.4) + ", " + (glow + 10) + ", 0.3, 0, 0);"
        );

        return star;
    }

    private static Label buildLuckLabel(LuckRating rating) {
        Label label = new Label(rating.displayText());
        label.setWrapText(true);
        label.setTextFill(rating.color());
        label.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: rgba(0,0,0,0.22);" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: " + toRgba(rating.color(), 0.45) + ";" +
                        "-fx-border-radius: 10px;" +
                        "-fx-padding: 5 8 5 8;"
        );
        return label;
    }

    private static LuckRating resolveLuckRating(Hunt hunt) {
        int encounters = Math.max(0, hunt.getTotalEncounters() + hunt.getResetCount());
        double odds = resolveOddsValue(hunt);

        if (encounters <= 0 || odds <= 0) {
            return new LuckRating("Luck Rating: Unavailable", Color.web("#9ca3af"));
        }

        double ratio = encounters / odds;

        if (ratio <= 0.25) {
            return new LuckRating("Luck Rating: Super Lucky", Color.web("#4ade80"));
        }
        if (ratio <= 0.50) {
            return new LuckRating("Luck Rating: Very Lucky", Color.web("#84cc16"));
        }
        if (ratio <= 0.90) {
            return new LuckRating("Luck Rating: Lucky", Color.web("#eab308"));
        }
        if (ratio <= 1.10) {
            return new LuckRating("Luck Rating: About Odds", Color.web("#f59e0b"));
        }
        if (ratio <= 1.75) {
            return new LuckRating("Luck Rating: Unlucky", Color.web("#fb923c"));
        }
        return new LuckRating("Luck Rating: Super Unlucky", Color.web("#ef4444"));
    }

    private static double resolveOddsValue(Hunt hunt) {
        try {
            double odds = ShinyOddsCalculator.getDisplayedOddsDenominator(hunt);
            return odds > 0 ? odds : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private static Double toOddsNumber(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            double d = number.doubleValue();
            return d > 0 ? d : null;
        }

        String text = String.valueOf(value).trim();
        if (text.isBlank()) {
            return null;
        }

        text = text.replace(",", "");

        if (text.matches("\\d+\\s*/\\s*\\d+")) {
            String[] parts = text.split("/");
            if (parts.length == 2) {
                try {
                    double denominator = Double.parseDouble(parts[1].trim());
                    return denominator > 0 ? denominator : null;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (text.toLowerCase(Locale.ENGLISH).startsWith("1 in ")) {
            text = text.substring(5).trim();
        }

        if (text.toLowerCase(Locale.ENGLISH).startsWith("1/")) {
            text = text.substring(2).trim();
        }

        try {
            double d = Double.parseDouble(text);
            return d > 0 ? d : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String toHex(Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    private static String toRgba(Color color, double alpha) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return String.format("rgba(%d,%d,%d,%.3f)", r, g, b, alpha);
    }

    private record LuckRating(String displayText, Color color) {
    }

    private static VBox buildEmptyStateCard() {
        VBox card = new VBox(12);
        card.setPrefSize(360, 160);
        card.setMinSize(360, 160);
        card.setMaxSize(360, 160);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #232323;" +
                        "-fx-background-radius: 14px;" +
                        "-fx-border-color: #3a3a3a;" +
                        "-fx-border-radius: 14px;"
        );

        Label title = new Label("No timeline entries yet");
        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );

        Label body = new Label("Timeline only shows completed hunts, so your finished shinies will appear here once a hunt is marked done.");
        body.setWrapText(true);
        body.setStyle(
                "-fx-text-fill: #b8b8b8;" +
                        "-fx-font-size: 14px;"
        );

        card.getChildren().addAll(title, body);
        return card;
    }

    private static Label buildInfoPill(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-text-fill: #e2e2e2;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #1a1a1a;" +
                        "-fx-background-radius: 999px;" +
                        "-fx-border-color: #333333;" +
                        "-fx-border-radius: 999px;" +
                        "-fx-padding: 5 11 5 11;"
        );
        return label;
    }

    private static void applySpecialTypeEffects(Hunt hunt,
                                                VBox card,
                                                Pane particleLayer,
                                                Region accentBar,
                                                Label orderLabel) {
        SpeciesEffectProfile profile = resolveSpeciesEffectProfile(hunt);
        if (!profile.qualifiesForTypeEffects()) {
            return;
        }

        if (profile.hasDexNumber(493) || profile.hasName("arceus")) {
            applyArceusEffects(card, particleLayer, accentBar, orderLabel);
            return;
        }

        TypeEffectStyle style = profile.primaryStyle();
        switch (style) {
            case FIRE -> applyFireEffects(card, particleLayer, accentBar, orderLabel);
            case WATER_ICE -> applyFrostEffects(card, particleLayer, accentBar, orderLabel);
            case ELECTRIC -> applyElectricEffects(card, particleLayer, accentBar, orderLabel);
            case GRASS -> applyGrassEffects(card, particleLayer, accentBar, orderLabel);
            case POISON -> applyPoisonEffects(card, particleLayer, accentBar, orderLabel);
            case DRAGON -> applyDragonEffects(card, particleLayer, accentBar, orderLabel);
            case PSYCHIC -> applyPsychicEffects(card, particleLayer, accentBar, orderLabel);
            case GHOST -> applyGhostEffects(card, particleLayer, accentBar, orderLabel);
            case DARK -> applyDarkEffects(card, particleLayer, accentBar, orderLabel);
            case FAIRY -> applyFairyEffects(card, particleLayer, accentBar, orderLabel);
            case STEEL -> applySteelEffects(card, particleLayer, accentBar, orderLabel);
            case ROCK_GROUND -> applyRockGroundEffects(card, particleLayer, accentBar, orderLabel);
            case FIGHTING -> applyFightingEffects(card, particleLayer, accentBar, orderLabel);
            case FLYING -> applyFlyingEffects(card, particleLayer, accentBar, orderLabel);
            case BUG -> applyBugEffects(card, particleLayer, accentBar, orderLabel);
            case NORMAL -> applyNormalEffects(card, particleLayer, accentBar, orderLabel);
            default -> {
            }
        }

        if (profile.isPastParadoxLegend()) {
            applyPastParadoxOverlay(card, particleLayer);
        } else if (profile.isFutureParadoxLegend()) {
            applyFutureParadoxOverlay(card, particleLayer);
        }
    }

    private static void applyArceusEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(22, Color.web("#c39cff").deriveColor(0, 1, 1, 0.55));
        shadow.setSpread(0.25);
        card.setEffect(shadow);

        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #232032, #272035 60%, #231d30);" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-color: #c1a7ff;" +
                        "-fx-border-width: 1.3px;" +
                        "-fx-border-radius: 16px;"
        );
        accentBar.setStyle(
                "-fx-background-color: linear-gradient(to right, #7cf06a, #59d6ff, #f4f0b2, #ff9f63, #f48cff, #7ca6ff);" +
                        "-fx-background-radius: 16px 16px 999px 999px;"
        );
        orderLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #c1a7ff;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 4 8 4 8;"
        );

        Timeline pulse = pulseShadow(shadow, 20, 28, 0.22, 0.34, 3.0, true);
        playAndRetain(card, pulse);
        createArceusMotes(particleLayer);
    }

    private static void applyFrostEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(22, Color.web("#7fd7ff").deriveColor(0, 1, 1, 0.55));
        shadow.setSpread(0.25);
        card.setEffect(shadow);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #182235, #1f2230 55%, #1b1b22);" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-color: #71c7ff;" +
                        "-fx-border-width: 1.3px;" +
                        "-fx-border-radius: 16px;"
        );
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #b6e4ff, #e3f7ff); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #74c8ff; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 20, 28, 0.22, 0.34, 2.8, true));
        createSnowFlakes(particleLayer);
    }

    private static void applyElectricEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(22, Color.web("#ffd93b").deriveColor(0, 1, 1, 0.55));
        shadow.setSpread(0.25);
        card.setEffect(shadow);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #282414, #241f13 55%, #1f1c15);" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-color: #ffd93b;" +
                        "-fx-border-width: 1.3px;" +
                        "-fx-border-radius: 16px;"
        );
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #ffe66a, #ffc400); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #e4c23b; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        Timeline glow = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(shadow.radiusProperty(), 18), new KeyValue(shadow.spreadProperty(), 0.20)),
                new KeyFrame(Duration.seconds(0.45), new KeyValue(shadow.radiusProperty(), 26), new KeyValue(shadow.spreadProperty(), 0.34)),
                new KeyFrame(Duration.seconds(0.8), new KeyValue(shadow.radiusProperty(), 19), new KeyValue(shadow.spreadProperty(), 0.22))
        );
        glow.setCycleCount(Animation.INDEFINITE);
        playAndRetain(card, glow);
        createElectricJolts(particleLayer);
    }

    private static void applyFireEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(22, Color.web("#ff7c38").deriveColor(0, 1, 1, 0.58));
        shadow.setSpread(0.27);
        card.setEffect(shadow);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #2d1d16, #241917 58%, #1f1816);" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-color: #ff8748;" +
                        "-fx-border-width: 1.3px;" +
                        "-fx-border-radius: 16px;"
        );
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #ff974f, #ffcb73); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #ff7b3d; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 20, 28, 0.22, 0.36, 1.9, true));
        createEmbers(particleLayer);
    }

    private static void applyGrassEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(20, Color.web("#6de98a").deriveColor(0, 1, 1, 0.45));
        shadow.setSpread(0.22);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #162819, #16221a 58%, #141815); -fx-background-radius: 16px; -fx-border-color: #67d884; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #8ff0a8, #52d37a); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #4fcb75; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 18, 25, 0.18, 0.28, 2.7, true));
        createLeafMotes(particleLayer);
    }

    private static void applyPoisonEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(20, Color.web("#d18cff").deriveColor(0, 1, 1, 0.44));
        shadow.setSpread(0.22);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #24162a, #201524 58%, #17131b); -fx-background-radius: 16px; -fx-border-color: #d18cff; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #eca6ff, #b863ea); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #b96ae8; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 18, 25, 0.18, 0.28, 2.6, true));
        createPoisonBubbles(particleLayer);
    }

    private static void applyDragonEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(20, Color.web("#7f8cff").deriveColor(0, 1, 1, 0.48));
        shadow.setSpread(0.22);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #1d2037, #1e1f2c 58%, #161722); -fx-background-radius: 16px; -fx-border-color: #8090ff; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #9ca7ff, #6b7aff); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #7080ff; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 18, 26, 0.18, 0.30, 2.5, true));
        createDragonRunes(particleLayer);
    }

    private static void applyPsychicEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(20, Color.web("#ff8fda").deriveColor(0, 1, 1, 0.44));
        shadow.setSpread(0.22);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #2a1731, #241623 58%, #1b141c); -fx-background-radius: 16px; -fx-border-color: #ff8fdc; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #ffb3ea, #ff7ccf); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #ff7dcd; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 18, 25, 0.18, 0.28, 2.4, true));
        createPsychicOrbs(particleLayer);
    }

    private static void applyGhostEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(20, Color.web("#a99cff").deriveColor(0, 1, 1, 0.42));
        shadow.setSpread(0.20);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #1e1a29, #1a1722 58%, #14131a); -fx-background-radius: 16px; -fx-border-color: #b09cff; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #cab8ff, #9586f3); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #9a8cf0; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 18, 24, 0.16, 0.26, 2.6, true));
        createGhostWisps(particleLayer);
    }

    private static void applyDarkEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(18, Color.web("#8090a3").deriveColor(0, 1, 1, 0.28));
        shadow.setSpread(0.16);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1c20, #161719 58%, #111214); -fx-background-radius: 16px; -fx-border-color: #748190; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #a0a8b4, #697481); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #6d7884; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 16, 22, 0.14, 0.22, 3.0, true));
        createDarkMotes(particleLayer);
    }

    private static void applyFairyEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(20, Color.web("#ffb3e8").deriveColor(0, 1, 1, 0.42));
        shadow.setSpread(0.20);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #2d1f2f, #241d26 58%, #1a171b); -fx-background-radius: 16px; -fx-border-color: #ffb3e8; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #ffd2f3, #f7a5df); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #ef9ad5; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 18, 24, 0.16, 0.26, 2.7, true));
        createFairySparkles(particleLayer);
    }

    private static void applySteelEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(18, Color.web("#cbd4de").deriveColor(0, 1, 1, 0.30));
        shadow.setSpread(0.14);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #20262d, #1b1f24 58%, #15181c); -fx-background-radius: 16px; -fx-border-color: #c2ccd8; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #edf2f8, #b8c3cf); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: #222; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #d7dfe8; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 16, 21, 0.12, 0.20, 2.8, true));
        createSteelShimmers(particleLayer);
    }

    private static void applyRockGroundEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(18, Color.web("#d4b17b").deriveColor(0, 1, 1, 0.28));
        shadow.setSpread(0.14);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #2c241a, #231e18 58%, #1a1714); -fx-background-radius: 16px; -fx-border-color: #caa56c; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #dfc08e, #b88e5f); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #b98f60; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 16, 22, 0.12, 0.20, 3.0, true));
        createDustMotes(particleLayer);
    }

    private static void applyFightingEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(18, Color.web("#ff9b8d").deriveColor(0, 1, 1, 0.30));
        shadow.setSpread(0.14);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #2d1919, #241717 58%, #191313); -fx-background-radius: 16px; -fx-border-color: #e88d7e; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #ffb0a6, #da7c6e); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #d37266; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 16, 22, 0.12, 0.20, 2.0, true));
        createStrikeBursts(particleLayer);
    }

    private static void applyFlyingEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(18, Color.web("#b8d4ff").deriveColor(0, 1, 1, 0.28));
        shadow.setSpread(0.14);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #1b2431, #1a2027 58%, #14181e); -fx-background-radius: 16px; -fx-border-color: #b9d3ff; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #dbeaff, #9fc0f5); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: #1d2430; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #dbe8ff; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 16, 21, 0.12, 0.20, 3.0, true));
        createBreezeMotes(particleLayer);
    }

    private static void applyBugEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(18, Color.web("#b6e45b").deriveColor(0, 1, 1, 0.30));
        shadow.setSpread(0.14);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #232819, #1c2117 58%, #151914); -fx-background-radius: 16px; -fx-border-color: #b3db4f; -fx-border-width: 1.2px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #d3f07f, #99c93b); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: #203017; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #bce35a; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 16, 22, 0.12, 0.20, 2.9, true));
        createBugMotes(particleLayer);
    }

    private static void applyNormalEffects(VBox card, Pane particleLayer, Region accentBar, Label orderLabel) {
        DropShadow shadow = new DropShadow(16, Color.web("#e6ddd0").deriveColor(0, 1, 1, 0.20));
        shadow.setSpread(0.12);
        card.setEffect(shadow);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #262320, #201d1a 58%, #171513); -fx-background-radius: 16px; -fx-border-color: #d7c9b8; -fx-border-width: 1.1px; -fx-border-radius: 16px;");
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #efe4d6, #c8b7a3); -fx-background-radius: 16px 16px 999px 999px;");
        orderLabel.setStyle("-fx-text-fill: #2f2923; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #e8dbcb; -fx-background-radius: 8px; -fx-padding: 4 8 4 8;");
        playAndRetain(card, pulseShadow(shadow, 14, 18, 0.10, 0.15, 3.2, true));
        createNeutralMotes(particleLayer);
    }


    private static void applyPastParadoxOverlay(VBox card, Pane particleLayer) {
        Color ruinColor = Color.web("#c8a06a");
        DropShadow existing = card.getEffect() instanceof DropShadow ds ? ds : null;
        if (existing != null) {
            existing.setColor(existing.getColor().interpolate(ruinColor, 0.22));
        }

        createPastRuinMarks(particleLayer);
        createDustMotes(particleLayer);
    }

    private static void applyFutureParadoxOverlay(VBox card, Pane particleLayer) {
        Color sciColor = Color.web("#67e7ff");
        DropShadow existing = card.getEffect() instanceof DropShadow ds ? ds : null;
        if (existing != null) {
            existing.setColor(existing.getColor().interpolate(sciColor, 0.28));
        }

        createFutureScanLines(particleLayer);
        createTechNodes(particleLayer);
    }

    private static Timeline pulseShadow(DropShadow shadow,
                                        double startRadius,
                                        double endRadius,
                                        double startSpread,
                                        double endSpread,
                                        double seconds,
                                        boolean autoReverse) {
        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(shadow.radiusProperty(), startRadius),
                        new KeyValue(shadow.spreadProperty(), startSpread)),
                new KeyFrame(Duration.seconds(seconds),
                        new KeyValue(shadow.radiusProperty(), endRadius),
                        new KeyValue(shadow.spreadProperty(), endSpread))
        );
        pulse.setAutoReverse(autoReverse);
        pulse.setCycleCount(Animation.INDEFINITE);
        return pulse;
    }

    private static void createSnowFlakes(Pane layer) {
        double[][] flakes = {{42, 42, 0.92, 8.2}, {90, 30, 0.76, 9.0}, {146, 48, 0.84, 8.5}, {204, 34, 0.70, 9.4}, {248, 52, 0.80, 8.8}};
        for (int i = 0; i < flakes.length; i++) {
            double[] flake = flakes[i];
            Group snowflake = buildSnowflake();
            snowflake.setTranslateX(flake[0]);
            snowflake.setTranslateY(flake[1]);
            snowflake.setScaleX(flake[2]);
            snowflake.setScaleY(flake[2]);
            snowflake.setOpacity(0.0);
            layer.getChildren().add(snowflake);

            TranslateTransition drift = new TranslateTransition(Duration.seconds(flake[3]), snowflake);
            drift.setByY(126 + (i * 7));
            drift.setByX(i % 2 == 0 ? 14 : -14);
            drift.setCycleCount(Animation.INDEFINITE);

            FadeTransition fade = new FadeTransition(Duration.seconds(flake[3] * 0.55), snowflake);
            fade.setFromValue(0.0);
            fade.setToValue(0.88);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);

            Timeline rotate = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(snowflake.rotateProperty(), 0)),
                    new KeyFrame(Duration.seconds(flake[3]), new KeyValue(snowflake.rotateProperty(), i % 2 == 0 ? 96 : -96))
            );
            rotate.setCycleCount(Animation.INDEFINITE);

            ParallelTransition combo = new ParallelTransition(drift, fade, rotate);
            combo.setDelay(Duration.seconds(i * 0.40));
            playAndRetain(layer, combo);
        }
    }

    private static Group buildSnowflake() {
        Group group = new Group();
        Line a = new Line(-4.2, 0, 4.2, 0);
        Line b = new Line(0, -4.2, 0, 4.2);
        Line c = new Line(-3.2, -3.2, 3.2, 3.2);
        Line d = new Line(-3.2, 3.2, 3.2, -3.2);
        Circle center = new Circle(0, 0, 1.0, Color.web("#f7fcff"));
        for (Line line : Arrays.asList(a, b, c, d)) {
            line.setStroke(Color.web("#e8f9ff"));
            line.setStrokeWidth(1.1);
            line.setOpacity(0.95);
        }
        group.getChildren().addAll(a, b, c, d, center);
        group.setEffect(new DropShadow(7, Color.web("#b6eaff")));
        return group;
    }

    private static void createElectricJolts(Pane layer) {
        double[][] anchors = {{40, 56, 22, 12}, {262, 48, -24, 15}, {60, 170, 24, -14}, {244, 182, -22, -16}};
        for (int i = 0; i < anchors.length; i++) {
            double[] anchor = anchors[i];
            Group bolt = buildElectricBolt(anchor[2], anchor[3]);
            bolt.setTranslateX(anchor[0]);
            bolt.setTranslateY(anchor[1]);
            bolt.setOpacity(0.0);
            layer.getChildren().add(bolt);

            Timeline flicker = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(bolt.opacityProperty(), 0.0)),
                    new KeyFrame(Duration.seconds(0.05), new KeyValue(bolt.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(0.10), new KeyValue(bolt.opacityProperty(), 0.20)),
                    new KeyFrame(Duration.seconds(0.16), new KeyValue(bolt.opacityProperty(), 0.92)),
                    new KeyFrame(Duration.seconds(0.24), new KeyValue(bolt.opacityProperty(), 0.0))
            );
            flicker.setCycleCount(Animation.INDEFINITE);
            flicker.setDelay(Duration.seconds(i * 0.34));

            ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.24), bolt);
            pulse.setFromX(0.92);
            pulse.setFromY(0.92);
            pulse.setToX(1.08);
            pulse.setToY(1.08);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(Animation.INDEFINITE);
            pulse.setDelay(Duration.seconds(i * 0.34));

            ParallelTransition combo = new ParallelTransition(flicker, pulse);
            playAndRetain(layer, combo);
        }
    }

    private static Group buildElectricBolt(double dx, double dy) {
        Group group = new Group();
        double midX = dx * 0.45;
        double midY = dy * 0.42;
        Line coreA = new Line(0, 0, midX, midY);
        Line coreB = new Line(midX, midY, dx * 0.62, dy * 0.74);
        Line coreC = new Line(dx * 0.62, dy * 0.74, dx, dy);
        Line branch = new Line(midX * 0.82, midY * 0.76, midX + (dx > 0 ? -6 : 6), midY + 9);
        for (Line line : Arrays.asList(coreA, coreB, coreC, branch)) {
            line.setStroke(Color.web("#fff2a4"));
            line.setStrokeWidth(line == branch ? 1.6 : 2.2);
            line.setOpacity(1.0);
            line.setEffect(new DropShadow(8, Color.web("#ffd93b")));
        }
        group.getChildren().addAll(coreA, coreB, coreC, branch);
        return group;
    }

    private static void createEmbers(Pane layer) {
        double[][] embers = {{36, 196, 1.8, 6.5}, {82, 174, 1.3, 7.8}, {136, 202, 2.0, 5.6}, {206, 188, 1.5, 7.1}, {256, 204, 1.7, 6.3}};
        for (int i = 0; i < embers.length; i++) {
            double[] ember = embers[i];
            Circle circle = new Circle(ember[2], i % 2 == 0 ? Color.web("#ffb05e") : Color.web("#ff7a38"));
            circle.setCenterX(ember[0]);
            circle.setCenterY(ember[1]);
            circle.setOpacity(0.0);
            circle.setEffect(new DropShadow(9, i % 2 == 0 ? Color.web("#ffb05e") : Color.web("#ff6a21")));
            layer.getChildren().add(circle);

            TranslateTransition rise = new TranslateTransition(Duration.seconds(ember[3]), circle);
            rise.setByY(-88 - (i * 7));
            rise.setByX(i % 2 == 0 ? 12 : -10);
            rise.setCycleCount(Animation.INDEFINITE);

            FadeTransition fade = new FadeTransition(Duration.seconds(ember[3]), circle);
            fade.setFromValue(0.0);
            fade.setToValue(0.92);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);

            ScaleTransition scale = new ScaleTransition(Duration.seconds(ember[3]), circle);
            scale.setFromX(0.75);
            scale.setFromY(0.75);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.setCycleCount(Animation.INDEFINITE);

            ParallelTransition combo = new ParallelTransition(rise, fade, scale);
            combo.setDelay(Duration.seconds(i * 0.45));
            playAndRetain(layer, combo);
        }
    }

    private static void createArceusMotes(Pane layer) {
        Color[] colors = {Color.web("#7cf06a"), Color.web("#59d6ff"), Color.web("#f4f0b2"), Color.web("#ff9f63"), Color.web("#f48cff"), Color.web("#7ca6ff")};
        double[][] motes = {{34, 44, 1.8}, {86, 38, 1.5}, {240, 56, 1.7}, {256, 160, 1.5}, {66, 192, 1.5}};
        for (int i = 0; i < motes.length; i++) {
            Circle mote = new Circle(motes[i][2], colors[i % colors.length]);
            mote.setCenterX(motes[i][0]);
            mote.setCenterY(motes[i][1]);
            mote.setOpacity(0.68);
            mote.setEffect(new DropShadow(7, colors[i % colors.length]));
            layer.getChildren().add(mote);

            TranslateTransition floatAround = new TranslateTransition(Duration.seconds(4.8 + (i * 0.5)), mote);
            floatAround.setByX(i % 2 == 0 ? 14 : -14);
            floatAround.setByY(i % 3 == 0 ? -12 : 12);
            floatAround.setAutoReverse(true);
            floatAround.setCycleCount(Animation.INDEFINITE);

            FadeTransition fade = new FadeTransition(Duration.seconds(3.0 + (i * 0.3)), mote);
            fade.setFromValue(0.30);
            fade.setToValue(0.88);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);

            ScaleTransition scale = new ScaleTransition(Duration.seconds(2.8 + (i * 0.25)), mote);
            scale.setFromX(0.8);
            scale.setFromY(0.8);
            scale.setToX(1.28);
            scale.setToY(1.28);
            scale.setAutoReverse(true);
            scale.setCycleCount(Animation.INDEFINITE);

            ParallelTransition combo = new ParallelTransition(floatAround, fade, scale);
            playAndRetain(layer, combo);
        }
    }

    private static void createLeafMotes(Pane layer) {
        createSimpleMotes(layer, new double[][]{{36, 44, 1.9}, {84, 64, 1.5}, {232, 42, 1.6}, {244, 168, 1.5}, {64, 176, 1.4}},
                Color.web("#83e493"), Color.web("#4dc86f"), 11, 13, 11, 12);
    }

    private static void createPoisonBubbles(Pane layer) {
        createSimpleMotes(layer, new double[][]{{38, 54, 1.9}, {82, 38, 1.5}, {236, 52, 1.7}, {248, 172, 1.6}, {62, 184, 1.4}},
                Color.web("#d79cff"), Color.web("#b35bde"), 10, 10, 10, 11);
    }

    private static void createDragonRunes(Pane layer) {
        createSimpleMotes(layer, new double[][]{{38, 48, 1.7}, {90, 34, 1.4}, {238, 56, 1.5}, {242, 176, 1.5}, {60, 182, 1.4}},
                Color.web("#9eb0ff"), Color.web("#6f81ff"), 9, 11, 8, 11);
    }

    private static void createPsychicOrbs(Pane layer) {
        createSimpleMotes(layer, new double[][]{{42, 46, 1.8}, {88, 36, 1.4}, {232, 50, 1.6}, {246, 168, 1.5}, {60, 180, 1.4}},
                Color.web("#ffb0e6"), Color.web("#ff7bca"), 10, 12, 9, 11);
    }

    private static void createGhostWisps(Pane layer) {
        createSimpleMotes(layer, new double[][]{{38, 44, 1.9}, {84, 34, 1.5}, {236, 46, 1.6}, {246, 170, 1.5}, {58, 182, 1.4}},
                Color.web("#c5b7ff"), Color.web("#8f81e7"), 10, 12, 8, 10);
    }

    private static void createDarkMotes(Pane layer) {
        createSimpleMotes(layer, new double[][]{{42, 48, 1.6}, {90, 38, 1.3}, {234, 52, 1.4}, {244, 168, 1.4}, {60, 182, 1.3}},
                Color.web("#a0aab8"), Color.web("#6e7782"), 8, 9, 7, 8);
    }

    private static void createFairySparkles(Pane layer) {
        createSimpleMotes(layer, new double[][]{{38, 42, 1.9}, {84, 32, 1.5}, {236, 44, 1.7}, {248, 170, 1.6}, {56, 184, 1.4}},
                Color.web("#ffd7f2"), Color.web("#f39ad8"), 10, 12, 8, 10);
    }

    private static void createSteelShimmers(Pane layer) {
        createSimpleMotes(layer, new double[][]{{42, 44, 1.5}, {86, 36, 1.2}, {236, 46, 1.3}, {244, 166, 1.3}, {60, 180, 1.2}},
                Color.web("#eef3f8"), Color.web("#b8c3cf"), 7, 8, 7, 8);
    }

    private static void createDustMotes(Pane layer) {
        createSimpleMotes(layer, new double[][]{{40, 46, 1.7}, {88, 38, 1.4}, {234, 50, 1.5}, {242, 170, 1.4}, {58, 178, 1.3}},
                Color.web("#ddc18b"), Color.web("#b88e5f"), 8, 9, 8, 9);
    }

    private static void createStrikeBursts(Pane layer) {
        createSimpleMotes(layer, new double[][]{{42, 44, 1.7}, {88, 34, 1.4}, {236, 46, 1.5}, {246, 170, 1.5}, {60, 182, 1.3}},
                Color.web("#ffb9ac"), Color.web("#d46d61"), 10, 11, 7, 9);
    }

    private static void createBreezeMotes(Pane layer) {
        createSimpleMotes(layer, new double[][]{{40, 42, 1.5}, {86, 34, 1.2}, {236, 44, 1.3}, {244, 168, 1.3}, {60, 180, 1.2}},
                Color.web("#e0efff"), Color.web("#9fc0f5"), 7, 8, 8, 9);
    }

    private static void createBugMotes(Pane layer) {
        createSimpleMotes(layer, new double[][]{{38, 46, 1.6}, {82, 38, 1.3}, {236, 50, 1.4}, {246, 172, 1.4}, {58, 182, 1.3}},
                Color.web("#d6f18a"), Color.web("#95c239"), 8, 10, 8, 10);
    }

    private static void createNeutralMotes(Pane layer) {
        createSimpleMotes(layer, new double[][]{{42, 48, 1.4}, {90, 38, 1.1}, {236, 52, 1.2}, {244, 170, 1.2}, {60, 184, 1.1}},
                Color.web("#f0e6d7"), Color.web("#c8b7a3"), 6, 7, 7, 8);
    }

    private static void createSimpleMotes(Pane layer, double[][] motes, Color colorA, Color colorB, double byX1, double byX2, double byY1, double byY2) {
        for (int i = 0; i < motes.length; i++) {
            Circle mote = new Circle(motes[i][2], i % 2 == 0 ? colorA : colorB);
            mote.setCenterX(motes[i][0]);
            mote.setCenterY(motes[i][1]);
            mote.setOpacity(0.26);
            mote.setEffect(new DropShadow(7, i % 2 == 0 ? colorA : colorB));
            layer.getChildren().add(mote);

            TranslateTransition move = new TranslateTransition(Duration.seconds(4.3 + (i * 0.35)), mote);
            move.setByX(i % 2 == 0 ? byX1 : -byX2);
            move.setByY(i % 3 == 0 ? -byY1 : byY2);
            move.setAutoReverse(true);
            move.setCycleCount(Animation.INDEFINITE);

            FadeTransition fade = new FadeTransition(Duration.seconds(2.7 + (i * 0.2)), mote);
            fade.setFromValue(0.10);
            fade.setToValue(0.55);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);

            ScaleTransition scale = new ScaleTransition(Duration.seconds(3.0 + (i * 0.2)), mote);
            scale.setFromX(0.85);
            scale.setFromY(0.85);
            scale.setToX(1.25);
            scale.setToY(1.25);
            scale.setAutoReverse(true);
            scale.setCycleCount(Animation.INDEFINITE);

            ParallelTransition combo = new ParallelTransition(move, fade, scale);
            playAndRetain(layer, combo);
        }
    }


    private static void createPastRuinMarks(Pane layer) {
        double[][] marks = {
                {34, 34, 18, 10},
                {230, 28, -16, 12},
                {26, 168, 14, -16},
                {226, 176, -18, -12}
        };

        for (int i = 0; i < marks.length; i++) {
            Group mark = buildRuinMark(marks[i][2], marks[i][3]);
            mark.setTranslateX(marks[i][0]);
            mark.setTranslateY(marks[i][1]);
            mark.setOpacity(0.18);
            layer.getChildren().add(mark);

            FadeTransition fade = new FadeTransition(Duration.seconds(2.4 + (i * 0.3)), mark);
            fade.setFromValue(0.08);
            fade.setToValue(0.26);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);

            ScaleTransition scale = new ScaleTransition(Duration.seconds(3.6 + (i * 0.2)), mark);
            scale.setFromX(0.92);
            scale.setFromY(0.92);
            scale.setToX(1.08);
            scale.setToY(1.08);
            scale.setAutoReverse(true);
            scale.setCycleCount(Animation.INDEFINITE);

            ParallelTransition combo = new ParallelTransition(fade, scale);
            playAndRetain(layer, combo);
        }
    }

    private static Group buildRuinMark(double dx, double dy) {
        Group group = new Group();

        Line a = new Line(0, 0, dx, dy);
        Line b = new Line(dx * 0.35, dy * 0.15, dx * 0.72, dy * 0.58);
        Line c = new Line(dx * 0.58, dy * 0.30, dx * 0.92, dy * 0.88);

        for (Line line : Arrays.asList(a, b, c)) {
            line.setStroke(Color.web("#c6a26f"));
            line.setStrokeWidth(1.5);
            line.setOpacity(0.9);
            line.setEffect(new DropShadow(6, Color.web("#8b6742")));
        }

        group.getChildren().addAll(a, b, c);
        return group;
    }

    private static void createFutureScanLines(Pane layer) {
        double[] ys = {28, 64, 118, 164};

        for (int i = 0; i < ys.length; i++) {
            Rectangle line = new Rectangle(0, ys[i], 86, 2);
            line.setArcWidth(6);
            line.setArcHeight(6);
            line.setFill(i % 2 == 0 ? Color.web("#6cf0ff", 0.34) : Color.web("#9aa7ff", 0.26));
            line.setEffect(new DropShadow(8, i % 2 == 0 ? Color.web("#67e7ff") : Color.web("#92a7ff")));
            layer.getChildren().add(line);

            TranslateTransition sweep = new TranslateTransition(Duration.seconds(3.1 + (i * 0.25)), line);
            sweep.setFromX(-94);
            sweep.setToX(306);
            sweep.setCycleCount(Animation.INDEFINITE);

            FadeTransition fade = new FadeTransition(Duration.seconds(1.3 + (i * 0.15)), line);
            fade.setFromValue(0.10);
            fade.setToValue(0.42);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);

            ParallelTransition combo = new ParallelTransition(sweep, fade);
            combo.setDelay(Duration.seconds(i * 0.22));
            playAndRetain(layer, combo);
        }
    }

    private static void createTechNodes(Pane layer) {
        double[][] nodes = {
                {36, 38, 2.2},
                {94, 30, 1.7},
                {236, 46, 1.9},
                {250, 170, 1.8},
                {58, 182, 1.7}
        };

        for (int i = 0; i < nodes.length; i++) {
            Circle node = new Circle(nodes[i][2], i % 2 == 0 ? Color.web("#69efff") : Color.web("#9ea8ff"));
            node.setCenterX(nodes[i][0]);
            node.setCenterY(nodes[i][1]);
            node.setOpacity(0.28);
            node.setEffect(new DropShadow(8, i % 2 == 0 ? Color.web("#69efff") : Color.web("#9ea8ff")));
            layer.getChildren().add(node);

            ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.9 + (i * 0.15)), node);
            pulse.setFromX(0.75);
            pulse.setFromY(0.75);
            pulse.setToX(1.45);
            pulse.setToY(1.45);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(Animation.INDEFINITE);

            FadeTransition fade = new FadeTransition(Duration.seconds(1.7 + (i * 0.18)), node);
            fade.setFromValue(0.12);
            fade.setToValue(0.62);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);

            ParallelTransition combo = new ParallelTransition(pulse, fade);
            playAndRetain(layer, combo);
        }
    }

    private static void playAndRetain(Node owner, Animation animation) {
        if (owner == null || animation == null) {
            return;
        }

        Object existing = owner.getProperties().get("timelineAnimations");
        ObservableList<Animation> animations;
        if (existing instanceof ObservableList<?>) {
            @SuppressWarnings("unchecked")
            ObservableList<Animation> cast = (ObservableList<Animation>) existing;
            animations = cast;
        } else {
            animations = FXCollections.observableArrayList();
            owner.getProperties().put("timelineAnimations", animations);
        }

        animations.add(animation);
        animation.play();
    }

    private static SpeciesEffectProfile resolveSpeciesEffectProfile(Hunt hunt) {
        int dexNumber = hunt != null ? hunt.getDexNumber() : -1;
        SpeciesEffectProfile fromRegistry = SPECIES_EFFECT_REGISTRY.get(dexNumber);
        if (fromRegistry != null) {
            return fromRegistry;
        }

        Set<String> fallbackTypes = new LinkedHashSet<>();
        Object combined = invokeFirstPresentMethod(hunt, "getTypes", "getPokemonTypes", "getTypeNames");
        appendTypeValue(fallbackTypes, combined);
        appendTypeValue(fallbackTypes, invokeFirstPresentMethod(hunt, "getType", "getPrimaryType", "getSecondaryType", "getType1", "getType2"));

        boolean special = readBooleanFlag(hunt, "isLegendary", "getLegendary", "legendary", "isMythical", "getMythical", "mythical")
                || dexNumber == 1009 || dexNumber == 1010 || dexNumber == 1020 || dexNumber == 1021 || dexNumber == 1022 || dexNumber == 1023;
        return new SpeciesEffectProfile(
                dexNumber,
                safePokemonName(hunt),
                special,
                fallbackTypes
        );
    }

    private static boolean readBooleanFlag(Hunt hunt, String... methodNames) {
        for (String methodName : methodNames) {
            Object value = invokeFirstPresentMethod(hunt, methodName);
            if (value instanceof Boolean bool && bool) {
                return true;
            }
            if (value instanceof Number number && number.intValue() != 0) {
                return true;
            }
        }
        return false;
    }

    private static void appendTypeValue(Set<String> parts, Object value) {
        if (value == null || parts == null) {
            return;
        }
        if (value instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                appendTypeValue(parts, item);
            }
            return;
        }
        if (value.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(value);
            for (int i = 0; i < length; i++) {
                appendTypeValue(parts, java.lang.reflect.Array.get(value, i));
            }
            return;
        }
        String text = normalizeTypeToken(String.valueOf(value));
        if (!text.isBlank()) {
            parts.add(text);
        }
    }

    private static String normalizeTypeToken(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('_', ' ').replace('-', ' ').trim().toLowerCase(Locale.ENGLISH);
    }

    private static final class SpeciesEffectRegistry {
        private static final Pattern ENTRY_PATTERN = Pattern.compile(
                "\\{\\s*\\\"id\\\"\\s*:\\s*\\\"#(\\d+)\\\".*?\\\"name\\\"\\s*:\\s*\\{.*?\\\"english\\\"\\s*:\\s*\\\"([^\\\"]+)\\\".*?\\}\\s*,.*?\\\"formData\\\"\\s*:\\s*\\[\\s*\\{.*?\\\"type\\\"\\s*:\\s*\\[(.*?)\\].*?\\]\\s*,.*?\\\"description\\\"\\s*:\\s*\\\"(.*?)\\\"",
                Pattern.DOTALL
        );
        private static final Pattern TYPE_TOKEN_PATTERN = Pattern.compile("\"([^\"]+)\"");

        private final Map<Integer, SpeciesEffectProfile> byDex;

        private SpeciesEffectRegistry(Map<Integer, SpeciesEffectProfile> byDex) {
            this.byDex = byDex;
        }

        static SpeciesEffectRegistry load() {
            Map<Integer, boolean[]> rarityMap = loadLegendaryMythicalMap();
            Map<Integer, SpeciesEffectProfile> map = new HashMap<>();

            String content = readResourceText("/pokemon/pokedex.json", "pokemon/pokedex.json", "/pokedex.json", "pokedex.json");
            if (!content.isBlank()) {
                Matcher matcher = ENTRY_PATTERN.matcher(content);
                while (matcher.find()) {
                    Integer dex = parseInteger(matcher.group(1));
                    if (dex == null) {
                        continue;
                    }

                    String englishName = matcher.group(2) == null ? "" : matcher.group(2);
                    String description = matcher.group(4) == null ? "" : matcher.group(4);
                    LinkedHashSet<String> types = new LinkedHashSet<>();
                    Matcher typeMatcher = TYPE_TOKEN_PATTERN.matcher(matcher.group(3));
                    while (typeMatcher.find()) {
                        String normalized = normalizeTypeToken(typeMatcher.group(1));
                        if (!normalized.isBlank()) {
                            types.add(normalized);
                        }
                    }

                    boolean[] flags = rarityMap.getOrDefault(dex, new boolean[]{false, false});
                    boolean paradoxLegend = dex == 1009 || dex == 1010 || dex == 1020 || dex == 1021 || dex == 1022 || dex == 1023;
                    boolean qualifies = flags[0] || flags[1] || paradoxLegend ||
                            description.toLowerCase(Locale.ENGLISH).contains("legendary") ||
                            description.toLowerCase(Locale.ENGLISH).contains("mythical");

                    map.put(dex, new SpeciesEffectProfile(dex, englishName, qualifies, types));
                }
            }

            return new SpeciesEffectRegistry(map);
        }

        SpeciesEffectProfile get(int dex) {
            return byDex.get(dex);
        }

        private static Map<Integer, boolean[]> loadLegendaryMythicalMap() {
            String content = readResourceText("/national_dex.txt", "national_dex.txt");
            if (content.isBlank()) {
                return Collections.emptyMap();
            }

            Map<Integer, boolean[]> map = new HashMap<>();
            String[] lines = content.split("\\R");
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length <= 17) {
                    continue;
                }
                Integer dex = parseInteger(parts[0]);
                if (dex == null) {
                    continue;
                }
                map.put(dex, new boolean[]{"1".equals(parts[16].trim()), "1".equals(parts[17].trim())});
            }
            return map;
        }

        private static Integer parseInteger(String text) {
            try {
                return Integer.parseInt(text.trim());
            } catch (Exception ignored) {
                return null;
            }
        }

        private static String readResourceText(String... candidatePaths) {
            if (candidatePaths == null) {
                return "";
            }

            for (String candidatePath : candidatePaths) {
                if (candidatePath == null || candidatePath.isBlank()) {
                    continue;
                }

                try (InputStream stream = TimelineView.class.getResourceAsStream(candidatePath)) {
                    if (stream == null) {
                        continue;
                    }

                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line).append("\n");
                        }
                    }

                    return builder.toString();
                } catch (IOException ignored) {
                }
            }

            return "";
        }
    }

    private record SpeciesEffectProfile(int dexNumber, String englishName, boolean qualifies, Set<String> types) {
        boolean qualifiesForTypeEffects() {
            return qualifies;
        }

        boolean hasDexNumber(int wantedDex) {
            return dexNumber == wantedDex;
        }

        boolean hasName(String wantedName) {
            return wantedName != null && wantedName.equalsIgnoreCase(englishName);
        }

        boolean isPastParadoxLegend() {
            return dexNumber == 1009 || dexNumber == 1020 || dexNumber == 1021;
        }

        boolean isFutureParadoxLegend() {
            return dexNumber == 1010 || dexNumber == 1022 || dexNumber == 1023;
        }

        boolean hasType(String wantedType) {
            String normalized = normalizeTypeToken(wantedType);
            for (String type : types) {
                if (normalized.equals(normalizeTypeToken(type))) {
                    return true;
                }
            }
            return false;
        }

        TypeEffectStyle primaryStyle() {
            String primary = primaryType();
            if ("electric".equals(primary)) return TypeEffectStyle.ELECTRIC;
            if ("fire".equals(primary)) return TypeEffectStyle.FIRE;
            if ("ice".equals(primary) || "water".equals(primary)) return TypeEffectStyle.WATER_ICE;
            if ("grass".equals(primary)) return TypeEffectStyle.GRASS;
            if ("poison".equals(primary)) return TypeEffectStyle.POISON;
            if ("dragon".equals(primary)) return TypeEffectStyle.DRAGON;
            if ("psychic".equals(primary)) return TypeEffectStyle.PSYCHIC;
            if ("ghost".equals(primary)) return TypeEffectStyle.GHOST;
            if ("dark".equals(primary)) return TypeEffectStyle.DARK;
            if ("fairy".equals(primary)) return TypeEffectStyle.FAIRY;
            if ("steel".equals(primary)) return TypeEffectStyle.STEEL;
            if ("rock".equals(primary) || "ground".equals(primary)) return TypeEffectStyle.ROCK_GROUND;
            if ("fighting".equals(primary)) return TypeEffectStyle.FIGHTING;
            if ("flying".equals(primary)) return TypeEffectStyle.FLYING;
            if ("bug".equals(primary)) return TypeEffectStyle.BUG;
            return TypeEffectStyle.NORMAL;
        }

        String primaryType() {
            if (types == null || types.isEmpty()) {
                return "";
            }
            for (String type : types) {
                String normalized = normalizeTypeToken(type);
                if (!normalized.isBlank()) {
                    return normalized;
                }
            }
            return "";
        }
    }

    private enum TypeEffectStyle {
        FIRE,
        WATER_ICE,
        ELECTRIC,
        GRASS,
        POISON,
        DRAGON,
        PSYCHIC,
        GHOST,
        DARK,
        FAIRY,
        STEEL,
        ROCK_GROUND,
        FIGHTING,
        FLYING,
        BUG,
        NORMAL
    }

    private static String formatEncounters(Hunt hunt) {
        int combinedEncounters = Math.max(0, hunt.getTotalEncounters() + hunt.getResetCount());
        return String.format("%,d encounters", combinedEncounters);
    }

    private static String formatPhases(Hunt hunt) {
        int phaseCount = Math.max(0, hunt.getPhaseCount());
        return phaseCount == 1 ? "1 phase" : String.format("%,d phases", phaseCount);
    }

    private static long resolveTimelineSortValue(Hunt hunt) {
        Object value = invokeFirstPresentMethod(
                hunt,
                "getCompletedAt",
                "getCompletionTimestamp",
                "getCompletedTimestamp",
                "getCompletionTime",
                "getCompletedTime",
                "getCompletedDate",
                "getCompletionDate",
                "getShinyDate",
                "getShinyTimestamp",
                "getFinishedAt",
                "getFinishedTime"
        );

        Long converted = toEpochMillis(value);
        if (converted != null) {
            return converted;
        }

        return hunt.getLastUpdated();
    }

    private static String resolveTimelineDisplayText(Hunt hunt) {
        Object value = invokeFirstPresentMethod(
                hunt,
                "getCompletedAt",
                "getCompletionTimestamp",
                "getCompletedTimestamp",
                "getCompletionTime",
                "getCompletedTime",
                "getCompletedDate",
                "getCompletionDate",
                "getShinyDate",
                "getShinyTimestamp",
                "getFinishedAt",
                "getFinishedTime"
        );

        String formatted = formatTemporalValue(value);
        if (formatted != null && !formatted.isBlank()) {
            return formatted;
        }

        long fallback = hunt.getLastUpdated();
        if (fallback > 0) {
            return DATE_TIME_FORMATTER.format(
                    Instant.ofEpochMilli(fallback)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );
        }

        return "Recently";
    }

    private static Object invokeFirstPresentMethod(Object target, String... methodNames) {
        if (target == null || methodNames == null) {
            return null;
        }

        for (String methodName : methodNames) {
            try {
                Method method = target.getClass().getMethod(methodName);
                return method.invoke(target);
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return null;
    }

    private static Long toEpochMillis(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            long raw = number.longValue();
            return raw > 0 && raw < 100_000_000_000L ? raw * 1000L : raw;
        }

        if (value instanceof Instant instant) {
            return instant.toEpochMilli();
        }

        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        if (value instanceof LocalDate localDate) {
            return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        if (value instanceof CharSequence sequence) {
            String text = sequence.toString().trim();
            if (text.isBlank()) {
                return null;
            }

            try {
                long parsed = Long.parseLong(text);
                return parsed > 0 && parsed < 100_000_000_000L ? parsed * 1000L : parsed;
            } catch (NumberFormatException ignored) {
            }

            try {
                return Instant.parse(text).toEpochMilli();
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private static String formatTemporalValue(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number || value instanceof Instant || value instanceof LocalDateTime || value instanceof LocalDate) {
            Long epochMillis = toEpochMillis(value);
            if (epochMillis != null && epochMillis > 0) {
                return DATE_TIME_FORMATTER.format(
                        Instant.ofEpochMilli(epochMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                );
            }
        }

        if (value instanceof CharSequence sequence) {
            String text = sequence.toString().trim();
            if (text.isBlank()) {
                return null;
            }

            Long epochMillis = toEpochMillis(text);
            if (epochMillis != null && epochMillis > 0) {
                return DATE_TIME_FORMATTER.format(
                        Instant.ofEpochMilli(epochMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                );
            }

            return text;
        }

        if (value instanceof LocalDate localDate) {
            return DATE_FORMATTER.format(localDate);
        }

        return String.valueOf(value);
    }

    private static String safePokemonName(Hunt hunt) {
        if (hunt == null || hunt.getPokemonName() == null || hunt.getPokemonName().isBlank()) {
            return "Unknown";
        }
        return hunt.getPokemonName();
    }

    private static String safeText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
}
