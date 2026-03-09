package net.owen.shinytracker;

import javafx.scene.control.Button;

public final class UiStyles {

    private UiStyles() {
    }

    public static String buildCardStyle(boolean hovered) {
        String background = hovered ? "#1d1d1d" : "#111111";

        return "-fx-background-color: " + background + ";" +
                "-fx-background-radius: 16px;" +
                "-fx-border-color: #2e2e2e;" +
                "-fx-border-radius: 16px;" +
                "-fx-border-width: 1px;" +
                "-fx-cursor: hand;";
    }

    public static void stylePrimaryButton(Button button) {
        button.setStyle(
                "-fx-background-color: #2f2f2f;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );
    }

    public static void styleSecondaryButton(Button button) {
        button.setStyle(
                "-fx-background-color: #1d1d1d;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: #333333;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );
    }
}