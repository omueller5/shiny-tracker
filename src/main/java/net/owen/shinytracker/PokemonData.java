package net.owen.shinytracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PokemonData {

    private static final List<PokemonEntry> ALL_POKEMON = new ArrayList<>();

    static {
        loadPokemonFromJson();
    }

    private PokemonData() {
    }

    private static void loadPokemonFromJson() {

        try (InputStream inputStream =
                     PokemonData.class.getResourceAsStream("/pokemon/pokedex.json")) {

            if (inputStream == null) {
                throw new RuntimeException("Could not find /pokemon/pokedex.json in resources.");
            }

            JsonArray rootArray = JsonParser.parseReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            ).getAsJsonArray();

            for (JsonElement element : rootArray) {

                JsonObject pokemonObject = element.getAsJsonObject();

                String rawId = pokemonObject.get("id").getAsString();
                int dexNumber = Integer.parseInt(rawId.replace("#", ""));

                JsonObject nameObject = pokemonObject.getAsJsonObject("name");
                String englishName = nameObject.get("english").getAsString();

                ALL_POKEMON.add(new PokemonEntry(dexNumber, englishName));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load Pokémon data from pokedex.json", e);
        }
    }

    public static List<PokemonEntry> getAllPokemon() {
        return Collections.unmodifiableList(ALL_POKEMON);
    }
}