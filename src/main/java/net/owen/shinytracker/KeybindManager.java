package net.owen.shinytracker;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class KeybindManager {

    private final Map<KeybindAction, KeyCombination> bindings = new EnumMap<>(KeybindAction.class);
    private final Map<KeybindAction, Runnable> actionHandlers = new EnumMap<>(KeybindAction.class);
    private final Set<Scene> installedScenes = java.util.Collections.newSetFromMap(new IdentityHashMap<>());

    public KeybindManager() {
        loadDefaultBindings();
    }

    public final void loadDefaultBindings() {
        bindings.clear();
        bindings.put(KeybindAction.INCREMENT_COUNTER, KeyCombination.valueOf("Add"));
        bindings.put(KeybindAction.DECREMENT_COUNTER, KeyCombination.valueOf("Subtract"));
        bindings.put(KeybindAction.OPEN_HUNT_TAB, KeyCombination.valueOf("Ctrl+1"));
        bindings.put(KeybindAction.OPEN_POKEMON_TAB, KeyCombination.valueOf("Ctrl+2"));
        bindings.put(KeybindAction.OPEN_CATCH_TAB, KeyCombination.valueOf("Ctrl+3"));
    }

    public void loadFromSettings(AppSettings settings) {
        loadDefaultBindings();

        if (settings == null || settings.getKeybinds() == null || settings.getKeybinds().isEmpty()) {
            return;
        }

        for (KeybindAction action : KeybindAction.values()) {
            String value = settings.getKeybinds().get(action.name());
            if (value == null || value.isBlank()) {
                continue;
            }

            try {
                bindings.put(action, KeyCombination.valueOf(value));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void saveToSettings(AppSettings settings) {
        if (settings == null) {
            return;
        }

        Map<String, String> stored = new java.util.LinkedHashMap<>();
        for (Map.Entry<KeybindAction, KeyCombination> entry : bindings.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                stored.put(entry.getKey().name(), entry.getValue().getName());
            }
        }

        settings.setKeybinds(stored);
    }

    public String getBindingDisplay(KeybindAction action) {
        KeyCombination combo = bindings.get(action);
        return combo == null ? "" : combo.getDisplayText();
    }

    public void registerAction(KeybindAction action, Runnable handler) {
        if (action == null || handler == null) {
            return;
        }
        actionHandlers.put(action, handler);
    }

    public void install(Scene scene) {
        if (scene == null || installedScenes.contains(scene)) {
            return;
        }

        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        installedScenes.add(scene);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event == null || shouldIgnore(event)) {
            return;
        }

        for (Map.Entry<KeybindAction, KeyCombination> entry : bindings.entrySet()) {
            KeybindAction action = entry.getKey();
            KeyCombination combination = entry.getValue();

            if (combination != null && combination.match(event)) {
                Runnable handler = actionHandlers.get(action);
                if (handler != null) {
                    handler.run();
                    event.consume();
                }
                return;
            }
        }
    }

    private boolean shouldIgnore(KeyEvent event) {
        Object target = event.getTarget();
        if (!(target instanceof Node node)) {
            return false;
        }

        if (node instanceof TextInputControl) {
            return true;
        }

        return isInsideEditableControl(node);
    }

    private boolean isInsideEditableControl(Node node) {
        Node current = node;

        while (current != null) {
            if (current instanceof TextInputControl) {
                return true;
            }

            if (current instanceof ComboBoxBase<?> comboBoxBase && comboBoxBase.isEditable()) {
                return true;
            }

            current = current.getParent();
        }

        return false;
    }

    public KeyCombination getBinding(KeybindAction action) {
        return bindings.get(action);
    }

    public void setBinding(KeybindAction action, KeyCombination combination) {
        if (action == null || combination == null) {
            return;
        }
        bindings.put(action, combination);
    }

    public Map<KeybindAction, KeyCombination> getBindings() {
        return Map.copyOf(bindings);
    }
}
