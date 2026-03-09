package net.owen.shinytracker;

import java.util.HashMap;
import java.util.Map;

public class AppSettings {

    private boolean showPlusMinusTenButtons = false;
    private String themeId = "dark";
    private String spriteMode = "default";
    private String spritePackMode = "default";
    private Map<String, String> keybinds = new HashMap<>();

    public AppSettings() {
    }

    public boolean isShowPlusMinusTenButtons() {
        return showPlusMinusTenButtons;
    }

    public void setShowPlusMinusTenButtons(boolean showPlusMinusTenButtons) {
        this.showPlusMinusTenButtons = showPlusMinusTenButtons;
    }

    public String getThemeId() {
        return themeId == null || themeId.isBlank() ? "dark" : themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = (themeId == null || themeId.isBlank()) ? "dark" : themeId;
    }

    public String getSpriteMode() {
        return spriteMode == null || spriteMode.isBlank() ? "default" : spriteMode;
    }

    public void setSpriteMode(String spriteMode) {
        this.spriteMode = (spriteMode == null || spriteMode.isBlank()) ? "default" : spriteMode;
    }

    public String getSpritePackMode() {
        return spritePackMode == null || spritePackMode.isBlank() ? "default" : spritePackMode;
    }

    public void setSpritePackMode(String spritePackMode) {
        this.spritePackMode = (spritePackMode == null || spritePackMode.isBlank()) ? "default" : spritePackMode;
    }

    public Map<String, String> getKeybinds() {
        return keybinds;
    }

    public void setKeybinds(Map<String, String> keybinds) {
        this.keybinds = keybinds == null ? new HashMap<>() : new HashMap<>(keybinds);
    }
}
