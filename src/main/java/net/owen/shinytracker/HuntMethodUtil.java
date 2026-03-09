package net.owen.shinytracker;

import java.util.ArrayList;
import java.util.List;

public final class HuntMethodUtil {

    private HuntMethodUtil() {
    }

    public static List<HuntMethod> getMethodsForGame(GameInfo game) {

        List<HuntMethod> methods = new ArrayList<>();

        if (game == null) {
            methods.add(HuntMethod.RANDOM_ENCOUNTER);
            return methods;
        }

        String gameName = game.getDisplayName();

        switch (gameName) {

            case "Gold / Silver / Crystal":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.SOFT_RESET);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.BUG_CATCHING_CONTEST);
                methods.add(HuntMethod.HEADBUTT_OR_HONEY_TREE);
                methods.add(HuntMethod.STARTER_RESET);
                methods.add(HuntMethod.LEGENDARY_RESET);
                methods.add(HuntMethod.GIFT_RESET);
                methods.add(HuntMethod.EGG_HATCH);
                break;

            case "Ruby / Sapphire / Emerald":
            case "FireRed / LeafGreen":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.SOFT_RESET);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.ROAMER);
                methods.add(HuntMethod.SAFARI_ZONE);
                methods.add(HuntMethod.STARTER_RESET);
                methods.add(HuntMethod.LEGENDARY_RESET);
                methods.add(HuntMethod.GIFT_RESET);
                methods.add(HuntMethod.FOSSIL_RESET);
                methods.add(HuntMethod.EGG_HATCH);
                break;

            case "Diamond / Pearl / Platinum":
            case "HeartGold / SoulSilver":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.SOFT_RESET);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.ROAMER);
                methods.add(HuntMethod.POKE_RADAR);
                methods.add(HuntMethod.MASUDA_METHOD);
                methods.add(HuntMethod.EGG_HATCH);
                methods.add(HuntMethod.STARTER_RESET);
                methods.add(HuntMethod.LEGENDARY_RESET);
                methods.add(HuntMethod.FOSSIL_RESET);
                methods.add(HuntMethod.GIFT_RESET);
                methods.add(HuntMethod.HEADBUTT_OR_HONEY_TREE);
                methods.add(HuntMethod.SAFARI_ZONE);
                break;

            case "Black / White":
            case "Black 2 / White 2":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.SOFT_RESET);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.ROAMER);
                methods.add(HuntMethod.MASUDA_METHOD);
                methods.add(HuntMethod.EGG_HATCH);
                methods.add(HuntMethod.STARTER_RESET);
                methods.add(HuntMethod.LEGENDARY_RESET);
                methods.add(HuntMethod.GIFT_RESET);
                break;

            case "X / Y":
            case "Omega Ruby / Alpha Sapphire":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.SOFT_RESET);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.MASUDA_METHOD);
                methods.add(HuntMethod.EGG_HATCH);
                methods.add(HuntMethod.DEXNAV);
                methods.add(HuntMethod.CHAIN_FISHING);
                methods.add(HuntMethod.FRIEND_SAFARI);
                methods.add(HuntMethod.SWEET_SCENT_OR_HORDE);
                methods.add(HuntMethod.LEGENDARY_RESET);
                methods.add(HuntMethod.STARTER_RESET);
                methods.add(HuntMethod.GIFT_RESET);
                break;

            case "Sun / Moon":
            case "Ultra Sun / Ultra Moon":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.SOFT_RESET);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.MASUDA_METHOD);
                methods.add(HuntMethod.EGG_HATCH);
                methods.add(HuntMethod.SOS_CHAIN);
                methods.add(HuntMethod.ISLAND_SCAN);
                methods.add(HuntMethod.ULTRA_WORMHOLE);
                methods.add(HuntMethod.LEGENDARY_RESET);
                methods.add(HuntMethod.GIFT_RESET);
                break;

            case "Let's Go Pikachu / Let's Go Eevee":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.SOFT_RESET);
                methods.add(HuntMethod.GIFT_RESET);
                methods.add(HuntMethod.LEGENDARY_RESET);
                break;

            case "Sword / Shield":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.MASUDA_METHOD);
                methods.add(HuntMethod.EGG_HATCH);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.DYNAMAX_ADVENTURE);
                methods.add(HuntMethod.LEGENDARY_RESET);
                methods.add(HuntMethod.GIFT_RESET);
                break;

            case "Brilliant Diamond / Shining Pearl":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.SOFT_RESET);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.POKE_RADAR);
                methods.add(HuntMethod.MASUDA_METHOD);
                methods.add(HuntMethod.EGG_HATCH);
                methods.add(HuntMethod.UNDERGROUND);
                methods.add(HuntMethod.LEGENDARY_RESET);
                methods.add(HuntMethod.FOSSIL_RESET);
                methods.add(HuntMethod.GIFT_RESET);
                break;

            case "Legends: Arceus":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.MASS_OUTBREAK);
                methods.add(HuntMethod.MASSIVE_MASS_OUTBREAK);
                methods.add(HuntMethod.SPAWN_RESET);
                break;

            case "Scarlet / Violet":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.MASUDA_METHOD);
                methods.add(HuntMethod.EGG_HATCH);
                methods.add(HuntMethod.OUTBREAK);
                methods.add(HuntMethod.SANDWICH);
                methods.add(HuntMethod.SANDWICH_PLUS_OUTBREAK);
                methods.add(HuntMethod.PICNIC_RESET);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.GIFT_RESET);
                break;

            case "Legends: Z-A":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.BENCH_RESET);
                methods.add(HuntMethod.FAST_TRAVEL_RESET);
                methods.add(HuntMethod.WARP_PAD_RESET);
                methods.add(HuntMethod.SPAWN_RESET);
                methods.add(HuntMethod.TELEPORT_RESET);
                methods.add(HuntMethod.SPECIAL_SCAN_LEGENDARY_RESET);
                methods.add(HuntMethod.FOSSIL_RESET);
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                break;

            case "Colosseum":
            case "XD: Gale of Darkness":
                methods.add(HuntMethod.STATIC_ENCOUNTER);
                methods.add(HuntMethod.GIFT_RESET);
                methods.add(HuntMethod.SOFT_RESET);
                break;

            case "Pokémon GO":
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.OUTBREAK);
                methods.add(HuntMethod.GIFT_RESET);
                break;

            default:
                methods.add(HuntMethod.RANDOM_ENCOUNTER);
                methods.add(HuntMethod.SOFT_RESET);
                break;
        }

        return methods;
    }
}