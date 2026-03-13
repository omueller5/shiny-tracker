package net.owen.shinytracker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.lang.reflect.Method;
import java.util.List;

public final class DashboardShellView {

    private DashboardShellView() {
    }

    public static ScrollPane build(List<Hunt> hunts, AppNavigator navigator, String selectedTab) {
        VBox outerContainer = new VBox(0);
        outerContainer.setPadding(new Insets(24, 28, 28, 28));
        outerContainer.setAlignment(Pos.TOP_LEFT);

        HBox tabBar = new HBox(12);
        tabBar.setAlignment(Pos.BOTTOM_LEFT);
        tabBar.setPadding(new Insets(0, 0, 0, 4));

        Button dashboardTab = buildTabButton("Dashboard", "hunts".equals(selectedTab));
        dashboardTab.setOnAction(e -> invokeNavigatorNoArg(navigator, "showDashboard"));

        Button statsTab = buildTabButton("Stats", "stats".equals(selectedTab));
        statsTab.setOnAction(e -> invokeNavigatorNoArg(navigator, "showStats"));

        Button catchCalcTab = buildTabButton("Catch Calc", "catch".equals(selectedTab));
        catchCalcTab.setOnAction(e -> invokeNavigatorNoArg(navigator, "showCatchCalc"));

        tabBar.getChildren().addAll(dashboardTab, statsTab, catchCalcTab);

        Node content;
        switch (selectedTab) {
            case "stats" -> content = StatsView.build(hunts);
            case "catch" -> content = CatchCalcView.build();
            case "hunts" -> content = DashboardView.build(hunts, navigator);
            default -> content = DashboardView.build(hunts, navigator);
        }

        if (content instanceof Region region) {
            region.setMaxWidth(Double.MAX_VALUE);
            VBox.setVgrow(region, Priority.ALWAYS);
        }

        VBox contentPanel = new VBox(content);
        contentPanel.setAlignment(Pos.TOP_LEFT);
        contentPanel.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(contentPanel, Priority.ALWAYS);
        contentPanel.setPadding(new Insets(22));
        contentPanel.setStyle(
                "-fx-background-color: #141414;" +
                        "-fx-background-radius: 0 16px 16px 16px;" +
                        "-fx-border-color: #2c2c2c;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 0 16px 16px 16px;"
        );

        StackPane contentWrapper = new StackPane(contentPanel);
        contentWrapper.setAlignment(Pos.TOP_LEFT);
        contentWrapper.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(contentWrapper, Priority.ALWAYS);
        StackPane.setMargin(contentPanel, new Insets(-1, 0, 0, 0));

        outerContainer.getChildren().addAll(tabBar, contentWrapper);

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

        return scrollPane;
    }

    private static Button buildTabButton(String text, boolean selected) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        button.setStyle(buildTabStyle(selected, false));
        button.setMinHeight(selected ? 52 : 48);
        button.setPrefHeight(selected ? 52 : 48);
        button.setPadding(selected
                ? new Insets(12, 24, 13, 24)
                : new Insets(10, 24, 11, 24));
        button.setTranslateY(selected ? 1 : 5);
        button.setViewOrder(selected ? -1 : 0);

        button.setOnMouseEntered(e -> button.setStyle(buildTabStyle(selected, true)));
        button.setOnMouseExited(e -> button.setStyle(buildTabStyle(selected, false)));

        return button;
    }

    private static String buildTabStyle(boolean selected, boolean hovered) {
        if (selected) {
            String background = hovered ? "#4e7fff" : "#3f6ef0";
            String border = hovered ? "#7da0ff" : "#6a8cf8";
            return "-fx-background-color: " + background + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 16px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 14px 14px 0 0;" +
                    "-fx-border-color: " + border + " " + border + " transparent " + border + ";" +
                    "-fx-border-width: 1px 1px 0px 1px;" +
                    "-fx-border-radius: 14px 14px 0 0;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(63,110,240,0.30), 14, 0.18, 0, 2);";
        }

        String background = hovered ? "#242424" : "#181818";
        String border = hovered ? "#343434" : "#2a2a2a";
        String textColor = hovered ? "#d9d9d9" : "#a3a3a3";

        return "-fx-background-color: " + background + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 14px 14px 0 0;" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 14px 14px 0 0;" +
                "-fx-cursor: hand;";
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
}
