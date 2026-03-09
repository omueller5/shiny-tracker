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

public final class HelpView {

    private HelpView() {
    }

    public static Parent build(AppNavigator navigator) {
        VBox content = new VBox(22);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: " + AppTheme.BACKGROUND_MAIN + ";");

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

        Label title = new Label("Help");
        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 34px;" +
                        "-fx-font-weight: bold;"
        );

        topBar.getChildren().addAll(backButton, title);

        VBox card = new VBox(22);
        card.setPadding(new Insets(28));
        card.setStyle(
                "-fx-background-color: #0b0f18;" +
                        "-fx-background-radius: 22px;" +
                        "-fx-border-radius: 22px;" +
                        "-fx-border-color: #202838;"
        );

        Label intro = new Label(
                "Shiny Tracker helps you manage hunts, count encounters, log phases, " +
                        "review milestone estimates, and keep everything saved locally in one desktop app."
        );
        intro.setWrapText(true);
        intro.setMaxWidth(Double.MAX_VALUE);
        intro.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-line-spacing: 3px;"
        );

        VBox sections = new VBox(16);
        sections.getChildren().addAll(
                buildSection(
                        "Getting Started",
                        "Create a hunt from the dashboard, choose your Pokémon, game, and hunt method, then open the active hunt screen to begin tracking."
                ),
                buildSection(
                        "Encounter Counter",
                        "Use the +1 and -1 controls to update your current encounter count. If enabled in Options, larger adjustment buttons can also appear for faster counting."
                ),
                buildSection(
                        "Hunt Done",
                        "Use Hunt Done only when you find the target shiny for the hunt. This marks the hunt as complete and updates the hunt display accordingly."
                ),
                buildSection(
                        "Logging Phases",
                        "Use Log Phase when you find a shiny that is not your target. This saves the phase, adds its encounters to your total encounters, and resets the current phase count."
                ),
                buildSection(
                        "Editing Phase Shinies",
                        "Logged phases default to Unknown Phase Shiny. Use the Edit button on a phase card to choose the actual shiny Pokémon later if you want to update it."
                ),
                buildSection(
                        "Probability Milestones",
                        "Open the milestone estimates section on the active hunt screen to view common probability breakpoints, estimated encounters, and estimated time."
                ),
                buildSection(
                        "Import / Export",
                        "Use the dashboard and menu options to import hunts from JSON files or export a single hunt, selected hunts, or all hunts for backup and transfer."
                ),
                buildSection(
                        "Selection Mode",
                        "Selection Mode lets you choose multiple hunts at once for batch actions like deleting or exporting selected hunts."
                ),
                buildSection(
                        "Options",
                        "Use Options from the menu to change settings such as theme, sprite mode, and other tracker preferences."
                ),
                buildSection(
                        "Refresh",
                        "Refresh reloads hunts from disk. It is useful if you manually changed save files, imported hunts, or want to reload current data."
                )
        );

        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color: #202838;");

        Label footer = new Label(
                "All hunt data is stored locally. For extra safety, use Export All Hunts regularly to keep a backup."
        );
        footer.setWrapText(true);
        footer.setStyle(
                "-fx-text-fill: #b8c2d6;" +
                        "-fx-font-size: 14px;"
        );

        card.getChildren().addAll(intro, sections, line, footer);

        content.getChildren().addAll(topBar, card);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle(
                "-fx-background: " + AppTheme.BACKGROUND_MAIN + ";" +
                        "-fx-background-color: " + AppTheme.BACKGROUND_MAIN + ";"
        );

        return scrollPane;
    }

    private static VBox buildSection(String heading, String bodyText) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(0, 0, 0, 0));

        Label headingLabel = new Label(heading);
        headingLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;"
        );

        Label body = new Label(bodyText);
        body.setWrapText(true);
        body.setMaxWidth(Double.MAX_VALUE);
        body.setStyle(
                "-fx-text-fill: #d4dbea;" +
                        "-fx-font-size: 15px;" +
                        "-fx-line-spacing: 3px;"
        );

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: #202838;");

        box.getChildren().addAll(headingLabel, body, divider);
        return box;
    }
}