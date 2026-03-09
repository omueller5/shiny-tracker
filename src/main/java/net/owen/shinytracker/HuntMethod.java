package net.owen.shinytracker;

public enum HuntMethod {

    RANDOM_ENCOUNTER("Random Encounter", "RE"),
    SOFT_RESET("Soft Reset", "SR"),
    STATIC_ENCOUNTER("Static Encounter", "SE"),
    ROAMER("Roamer", "Roamer"),
    RUN_AWAY("Run Away", "RA"),
    MASUDA_METHOD("Masuda Method", "Masuda"),
    EGG_HATCH("Egg Hatch", "Egg"),
    STARTER_RESET("Starter Reset", "Starter"),
    LEGENDARY_RESET("Legendary Reset", "Legendary"),
    GIFT_RESET("Gift Reset",  "Gift"),
    FOSSIL_RESET("Fossil Reset",  "Fossil"),
    SAFARI_ZONE("Safari Zone", "SZ"),
    BUG_CATCHING_CONTEST("Bug-Catching Contest", "BCC"),
    HEADBUTT_OR_HONEY_TREE("Headbutt / Honey Tree", "Tree"),
    SWEET_SCENT_OR_HORDE("Sweet Scent / Horde", "Hoard"),
    POKE_RADAR("Poké Radar", "Radar"),
    CHAIN_FISHING("Chain Fishing", "CF"),
    FRIEND_SAFARI("Friend Safari", "FS"),
    DEXNAV("DexNav", "DexNav"),
    SOS_CHAIN("SOS Chain", "SOS"),
    ISLAND_SCAN("Island Scan", "Scan"),
    ULTRA_WORMHOLE("Ultra Wormhole", "UW"),
    DYNAMAX_ADVENTURE("Dynamax Adventure", "DA"),
    UNDERGROUND("Underground / Grand Underground", "Underground"),
    OUTBREAK("Outbreak", "Outbreak"),
    MASS_OUTBREAK("Mass Outbreak", "Mass Outbreak"),
    MASSIVE_MASS_OUTBREAK("Massive Mass Outbreak",  "Massive Mass Outbreak"),
    SANDWICH("Sandwich", "Sandwich"),
    SANDWICH_PLUS_OUTBREAK("Sandwich + Outbreak",  "Sandwich + Outbreak"),
    PICNIC_RESET("Picnic Reset", "PR"),
    SPAWN_RESET("Spawn Reset", "Spawn"),
    TELEPORT_RESET("Teleport Reset", "TR"),
    BENCH_RESET("Bench Reset", "BR"),
    FAST_TRAVEL_RESET("Fast Travel Reset", "Fast Travel"),
    WARP_PAD_RESET("Warp Pad Reset", "Warp Pad"),
    SPECIAL_SCAN_LEGENDARY_RESET("Special Scan Legendary Reset", "Scan"),
    CUSTOM("Custom", "Custom");
    private final String displayName;
    private final String shortName;

    HuntMethod(String displayName, String shortName) {
        this.displayName = displayName;
        this.shortName = shortName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public static HuntMethod fromDisplayName(String name) {
        for (HuntMethod method : values()) {
            if (method.displayName.equals(name)) {
                return method;
            }
        }
        return CUSTOM;
    }

    @Override
    public String toString() {
        return displayName;
    }
}