package net.owen.shinytracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class SettingsStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path SETTINGS_PATH = Paths.get("app_settings.json");

    private SettingsStorage() {
    }

    public static AppSettings loadSettings() {
        if (!Files.exists(SETTINGS_PATH)) {
            AppSettings defaults = new AppSettings();
            saveSettings(defaults);
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(SETTINGS_PATH)) {
            AppSettings settings = GSON.fromJson(reader, AppSettings.class);
            if (settings == null) {
                settings = new AppSettings();
            }

            normalizeSettings(settings);
            return settings;
        } catch (IOException e) {
            e.printStackTrace();
            return new AppSettings();
        }
    }

    public static void saveSettings(AppSettings settings) {
        if (settings == null) {
            settings = new AppSettings();
        }

        normalizeSettings(settings);

        try (Writer writer = Files.newBufferedWriter(SETTINGS_PATH)) {
            GSON.toJson(settings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void normalizeSettings(AppSettings settings) {
        settings.setThemeId(settings.getThemeId());
        settings.setSpriteMode(settings.getSpriteMode());
        settings.setSpritePackMode(settings.getSpritePackMode());
    }
}