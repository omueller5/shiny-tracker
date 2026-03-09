package net.owen.shinytracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class HuntImportExportService {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private HuntImportExportService() {
    }

    public static void exportHunt(Hunt hunt, Path file) throws IOException {
        if (hunt == null) {
            throw new IllegalArgumentException("Hunt cannot be null.");
        }

        exportHunts(List.of(hunt), file);
    }

    public static void exportHunts(Collection<Hunt> hunts, Path file) throws IOException {
        if (hunts == null || hunts.isEmpty()) {
            throw new IllegalArgumentException("There are no hunts to export.");
        }

        if (file == null) {
            throw new IllegalArgumentException("Export file cannot be null.");
        }

        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        ExportBundle bundle = new ExportBundle();
        bundle.formatVersion = 1;
        bundle.exportedAt = OffsetDateTime.now().toString();
        bundle.hunts = new ArrayList<>(hunts);

        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(bundle, writer);
        }
    }

    public static ImportReadResult importHunts(Collection<Path> files) {
        ImportReadResult result = new ImportReadResult();

        if (files == null || files.isEmpty()) {
            return result;
        }

        for (Path file : files) {
            if (file == null) {
                continue;
            }

            try (Reader reader = Files.newBufferedReader(file)) {
                String json = readAll(reader);
                if (json.isBlank()) {
                    result.errors.add(file.getFileName() + ": file was empty.");
                    continue;
                }

                List<Hunt> huntsFromFile = parseHunts(json);
                if (huntsFromFile.isEmpty()) {
                    result.errors.add(file.getFileName() + ": no hunts found.");
                    continue;
                }

                for (Hunt hunt : huntsFromFile) {
                    if (hunt == null) {
                        result.errors.add(file.getFileName() + ": encountered a null hunt entry.");
                        continue;
                    }

                    sanitizeImportedHunt(hunt);
                    result.hunts.add(hunt);
                }
            } catch (IOException e) {
                result.errors.add(file.getFileName() + ": " + e.getMessage());
            } catch (JsonParseException | IllegalStateException e) {
                result.errors.add(file.getFileName() + ": invalid JSON.");
            }
        }

        return result;
    }

    public static ImportApplyResult applyImportedHunts(Collection<Hunt> existingHunts, Collection<Hunt> importedHunts) {
        List<Hunt> added = new ArrayList<>();
        List<Hunt> duplicates = new ArrayList<>();

        Set<String> seenKeys = new LinkedHashSet<>();
        if (existingHunts != null) {
            for (Hunt existing : existingHunts) {
                if (existing != null) {
                    seenKeys.add(existing.getFileSafeName());
                }
            }
        }

        if (importedHunts != null) {
            for (Hunt hunt : importedHunts) {
                if (hunt == null) {
                    continue;
                }

                String key = hunt.getFileSafeName();
                if (seenKeys.contains(key)) {
                    duplicates.add(hunt);
                } else {
                    added.add(hunt);
                    seenKeys.add(key);
                }
            }
        }

        return new ImportApplyResult(added, duplicates);
    }

    public static String buildSingleExportFileName(Hunt hunt) {
        if (hunt == null) {
            return "hunt.json";
        }

        String fileName = hunt.getFileSafeName();
        return (fileName == null || fileName.isBlank()) ? "hunt.json" : fileName;
    }

    public static String buildBatchExportFileName(String prefix, int count) {
        String safePrefix = sanitizeFilePart(prefix == null || prefix.isBlank() ? "hunts" : prefix);
        if (count <= 1) {
            return safePrefix + ".json";
        }
        return safePrefix + "_" + count + "_hunts.json";
    }

    private static List<Hunt> parseHunts(String json) {
        ExportBundle bundle = GSON.fromJson(json, ExportBundle.class);
        if (bundle != null && bundle.hunts != null) {
            return bundle.hunts;
        }

        Hunt singleHunt = GSON.fromJson(json, Hunt.class);
        if (singleHunt != null) {
            return List.of(singleHunt);
        }

        return List.of();
    }

    private static String readAll(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }

    private static void sanitizeImportedHunt(Hunt hunt) {
        hunt.setPokemonName(defaultString(hunt.getPokemonName(), "Unknown"));
        hunt.setGame(defaultString(hunt.getGame(), "Unknown"));
        hunt.setMethod(defaultString(hunt.getMethod(), "Unknown"));
        hunt.setResetPreset(defaultString(hunt.getResetPreset(), ""));
        hunt.setDoughnutLevel(defaultString(hunt.getDoughnutLevel(), ""));
        hunt.setStartTime(defaultString(hunt.getStartTime(), ""));
        hunt.setCompletedTime(defaultString(hunt.getCompletedTime(), ""));
        hunt.setResetCount(hunt.getResetCount());
        hunt.setTotalEncounters(hunt.getTotalEncounters());

        if (hunt.getPhases() == null) {
            hunt.setPhases(new ArrayList<>());
        }
    }

    private static String defaultString(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private static String sanitizeFilePart(String value) {
        return value.toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }

    private static final class ExportBundle {
        int formatVersion;
        String exportedAt;
        List<Hunt> hunts = new ArrayList<>();
    }

    public static final class ImportReadResult {
        private final List<Hunt> hunts = new ArrayList<>();
        private final List<String> errors = new ArrayList<>();

        public List<Hunt> getHunts() {
            return hunts;
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    public static final class ImportApplyResult {
        private final List<Hunt> added;
        private final List<Hunt> duplicates;

        public ImportApplyResult(List<Hunt> added, List<Hunt> duplicates) {
            this.added = added;
            this.duplicates = duplicates;
        }

        public List<Hunt> getAdded() {
            return added;
        }

        public List<Hunt> getDuplicates() {
            return duplicates;
        }
    }
}
