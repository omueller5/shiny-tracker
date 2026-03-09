package net.owen.shinytracker;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

public final class GameDisplayUtil {

    private GameDisplayUtil() {
    }

    public static final GameInfo GSC =
            new GameInfo("gsc", "Gold / Silver / Crystal", 8192, false);

    public static final GameInfo RSE =
            new GameInfo("rse", "Ruby / Sapphire / Emerald", 8192, false);

    public static final GameInfo FRLG =
            new GameInfo("frlg", "FireRed / LeafGreen", 8192, false);

    public static final GameInfo DPP =
            new GameInfo("dpp", "Diamond / Pearl / Platinum", 8192, false);

    public static final GameInfo HGSS =
            new GameInfo("hgss", "HeartGold / SoulSilver", 8192, false);

    public static final GameInfo BW =
            new GameInfo("bw", "Black / White", 8192, false);

    public static final GameInfo BW2 =
            new GameInfo("bw2", "Black 2 / White 2", 8192, true);

    public static final GameInfo XY =
            new GameInfo("xy", "X / Y", 4096, true);

    public static final GameInfo ORAS =
            new GameInfo("oras", "Omega Ruby / Alpha Sapphire", 4096, true);

    public static final GameInfo SM =
            new GameInfo("sm", "Sun / Moon", 4096, true);

    public static final GameInfo USUM =
            new GameInfo("usum", "Ultra Sun / Ultra Moon", 4096, true);

    public static final GameInfo LGPE =
            new GameInfo("lgpe", "Let's Go Pikachu / Let's Go Eevee", 4096, true);

    public static final GameInfo SWSH =
            new GameInfo("swsh", "Sword / Shield", 4096, true);

    public static final GameInfo BDSP =
            new GameInfo("bdsp", "Brilliant Diamond / Shining Pearl", 4096, true);

    public static final GameInfo PLA =
            new GameInfo("pla", "Legends: Arceus", 4096, true);

    public static final GameInfo SV =
            new GameInfo("sv", "Scarlet / Violet", 4096, true);

    public static final GameInfo ZA =
            new GameInfo("za", "Legends: Z-A", 4096, true);

    public static final GameInfo COLOSSEUM =
            new GameInfo("colosseum", "Colosseum", 8192, false);

    public static final GameInfo XD =
            new GameInfo("xd", "XD: Gale of Darkness", 8192, false);

    public static final GameInfo GO =
            new GameInfo("go", "Pokémon GO", 512, false);

    public static final List<GameInfo> GENERATION_2 = List.of(
            GSC
    );

    public static final List<GameInfo> GENERATION_3 = List.of(
            RSE,
            FRLG
    );

    public static final List<GameInfo> GENERATION_4 = List.of(
            DPP,
            HGSS
    );

    public static final List<GameInfo> GENERATION_5 = List.of(
            BW,
            BW2
    );

    public static final List<GameInfo> GENERATION_6 = List.of(
            XY,
            ORAS
    );

    public static final List<GameInfo> GENERATION_7 = List.of(
            SM,
            USUM,
            LGPE
    );

    public static final List<GameInfo> GENERATION_8 = List.of(
            SWSH,
            BDSP,
            PLA
    );

    public static final List<GameInfo> GENERATION_9 = List.of(
            SV,
            ZA
    );

    public static final List<GameInfo> SIDE_GAMES = List.of(
            COLOSSEUM,
            XD,
            GO
    );

    public static final List<GameInfo> ALL_GAMES = List.of(
            GSC,
            RSE,
            FRLG,
            DPP,
            HGSS,
            BW,
            BW2,
            XY,
            ORAS,
            SM,
            USUM,
            LGPE,
            SWSH,
            BDSP,
            PLA,
            SV,
            ZA,
            COLOSSEUM,
            XD,
            GO
    );

    public static TextFlow createStyledGameText(GameInfo gameInfo) {
        return switch (gameInfo.getId()) {
            case "gsc" -> buildTextFlow(
                    segment("Gold", "#FFD700"),
                    separator(),
                    segment("Silver", "#C0C0C0"),
                    separator(),
                    segment("Crystal", "#A7D8DE")
            );
            case "rse" -> buildTextFlow(
                    segment("Ruby", "#E0115F"),
                    separator(),
                    segment("Sapphire", "#0F52BA"),
                    separator(),
                    segment("Emerald", "#50C878")
            );
            case "frlg" -> buildTextFlow(
                    segment("FireRed", "#FF0000"),
                    separator(),
                    segment("LeafGreen", "#008000")
            );
            case "dpp" -> buildTextFlow(
                    segment("Diamond", "#B9F2FF"),
                    separator(),
                    segment("Pearl", "#F8F6F0"),
                    separator(),
                    segment("Platinum", "#E5E4E2")
            );
            case "hgss" -> buildTextFlow(
                    segment("HeartGold", "#FFBF00"),
                    separator(),
                    segment("SoulSilver", "#BFC1C2")
            );
            case "bw" -> buildTextFlow(
                    segment("Black", "#000000"),
                    separator(),
                    segment("White", "#FFFFFF")
            );
            case "bw2" -> buildTextFlow(
                    segment("Black 2", "#000000"),
                    separator(),
                    segment("White 2", "#FFFFFF")
            );
            case "xy" -> buildTextFlow(
                    segment("X", "#90D5FF"),
                    separator(),
                    segment("Y", "#FF4433")
            );
            case "oras" -> buildTextFlow(
                    segment("Omega Ruby", "#E10531"),
                    separator(),
                    segment("Alpha Sapphire", "#080F87")
            );
            case "sm" -> buildTextFlow(
                    segment("Sun", "#FFFACD"),
                    separator(),
                    segment("Moon", "#F8F8FF")
            );
            case "usum" -> buildTextFlow(
                    segment("Ultra Sun", "#FFEF00"),
                    separator(),
                    segment("Ultra Moon", "#FFF5EE")
            );
            case "lgpe" -> buildTextFlow(
                    segment("Let's Go Pikachu", "#FFFF00"),
                    separator(),
                    segment("Let's Go Eevee", "#B5651D")
            );
            case "swsh" -> buildTextFlow(
                    segment("Sword", "#4A90E2"),
                    separator(),
                    segment("Shield", "#C2185B")
            );
            case "bdsp" -> buildTextFlow(
                    segment("Brilliant Diamond", "#4EE2EC"),
                    separator(),
                    segment("Shining Pearl", "#FBF8F4")
            );
            case "pla" -> buildTextFlow(
                    segment("Legends: Arceus", "#C9B037")
            );
            case "sv" -> buildTextFlow(
                    segment("Scarlet", "#FF2400"),
                    separator(),
                    segment("Violet", "#7F00FF")
            );
            case "za" -> buildTextFlow(
                    segment("Legends: ", "#D0D0D0"),
                    segment("Z", "#2E6F40"),
                    segment("-", "#D0D0D0"),
                    segment("A", "#36454F")
            );
            case "colosseum" -> buildTextFlow(
                    segment("Colosseum", "#8B4513")
            );
            case "xd" -> buildTextFlow(
                    segment("XD: Gale of Darkness", "#4B0082")
            );
            case "go" -> buildTextFlow(
                    segment("Pokémon GO", "#00CFFF")
            );
            default -> buildTextFlow(
                    segment(gameInfo.getDisplayName(), "#FFFFFF")
            );
        };
    }

    public static GameInfo findByDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return null;
        }

        String normalizedInput = canonicalize(normalize(displayName));

        for (GameInfo gameInfo : ALL_GAMES) {
            if (gameInfo.getId().equalsIgnoreCase(normalizedInput)) {
                return gameInfo;
            }

            String normalizedDisplay = canonicalize(normalize(gameInfo.getDisplayName()));
            if (normalizedDisplay.equals(normalizedInput)) {
                return gameInfo;
            }
        }

        return null;
    }

    public static TextFlow createStyledGameTextFromDisplayName(String displayName) {
        GameInfo gameInfo = findByDisplayName(displayName);

        if (gameInfo != null) {
            return createStyledGameText(gameInfo);
        }

        return buildTextFlow(
                segment(displayName == null || displayName.isBlank() ? "-" : displayName, "#B8B8B8")
        );
    }

    private static String normalize(String value) {
        return value == null
                ? ""
                : value.toLowerCase()
                .replace("pokémon", "pokemon")
                .replace("’", "'")
                .replaceAll("[\\s\\-_/.:']+", "");
    }

    private static String canonicalize(String normalized) {
        return switch (normalized) {
            case "gsc", "goldsilvercrystal" -> "gsc";
            case "rse", "rubysapphireemerald" -> "rse";
            case "frlg", "fireredleafgreen" -> "frlg";
            case "dpp", "dppt", "diamondpearlplatinum" -> "dpp";
            case "hgss", "heartgoldsoulsilver" -> "hgss";
            case "bw", "blackwhite" -> "bw";
            case "bw2", "b2w2", "black2white2" -> "bw2";
            case "xy" -> "xy";
            case "oras", "omegarubyalphasapphire" -> "oras";
            case "sm", "sunmoon" -> "sm";
            case "usum", "ultrasunultramoon" -> "usum";
            case "lgpe", "letsgopikachuletsgoveevee" -> "lgpe";
            case "swsh", "swordshield" -> "swsh";
            case "bdsp", "brilliantdiamondshiningpearl" -> "bdsp";
            case "pla", "legendsarceus", "pokemonlegendsarceus" -> "pla";
            case "sv", "scarletviolet" -> "sv";
            case "za", "legendsza", "pokemonlegendsza" -> "za";
            case "colosseum" -> "colosseum";
            case "xd", "xdgaleofdarkness" -> "xd";
            case "go", "pokemongo" -> "go";
            default -> normalized;
        };
    }

    private static TextFlow buildTextFlow(Text... texts) {
        TextFlow flow = new TextFlow(texts);
        flow.setLineSpacing(0);
        return flow;
    }

    private static Text separator() {
        return segment(" / ", "#CFCFCF");
    }

    private static Text segment(String text, String hexColor) {
        Text segment = new Text(text);
        segment.setFill(Color.web(hexColor));
        segment.setFont(Font.font("System", FontWeight.BOLD, 17));
        return segment;
    }
}