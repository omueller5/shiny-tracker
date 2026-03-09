package net.owen.shinytracker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppInfo {

    private static final String DEFAULT_VERSION = "0.0.0";

    private AppInfo() {
    }

    public static String getVersion() {
        Properties properties = new Properties();

        try (InputStream inputStream = AppInfo.class.getResourceAsStream("/app.properties")) {
            if (inputStream == null) {
                return DEFAULT_VERSION;
            }

            properties.load(inputStream);
            return properties.getProperty("app.version", DEFAULT_VERSION);
        } catch (IOException e) {
            return DEFAULT_VERSION;
        }
    }

    public static String getWindowTitle() {
        return "Shiny Tracker | v" + getVersion();
    }
}