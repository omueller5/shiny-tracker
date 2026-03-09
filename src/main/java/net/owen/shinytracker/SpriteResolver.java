package net.owen.shinytracker;

import javafx.scene.image.Image;

import java.lang.reflect.Method;
import java.net.URL;

public final class SpriteResolver {

    private static final String DEFAULT_BASE = "/pokemon/sprites/pokemon";
    private static final String DEFAULT_SHINY = "/pokemon/sprites/pokemon/shiny";
    private static final String HOME_BASE = "/pokemon/sprites/pokemon/other/home";
    private static final String HOME_SHINY = "/pokemon/sprites/pokemon/other/home/shiny";

    private SpriteResolver() {
    }

    public static Image loadSprite(Class<?> resourceClass, AppSettings settings, int dexNumber, boolean shiny) {
        String spritePackMode = readSpritePackMode(settings);

        String[] paths;

        switch (spritePackMode) {
            case "sprites3d" -> paths = buildThreeDSpritePaths(dexNumber, shiny);
            case "gen2", "gen3", "gen4", "gen5", "gen6", "gen7" -> {
                paths = buildGenerationSpritePaths(spritePackMode, dexNumber, shiny);

                Image generationImage = loadImageFromPaths(resourceClass, paths);
                if (generationImage != null) {
                    return generationImage;
                }

                paths = buildDefaultSpritePaths(dexNumber, shiny);
            }
            default -> paths = buildDefaultSpritePaths(dexNumber, shiny);
        }

        return loadImageFromPaths(resourceClass, paths);
    }

    public static String readSpritePackMode(Object settings) {
        if (settings == null) {
            return "default";
        }

        String value = invokeStringGetter(settings,
                "getSpritePackMode",
                "getSelectedSpritePackMode",
                "getSpritePackSetting",
                "getSpriteStyle");

        if (value == null || value.isBlank()) {
            return "default";
        }

        return value.replace('_', ' ').trim().toLowerCase();
    }

    public static Image loadImageFromPaths(Class<?> resourceClass, String[] paths) {
        if (resourceClass == null || paths == null) {
            return null;
        }

        for (String path : paths) {
            if (path == null || path.isBlank()) {
                continue;
            }

            URL url = resourceClass.getResource(path);
            if (url != null) {
                return new Image(url.toExternalForm(), true);
            }
        }

        return null;
    }

    public static String[] buildDefaultSpritePaths(int dex, boolean shiny) {
        if (shiny) {
            return new String[] {
                    DEFAULT_SHINY + "/" + dex + ".png",
                    trimLeadingSlash(DEFAULT_SHINY) + "/" + dex + ".png",
                    DEFAULT_BASE + "/" + dex + ".png",
                    trimLeadingSlash(DEFAULT_BASE) + "/" + dex + ".png"
            };
        }

        return new String[] {
                DEFAULT_BASE + "/" + dex + ".png",
                trimLeadingSlash(DEFAULT_BASE) + "/" + dex + ".png",
                DEFAULT_SHINY + "/" + dex + ".png",
                trimLeadingSlash(DEFAULT_SHINY) + "/" + dex + ".png"
        };
    }

    public static String[] buildThreeDSpritePaths(int dex, boolean shiny) {
        if (shiny) {
            return new String[] {
                    HOME_SHINY + "/" + dex + ".png",
                    trimLeadingSlash(HOME_SHINY) + "/" + dex + ".png",
                    HOME_BASE + "/" + dex + ".png",
                    trimLeadingSlash(HOME_BASE) + "/" + dex + ".png"
            };
        }

        return new String[] {
                HOME_BASE + "/" + dex + ".png",
                trimLeadingSlash(HOME_BASE) + "/" + dex + ".png",
                HOME_SHINY + "/" + dex + ".png",
                trimLeadingSlash(HOME_SHINY) + "/" + dex + ".png"
        };
    }

    public static String[] buildGenerationSpritePaths(String spritePackMode, int dex, boolean shiny) {
        String baseFolder = resolveGenerationBaseFolder(spritePackMode, dex);
        if (baseFolder == null || baseFolder.isBlank()) {
            return new String[0];
        }

        if (shiny) {
            return new String[] {
                    baseFolder + "/shiny/" + dex + ".png",
                    trimLeadingSlash(baseFolder) + "/shiny/" + dex + ".png",
                    baseFolder + "/" + dex + ".png",
                    trimLeadingSlash(baseFolder) + "/" + dex + ".png"
            };
        }

        return new String[] {
                baseFolder + "/" + dex + ".png",
                trimLeadingSlash(baseFolder) + "/" + dex + ".png",
                baseFolder + "/shiny/" + dex + ".png",
                trimLeadingSlash(baseFolder) + "/shiny/" + dex + ".png"
        };
    }

    public static String resolveGenerationBaseFolder(String spritePackMode, int dex) {
        String[] folders = switch (spritePackMode) {
            case "gen2" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-ii/gold",
                    "/pokemon/sprites/pokemon/versions/generation-ii/silver",
                    "/pokemon/sprites/pokemon/versions/generation-ii/crystal"
            };
            case "gen3" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-iii/ruby-sapphire",
                    "/pokemon/sprites/pokemon/versions/generation-iii/emerald",
                    "/pokemon/sprites/pokemon/versions/generation-iii/firered-leafgreen"
            };
            case "gen4" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-iv/diamond-pearl",
                    "/pokemon/sprites/pokemon/versions/generation-iv/platinum",
                    "/pokemon/sprites/pokemon/versions/generation-iv/heartgold-soulsilver"
            };
            case "gen5" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-v/black-white"
            };
            case "gen6" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-vi/x-y",
                    "/pokemon/sprites/pokemon/versions/generation-vi/omegaruby-alphasapphire"
            };
            case "gen7" -> new String[] {
                    "/pokemon/sprites/pokemon/versions/generation-vii/ultra-sun-ultra-moon"
            };
            default -> null;
        };

        if (folders == null || folders.length == 0) {
            return null;
        }

        int index = Math.floorMod(dex - 1, folders.length);
        return folders[index];
    }

    public static String trimLeadingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.startsWith("/") ? value.substring(1) : value;
    }

    private static String invokeStringGetter(Object target, String... methodNames) {
        if (target == null || methodNames == null) {
            return "";
        }

        for (String methodName : methodNames) {
            if (methodName == null || methodName.isBlank()) {
                continue;
            }

            try {
                Method method = target.getClass().getMethod(methodName);
                Object value = method.invoke(target);
                if (value != null) {
                    return String.valueOf(value);
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return "";
    }
}