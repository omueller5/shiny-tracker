package net.owen.shinytracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class HuntStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path HUNTS_FOLDER = Paths.get("hunts");

    public HuntStorage() {
        ensureHuntsFolderExists();
    }

    public List<Hunt> loadAllHunts() {
        ensureHuntsFolderExists();

        List<Hunt> hunts = new ArrayList<>();

        try (Stream<Path> stream = Files.list(HUNTS_FOLDER)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase()))
                    .forEach(path -> {
                        Hunt hunt = loadHunt(path);
                        if (hunt != null) {
                            hunts.add(hunt);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hunts;
    }

    public void saveHunt(Hunt hunt) {
        ensureHuntsFolderExists();

        Path filePath = HUNTS_FOLDER.resolve(hunt.getFileSafeName());

        try (Writer writer = Files.newBufferedWriter(filePath)) {
            GSON.toJson(hunt, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveHunts(Collection<Hunt> hunts) {
        if (hunts == null) {
            return;
        }

        for (Hunt hunt : hunts) {
            if (hunt != null) {
                saveHunt(hunt);
            }
        }
    }

    public void deleteHunt(Hunt hunt) {
        ensureHuntsFolderExists();

        Path filePath = HUNTS_FOLDER.resolve(hunt.getFileSafeName());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteHunts(List<Hunt> hunts) {
        for (Hunt hunt : hunts) {
            deleteHunt(hunt);
        }
    }

    private Hunt loadHunt(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            return GSON.fromJson(reader, Hunt.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void ensureHuntsFolderExists() {
        try {
            Files.createDirectories(HUNTS_FOLDER);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create hunts folder", e);
        }
    }
}
