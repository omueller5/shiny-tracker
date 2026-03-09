package net.owen.shinytracker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class SpritePackManager {

    private static final String PACKS_RESOURCE = "/pokemon/sprite_packs.json";
    private static final String HOME_BASE_PATH = "/pokemon/sprites/pokemon/other/home";

    private static List<SpritePackInfo> cachedPacks;

    private SpritePackManager() {
    }

    public static List<SpritePackInfo> getAvailablePacks() {
        if (cachedPacks == null) {
            cachedPacks = loadPacksInternal();
        }
        return List.copyOf(cachedPacks);
    }

    public static SpritePackInfo getDefaultPack() {
        return findById("home");
    }

    public static SpritePackInfo findById(String id) {
        if (id == null || id.isBlank()) {
            return getFirstAvailableOrHome();
        }

        for (SpritePackInfo pack : getAvailablePacks()) {
            if (pack.getId().equalsIgnoreCase(id.trim())) {
                return pack;
            }
        }

        return getFirstAvailableOrHome();
    }

    public static String resolveSpritePath(String packId, int dexNumber, boolean shiny, boolean female) {
        SpritePackInfo selectedPack = findById(packId);

        String selectedBase = selectedPack.getBasePath();
        String homeBase = HOME_BASE_PATH;
        String fileName = dexNumber + ".png";

        List<String> candidatePaths = new ArrayList<>();

        if (shiny) {
            candidatePaths.add(join(selectedBase, "shiny", fileName));
            candidatePaths.add(join(homeBase, "shiny", fileName));
            candidatePaths.add(join(selectedBase, fileName));
            candidatePaths.add(join(homeBase, fileName));
        } else if (female) {
            candidatePaths.add(join(selectedBase, "female", fileName));
            candidatePaths.add(join(selectedBase, fileName));
            candidatePaths.add(join(homeBase, "female", fileName));
            candidatePaths.add(join(homeBase, fileName));
        } else {
            candidatePaths.add(join(selectedBase, fileName));
            candidatePaths.add(join(homeBase, fileName));
        }

        for (String path : candidatePaths) {
            if (resourceExists(path)) {
                return path;
            }
        }

        return null;
    }

    public static String resolveSpritePath(SpritePackInfo pack, int dexNumber, boolean shiny, boolean female) {
        return resolveSpritePath(pack == null ? null : pack.getId(), dexNumber, shiny, female);
    }

    public static boolean resourceExists(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return false;
        }

        try (InputStream stream = SpritePackManager.class.getResourceAsStream(resourcePath)) {
            return stream != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static List<SpritePackInfo> loadPacksInternal() {
        try (InputStream stream = SpritePackManager.class.getResourceAsStream(PACKS_RESOURCE)) {
            if (stream == null) {
                return fallbackPacks();
            }

            Type listType = new TypeToken<List<SpritePackInfoJson>>() {}.getType();
            List<SpritePackInfoJson> rawPacks = new Gson().fromJson(
                    new InputStreamReader(stream, StandardCharsets.UTF_8),
                    listType
            );

            if (rawPacks == null || rawPacks.isEmpty()) {
                return fallbackPacks();
            }

            List<SpritePackInfo> packs = new ArrayList<>();
            for (SpritePackInfoJson raw : rawPacks) {
                if (raw == null || isBlank(raw.id) || isBlank(raw.displayName) || isBlank(raw.basePath)) {
                    continue;
                }

                packs.add(new SpritePackInfo(raw.id, raw.displayName, raw.basePath));
            }

            if (packs.isEmpty()) {
                return fallbackPacks();
            }

            packs.sort(Comparator.comparing(SpritePackInfo::getDisplayName, String.CASE_INSENSITIVE_ORDER));
            moveHomeToEnd(packs);

            return packs;
        } catch (Exception ignored) {
            return fallbackPacks();
        }
    }

    private static void moveHomeToEnd(List<SpritePackInfo> packs) {
        SpritePackInfo home = null;
        for (SpritePackInfo pack : packs) {
            if ("home".equalsIgnoreCase(pack.getId())) {
                home = pack;
                break;
            }
        }

        if (home != null) {
            packs.remove(home);
            packs.add(home);
        }
    }

    private static List<SpritePackInfo> fallbackPacks() {
        List<SpritePackInfo> packs = new ArrayList<>();
        packs.add(new SpritePackInfo("red_blue", "Red / Blue", "/pokemon/sprites/pokemon/versions/generation-i/red-blue"));
        packs.add(new SpritePackInfo("yellow", "Yellow", "/pokemon/sprites/pokemon/versions/generation-i/yellow"));
        packs.add(new SpritePackInfo("gold", "Gold", "/pokemon/sprites/pokemon/versions/generation-ii/gold"));
        packs.add(new SpritePackInfo("silver", "Silver", "/pokemon/sprites/pokemon/versions/generation-ii/silver"));
        packs.add(new SpritePackInfo("crystal", "Crystal", "/pokemon/sprites/pokemon/versions/generation-ii/crystal"));
        packs.add(new SpritePackInfo("ruby_sapphire", "Ruby / Sapphire", "/pokemon/sprites/pokemon/versions/generation-iii/ruby-sapphire"));
        packs.add(new SpritePackInfo("emerald", "Emerald", "/pokemon/sprites/pokemon/versions/generation-iii/emerald"));
        packs.add(new SpritePackInfo("firered_leafgreen", "FireRed / LeafGreen", "/pokemon/sprites/pokemon/versions/generation-iii/firered-leafgreen"));
        packs.add(new SpritePackInfo("diamond_pearl", "Diamond / Pearl", "/pokemon/sprites/pokemon/versions/generation-iv/diamond-pearl"));
        packs.add(new SpritePackInfo("platinum", "Platinum", "/pokemon/sprites/pokemon/versions/generation-iv/platinum"));
        packs.add(new SpritePackInfo("heartgold_soulsilver", "HeartGold / SoulSilver", "/pokemon/sprites/pokemon/versions/generation-iv/heartgold-soulsilver"));
        packs.add(new SpritePackInfo("black_white", "Black / White", "/pokemon/sprites/pokemon/versions/generation-v/black-white"));
        packs.add(new SpritePackInfo("x_y", "X / Y", "/pokemon/sprites/pokemon/versions/generation-vi/x-y"));
        packs.add(new SpritePackInfo("omegaruby_alphasapphire", "Omega Ruby / Alpha Sapphire", "/pokemon/sprites/pokemon/versions/generation-vi/omegaruby-alphasapphire"));
        packs.add(new SpritePackInfo("ultra_sun_ultra_moon", "Ultra Sun / Ultra Moon", "/pokemon/sprites/pokemon/versions/generation-vii/ultra-sun-ultra-moon"));
        packs.add(new SpritePackInfo("home", "HOME", HOME_BASE_PATH));
        return packs;
    }

    private static SpritePackInfo getFirstAvailableOrHome() {
        List<SpritePackInfo> packs = getAvailablePacks();
        for (SpritePackInfo pack : packs) {
            if ("home".equalsIgnoreCase(pack.getId())) {
                return pack;
            }
        }
        return packs.isEmpty() ? new SpritePackInfo("home", "HOME", HOME_BASE_PATH) : packs.get(0);
    }

    private static String join(String... parts) {
        StringBuilder builder = new StringBuilder();

        for (String part : parts) {
            if (isBlank(part)) {
                continue;
            }

            String cleaned = part.trim();
            if (builder.isEmpty()) {
                if (!cleaned.startsWith("/")) {
                    builder.append('/');
                }
                builder.append(trimSlashes(cleaned));
            } else {
                builder.append('/').append(trimSlashes(cleaned));
            }
        }

        return builder.toString();
    }

    private static String trimSlashes(String value) {
        String result = value;
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static final class SpritePackInfoJson {
        String id;
        String displayName;
        String basePath;
    }
}