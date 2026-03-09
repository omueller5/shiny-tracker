package net.owen.shinytracker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main extends Application implements AppNavigator {

    private BorderPane root;
    private HuntStorage huntStorage;
    private List<Hunt> hunts;

    private boolean selectionMode = false;
    private final Set<String> selectedHuntKeys = new LinkedHashSet<>();

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        SplashScreen splash = new SplashScreen();
        splash.show();

        javafx.concurrent.Task<Void> startupTask = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() {
                updateMessage("Loading settings...");
                updateProgress(0.15, 1.0);
                SettingsStorage.loadSettings();

                updateMessage("Loading hunts...");
                updateProgress(0.40, 1.0);
                huntStorage = new HuntStorage();
                hunts = new ArrayList<>(huntStorage.loadAllHunts());

                updateMessage("Loading Pokémon data...");
                updateProgress(0.65, 1.0);

                updateMessage("Building interface...");
                updateProgress(0.85, 1.0);

                return null;
            }
        };

        startupTask.messageProperty().addListener((obs, oldValue, newValue) -> {
            splash.updateStatus(newValue);
        });

        startupTask.progressProperty().addListener((obs, oldValue, newValue) -> {
            splash.updateProgress(newValue.doubleValue());
        });

        startupTask.setOnSucceeded(event -> {
            root = new BorderPane();
            applyCurrentTheme();
            showDashboard();

            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

            double width = Math.min(AppTheme.WINDOW_WIDTH, screenWidth * 0.9);
            double height = Math.min(AppTheme.WINDOW_HEIGHT, screenHeight * 0.9);

            Scene scene = new Scene(root, width, height);

            stage.getIcons().addAll(
                    new Image(getClass().getResource("/shiny-charm.png").toExternalForm(), 16, 16, true, true),
                    new Image(getClass().getResource("/shiny-charm.png").toExternalForm(), 32, 32, true, true),
                    new Image(getClass().getResource("/shiny-charm.png").toExternalForm(), 64, 64, true, true),
                    new Image(getClass().getResource("/shiny-charm.png").toExternalForm(), 128, 128, true, true),
                    new Image(getClass().getResource("/shiny-charm.png").toExternalForm(), 256, 256, true, true)
            );

            stage.setTitle(AppInfo.getWindowTitle());
            stage.setMinWidth(AppTheme.MIN_WINDOW_WIDTH);
            stage.setMinHeight(AppTheme.MIN_WINDOW_HEIGHT);
            stage.setScene(scene);

            splash.updateStatus("Almost ready...");
            splash.updateProgress(1.0);

            javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(javafx.util.Duration.millis(250));
            pause.setOnFinished(e -> {
                splash.close();
                stage.show();
            });
            pause.play();
        });

        startupTask.setOnFailed(event -> {
            splash.close();
            startupTask.getException().printStackTrace();
        });

        Thread startupThread = new Thread(startupTask, "startup-task");
        startupThread.setDaemon(true);
        startupThread.start();
    }

    @Override
    public void showDashboard() {
        applyCurrentTheme();
        setContent(DashboardView.build(hunts, this));
    }

    @Override
    public void showCreateHunt() {
        applyCurrentTheme();
        setContent(CreateHuntView.build(hunts, huntStorage, this));
    }

    @Override
    public void showActiveHunt(Hunt hunt) {
        applyCurrentTheme();
        setContent(ActiveHuntView.build(hunt, huntStorage, this));
    }

    @Override
    public void showOptions() {
        applyCurrentTheme();
        setContent(OptionsView.build(this));
    }

    @Override
    public void showAbout() {
        setContent(AboutView.build(this));
    }

    @Override
    public void showHelp() {
        setContent(HelpView.build(this));
    }

    @Override
    public boolean isSelectionMode() {
        return selectionMode;
    }

    @Override
    public boolean isHuntSelected(Hunt hunt) {
        return selectedHuntKeys.contains(getHuntKey(hunt));
    }

    @Override
    public void toggleSelectionMode() {
        selectionMode = !selectionMode;

        if (!selectionMode) {
            selectedHuntKeys.clear();
        }

        showDashboard();
    }

    @Override
    public void toggleHuntSelection(Hunt hunt) {
        String key = getHuntKey(hunt);

        if (selectedHuntKeys.contains(key)) {
            selectedHuntKeys.remove(key);
        } else {
            selectedHuntKeys.add(key);
        }

        showDashboard();
    }

    @Override
    public void deleteHunt(Hunt hunt) {
        huntStorage.deleteHunt(hunt);
        hunts.removeIf(existing -> getHuntKey(existing).equals(getHuntKey(hunt)));
        selectedHuntKeys.remove(getHuntKey(hunt));
        showDashboard();
    }

    @Override
    public void deleteSelectedHunts() {
        List<Hunt> huntsToDelete = hunts.stream()
                .filter(hunt -> selectedHuntKeys.contains(getHuntKey(hunt)))
                .toList();

        huntStorage.deleteHunts(huntsToDelete);
        hunts.removeIf(hunt -> selectedHuntKeys.contains(getHuntKey(hunt)));

        selectedHuntKeys.clear();
        selectionMode = false;

        showDashboard();
    }

    public void exportHunt(Hunt hunt) {
        if (hunt == null) {
            showInfo("Export Hunt", "No hunt was provided to export.");
            return;
        }

        FileChooser fileChooser = createJsonSaveChooser(
                "Export Hunt",
                HuntImportExportService.buildSingleExportFileName(hunt)
        );

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file == null) {
            return;
        }

        try {
            HuntImportExportService.exportHunt(hunt, file.toPath());
            showInfo("Export Complete", "Exported 1 hunt to:\n" + file.getName());
        } catch (IOException e) {
            showError("Export Failed", "Could not export the hunt.\n\n" + e.getMessage());
        }
    }

    public void exportSelectedHunts() {
        List<Hunt> selectedHunts = hunts.stream()
                .filter(hunt -> selectedHuntKeys.contains(getHuntKey(hunt)))
                .toList();

        if (selectedHunts.isEmpty()) {
            showInfo("Export Selected Hunts", "No hunts are currently selected.");
            return;
        }

        FileChooser fileChooser = createJsonSaveChooser(
                "Export Selected Hunts",
                HuntImportExportService.buildBatchExportFileName("selected_hunts", selectedHunts.size())
        );

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file == null) {
            return;
        }

        try {
            HuntImportExportService.exportHunts(selectedHunts, file.toPath());
            showInfo("Export Complete", "Exported " + selectedHunts.size() + " selected hunt(s) to:\n" + file.getName());
        } catch (IOException e) {
            showError("Export Failed", "Could not export the selected hunts.\n\n" + e.getMessage());
        }
    }

    public void exportAllHunts() {
        if (hunts.isEmpty()) {
            showInfo("Export All Hunts", "There are no hunts to export.");
            return;
        }

        FileChooser fileChooser = createJsonSaveChooser(
                "Export All Hunts",
                HuntImportExportService.buildBatchExportFileName("all_hunts", hunts.size())
        );

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file == null) {
            return;
        }

        try {
            HuntImportExportService.exportHunts(hunts, file.toPath());
            showInfo("Export Complete", "Exported " + hunts.size() + " hunt(s) to:\n" + file.getName());
        } catch (IOException e) {
            showError("Export Failed", "Could not export all hunts.\n\n" + e.getMessage());
        }
    }

    public void importHunts() {
        FileChooser fileChooser = createJsonOpenChooser("Import Hunts");
        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
        if (files == null || files.isEmpty()) {
            return;
        }

        List<Path> paths = files.stream().map(File::toPath).toList();
        HuntImportExportService.ImportReadResult readResult = HuntImportExportService.importHunts(paths);
        HuntImportExportService.ImportApplyResult applyResult = HuntImportExportService.applyImportedHunts(hunts, readResult.getHunts());

        huntStorage.saveHunts(applyResult.getAdded());
        hunts.addAll(applyResult.getAdded());

        selectedHuntKeys.clear();
        selectionMode = false;
        showDashboard();

        StringBuilder summary = new StringBuilder();
        summary.append("Imported: ").append(applyResult.getAdded().size()).append("\n");
        summary.append("Skipped duplicates: ").append(applyResult.getDuplicates().size()).append("\n");
        summary.append("Issues: ").append(readResult.getErrors().size());

        if (!readResult.getErrors().isEmpty()) {
            String details = readResult.getErrors().stream()
                    .limit(8)
                    .collect(Collectors.joining("\n"));
            summary.append("\n\nDetails:\n").append(details);

            if (readResult.getErrors().size() > 8) {
                summary.append("\n...and ").append(readResult.getErrors().size() - 8).append(" more");
            }
        }

        showInfo("Import Complete", summary.toString());
    }

    private void refreshHunts() {
        hunts = new ArrayList<>(huntStorage.loadAllHunts());
        selectedHuntKeys.clear();
        selectionMode = false;
        showDashboard();
    }

    private String getHuntKey(Hunt hunt) {
        return hunt.getFileSafeName();
    }

    private void setContent(javafx.scene.Node content) {
        StackPane contentWrapper = new StackPane();
        contentWrapper.getChildren().add(content);

        Button menuButton = buildMenuButton();
        StackPane.setAlignment(menuButton, Pos.TOP_RIGHT);
        StackPane.setMargin(menuButton, new Insets(10));

        contentWrapper.getChildren().add(menuButton);
        root.setCenter(contentWrapper);
    }

    private void applyCurrentTheme() {
        AppSettings settings = SettingsStorage.loadSettings();
        String background = switch (settings.getThemeId()) {
            case "slate" -> "#3f434d";
            case "oled" -> "#000000";
            default -> AppTheme.BACKGROUND_MAIN;
        };
        root.setStyle("-fx-background-color: " + background + ";");
    }

    private Button buildMenuButton() {
        Button menuButton = new Button("☰");
        menuButton.setFocusTraversable(false);
        menuButton.setPrefSize(50, 40);
        menuButton.setStyle(
                "-fx-background-color: " + AppTheme.BUTTON_DARK + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 22px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-border-color: #333333;"
        );

        ContextMenu contextMenu = new ContextMenu();

        MenuItem refreshItem = new MenuItem("Refresh");
        MenuItem importItem = new MenuItem("Import Hunts...");
        MenuItem exportAllItem = new MenuItem("Export All Hunts...");
        MenuItem optionsItem = new MenuItem("Options");
        MenuItem helpItem = new MenuItem("Help");
        MenuItem aboutItem = new MenuItem("About");

        refreshItem.setOnAction(e -> refreshHunts());
        importItem.setOnAction(e -> importHunts());
        exportAllItem.setOnAction(e -> exportAllHunts());
        optionsItem.setOnAction(e -> showOptions());
        helpItem.setOnAction(e -> showHelp());
        aboutItem.setOnAction(e -> showAbout());

        contextMenu.getItems().addAll(
                refreshItem,
                importItem,
                exportAllItem,
                optionsItem,
                helpItem,
                aboutItem
        );

        menuButton.setOnAction(e -> {
            if (contextMenu.isShowing()) {
                contextMenu.hide();
            } else {
                contextMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 8);
            }
        });

        return menuButton;
    }

    private FileChooser createJsonSaveChooser(String title, String initialFileName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        chooser.setInitialFileName(initialFileName);
        return chooser;
    }

    private FileChooser createJsonOpenChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        return chooser;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
