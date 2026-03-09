package net.owen.shinytracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class PokemonExpandedInfoLoader {

    private PokemonExpandedInfoLoader() {
    }

    public static PokemonExpandedInfo load(int dexNumber) {
        try (InputStream stream = PokemonExpandedInfoLoader.class.getResourceAsStream("/pokemon/pokemon_expanded_info.json")) {
            if (stream == null) {
                return empty();
            }

            JsonElement rootElement = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            if (!rootElement.isJsonArray()) {
                return empty();
            }

            JsonArray root = rootElement.getAsJsonArray();
            for (JsonElement element : root) {
                if (!element.isJsonObject()) {
                    continue;
                }

                JsonObject obj = element.getAsJsonObject();
                int id = getInt(obj, "dexNumber", -1);
                if (id != dexNumber) {
                    continue;
                }

                JsonObject stats = getObject(obj, "stats");
                int hp = getInt(stats, "hp", 0);
                int attack = getInt(stats, "attack", 0);
                int defense = getInt(stats, "defense", 0);
                int specialAttack = getInt(stats, "specialAttack", 0);
                int specialDefense = getInt(stats, "specialDefense", 0);
                int speed = getInt(stats, "speed", 0);

                List<String> evolutionChain = getStringList(obj, "evolutionChain");
                List<String> evolutionDetails = getStringList(obj, "evolutionDetails");

                return new PokemonExpandedInfo(
                        hp,
                        attack,
                        defense,
                        specialAttack,
                        specialDefense,
                        speed,
                        evolutionChain,
                        evolutionDetails
                );
            }
        } catch (Exception ignored) {
        }

        return empty();
    }

    private static PokemonExpandedInfo empty() {
        return new PokemonExpandedInfo(
                0, 0, 0, 0, 0, 0,
                List.of(),
                List.of()
        );
    }

    private static JsonObject getObject(JsonObject parent, String key) {
        if (parent == null || !parent.has(key) || !parent.get(key).isJsonObject()) {
            return null;
        }
        return parent.getAsJsonObject(key);
    }

    private static int getInt(JsonObject obj, String key, int fallback) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return fallback;
        }

        try {
            return obj.get(key).getAsInt();
        } catch (Exception e) {
            return fallback;
        }
    }

    private static List<String> getStringList(JsonObject obj, String key) {
        List<String> values = new ArrayList<>();

        if (obj == null || !obj.has(key) || !obj.get(key).isJsonArray()) {
            return values;
        }

        JsonArray array = obj.getAsJsonArray(key);
        for (JsonElement element : array) {
            if (element != null && !element.isJsonNull()) {
                values.add(element.getAsString());
            }
        }

        return values;
    }
}