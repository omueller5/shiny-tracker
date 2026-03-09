package net.owen.shinytracker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.lang.reflect.Method;

public final class AboutView {

    private AboutView() {
    }

    public static Parent build(AppNavigator navigator) {
        AppSettings settings = SettingsStorage.loadSettings();

        String rawTheme = readThemeName(settings);
        String formattedTheme = formatThemeName(rawTheme);
        ThemePalette palette = ThemePalette.fromThemeName(rawTheme);

        VBox content = new VBox(22);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: " + palette.pageBackground + ";");

        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back");
        backButton.setFocusTraversable(false);
        backButton.setStyle(
                "-fx-background-color: " + AppTheme.BUTTON_DARK + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-border-color: #333333;" +
                        "-fx-padding: 10 16 10 16;"
        );
        backButton.setOnAction(e -> navigator.showDashboard());

        Label title = new Label("About");
        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 34px;" +
                        "-fx-font-weight: bold;"
        );

        topBar.getChildren().addAll(backButton, title);

        VBox card = new VBox(20);
        card.setPadding(new Insets(28));
        card.setStyle(
                "-fx-background-color: " + palette.mainCardBackground + ";" +
                        "-fx-background-radius: 22px;" +
                        "-fx-border-radius: 22px;" +
                        "-fx-border-color: " + palette.borderColor + ";"
        );

        Label appName = new Label("Shiny Tracker");
        appName.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;"
        );

        Label description = new Label(
                "Shiny Tracker is a local desktop shiny hunting tracker for all 1025 Pokémon. " +
                        "It is built to give a simple encounter counter, shiny odds milestones, " +
                        "time estimates, and phase logging in one place."
        );
        description.setWrapText(true);
        description.setMaxWidth(Double.MAX_VALUE);
        description.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-line-spacing: 3px;"
        );

        VBox infoBox = new VBox(12);
        infoBox.getChildren().addAll(
                buildInfoRow("Version", AppInfo.getVersion(), palette),
                buildInfoRow("Current Theme", formattedTheme, palette),
                buildInfoRow("Sprite Mode", formatSpriteMode(readSpriteMode(settings)), palette)
        );

        Label creditsTitle = new Label("Credits");
        creditsTitle.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );

        VBox creditsBox = new VBox(8);
        creditsBox.setPadding(new Insets(4, 0, 0, 0));

        Label creditsLead = new Label("📌 Credits");
        creditsLead.setStyle(
                "-fx-text-fill: #f3b3c7;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );

        Label creditsBody = new Label(
                "Made by Owen / Shiny Tracker\n" +
                        "Pokémon is © Nintendo, Game Freak, and Creatures Inc.\n" +
                        "Some Pokémon-related names, artwork, and game references belong to their respective owners."
        );
        creditsBody.setWrapText(true);
        creditsBody.setMaxWidth(Double.MAX_VALUE);
        creditsBody.setStyle(
                "-fx-text-fill: " + palette.secondaryText + ";" +
                        "-fx-font-size: 16px;" +
                        "-fx-line-spacing: 3px;"
        );

        creditsBox.getChildren().addAll(creditsLead, creditsBody);

        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color: " + palette.borderColor + ";");

        Label footer = new Label("© 2026 Owen Lab. All rights reserved.");
        footer.setMaxWidth(Double.MAX_VALUE);
        footer.setAlignment(Pos.CENTER);
        footer.setStyle(
                "-fx-text-fill: " + palette.footerText + ";" +
                        "-fx-font-size: 14px;"
        );

        card.getChildren().addAll(
                appName,
                description,
                infoBox,
                creditsTitle,
                creditsBox,
                line,
                footer
        );

        content.getChildren().addAll(topBar, card);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle(
                "-fx-background: " + palette.pageBackground + ";" +
                        "-fx-background-color: " + palette.pageBackground + ";"
        );

        return scrollPane;
    }

    private static HBox buildInfoRow(String labelText, String valueText, ThemePalette palette) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 16, 14, 16));
        row.setStyle(
                "-fx-background-color: " + palette.infoRowBackground + ";" +
                        "-fx-background-radius: 14px;" +
                        "-fx-border-radius: 14px;" +
                        "-fx-border-color: " + palette.infoRowBorder + ";"
        );

        Label label = new Label(labelText + ":");
        label.setMinWidth(145);
        label.setStyle(
                "-fx-text-fill: " + palette.labelText + ";" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );

        Label value = new Label(valueText == null || valueText.isBlank() ? "-" : valueText);
        value.setWrapText(true);
        value.setMaxWidth(Double.MAX_VALUE);
        value.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;"
        );

        row.getChildren().addAll(label, value);
        return row;
    }

    private static String readThemeName(AppSettings settings) {
        return invokeStringGetter(settings,
                "getTheme",
                "getThemeName",
                "getCurrentTheme",
                "getSelectedTheme",
                "getAppearance",
                "getThemeMode",
                "getSelectedThemeName",
                "getThemeId");
    }

    private static String readSpriteMode(AppSettings settings) {
        return invokeStringGetter(settings,
                "getSpriteMode",
                "getSelectedSpriteMode",
                "getSpritePreference",
                "getSpriteSetting");
    }

    private static String invokeStringGetter(Object target, String... methodNames) {
        if (target == null) {
            return "-";
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

        return "-";
    }

    private static String formatThemeName(String raw) {
        if (raw == null || raw.isBlank() || "-".equals(raw)) {
            return "-";
        }

        String normalized = raw.replace('_', ' ').replace('-', ' ').trim().toLowerCase();

        if ("oled".equals(normalized)) {
            return "OLED";
        }

        String[] parts = normalized.split("\\s+");
        StringBuilder formatted = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }

            if (formatted.length() > 0) {
                formatted.append(' ');
            }

            formatted.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                formatted.append(part.substring(1));
            }
        }

        return formatted.toString();
    }

    private static String formatSpriteMode(String raw) {
        if (raw == null || raw.isBlank() || "-".equals(raw)) {
            return "-";
        }

        String normalized = raw.replace('_', ' ').trim().toLowerCase();

        return switch (normalized) {
            case "always shiny" -> "Always Shiny";
            case "always normal" -> "Always Normal";
            case "default" -> "Default";
            default -> {
                String[] parts = normalized.split("\\s+");
                StringBuilder formatted = new StringBuilder();

                for (String part : parts) {
                    if (part.isEmpty()) {
                        continue;
                    }

                    if (formatted.length() > 0) {
                        formatted.append(' ');
                    }

                    formatted.append(Character.toUpperCase(part.charAt(0)));
                    if (part.length() > 1) {
                        formatted.append(part.substring(1));
                    }
                }

                yield formatted.toString();
            }
        };
    }

    private static final class ThemePalette {
        private final String pageBackground;
        private final String mainCardBackground;
        private final String borderColor;
        private final String infoRowBackground;
        private final String infoRowBorder;
        private final String labelText;
        private final String secondaryText;
        private final String footerText;

        private ThemePalette(
                String pageBackground,
                String mainCardBackground,
                String borderColor,
                String infoRowBackground,
                String infoRowBorder,
                String labelText,
                String secondaryText,
                String footerText
        ) {
            this.pageBackground = pageBackground;
            this.mainCardBackground = mainCardBackground;
            this.borderColor = borderColor;
            this.infoRowBackground = infoRowBackground;
            this.infoRowBorder = infoRowBorder;
            this.labelText = labelText;
            this.secondaryText = secondaryText;
            this.footerText = footerText;
        }

        private static ThemePalette fromThemeName(String rawTheme) {
            String normalized = rawTheme == null
                    ? ""
                    : rawTheme.replace('_', ' ').replace('-', ' ').trim().toLowerCase();

            return switch (normalized) {
                case "oled" -> new ThemePalette(
                        "#000000",
                        "#000000",
                        "#1f2937",
                        "#000000",
                        "#1f2937",
                        "#d9e1f2",
                        "#d4dbea",
                        "#b8c2d6"
                );
                case "slate" -> new ThemePalette(
                        "#4a4a4a",
                        "#0b0f18",
                        "#202838",
                        "#060a12",
                        "#1a2333",
                        "#d9e1f2",
                        "#d4dbea",
                        "#b8c2d6"
                );
                default -> new ThemePalette(
                        AppTheme.BACKGROUND_MAIN,
                        "#111111",
                        "#2a2a2a",
                        "#0b0b0b",
                        "#222222",
                        "#d9e1f2",
                        "#d4dbea",
                        "#b8c2d6"
                );
            };
        }
    }
}
