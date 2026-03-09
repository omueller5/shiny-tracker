package net.owen.shinytracker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreen {

    private final Stage stage;
    private final ProgressBar progressBar;
    private final Label statusLabel;

    public SplashScreen() {
        stage = new Stage(StageStyle.UNDECORATED);

        ImageView iconView = new ImageView(
                new Image(getClass().getResource("/shiny-charm.png").toExternalForm())
        );
        iconView.setFitWidth(96);
        iconView.setFitHeight(96);
        iconView.setPreserveRatio(true);

        Label titleLabel = new Label("Shiny Tracker");
        titleLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;"
        );

        statusLabel = new Label("Starting...");
        statusLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.95);" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: 600;"
        );

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(280);
        progressBar.setPrefHeight(14);
        progressBar.setStyle("-fx-accent: white;");

        VBox root = new VBox(18, iconView, titleLabel, statusLabel, progressBar);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #63BEFF, #3E9EF2);"
        );

        Scene scene = new Scene(root, 900, 600);

        stage.setScene(scene);
        stage.setTitle("Shiny Tracker");
    }

    public void show() {
        stage.show();
        stage.centerOnScreen();
    }

    public void updateProgress(double progress) {
        progressBar.setProgress(progress);
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    public void close() {
        stage.close();
    }
}