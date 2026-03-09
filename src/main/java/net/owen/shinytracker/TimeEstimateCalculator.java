package net.owen.shinytracker;

public final class TimeEstimateCalculator {

    private TimeEstimateCalculator() {
    }

    public static double getSecondsPerAttempt(Hunt hunt) {
        String preset = hunt.getResetPreset();
        String method = hunt.getMethod();

        if (preset != null) {
            switch (preset) {
                case "Bench (19s)":
                    return 19.0;
                case "Fast Travel (5s)":
                    return 5.0;
                case "Fast Travel (4.5s)":
                    return 4.5;
                case "Warp Pad (6s)":
                    return 6.0;
                case "Custom":
                    return getDefaultSecondsForMethod(method);
                default:
                    return getDefaultSecondsForMethod(method);
            }
        }

        return getDefaultSecondsForMethod(method);
    }

    private static double getDefaultSecondsForMethod(String method) {
        if (method == null) {
            return 10.0;
        }

        switch (method) {
            case "Soft Reset":
                return 20.0;
            case "Random Encounter":
                return 10.0;
            case "Static Encounter":
                return 15.0;
            case "Roamer":
                return 12.0;
            case "Run Away":
                return 8.0;
            case "Masuda Method":
                return 45.0;
            case "Egg Hatch":
                return 45.0;
            case "Starter Reset":
                return 25.0;
            case "Legendary Reset":
                return 25.0;
            case "Gift Reset":
                return 20.0;
            case "Fossil Reset":
                return 30.0;
            case "Safari Zone":
                return 12.0;
            case "Bug-Catching Contest":
                return 12.0;
            case "Headbutt / Honey Tree":
                return 15.0;
            case "Sweet Scent / Horde":
                return 8.0;
            case "Poké Radar":
                return 10.0;
            case "Chain Fishing":
                return 8.0;
            case "Friend Safari":
                return 8.0;
            case "DexNav":
                return 10.0;
            case "SOS Chain":
                return 12.0;
            case "Island Scan":
                return 12.0;
            case "Ultra Wormhole":
                return 15.0;
            case "Dynamax Adventure":
                return 900.0;
            case "Underground / Grand Underground":
                return 10.0;
            case "Outbreak":
                return 8.0;
            case "Mass Outbreak":
                return 8.0;
            case "Massive Mass Outbreak":
                return 10.0;
            case "Sandwich":
                return 8.0;
            case "Sandwich + Outbreak":
                return 8.0;
            case "Picnic Reset":
                return 10.0;
            case "Spawn Reset":
                return 8.0;
            case "Teleport Reset":
                return 6.0;
            case "Bench Reset":
                return 19.0;
            case "Fast Travel Reset":
                return 5.0;
            case "Warp Pad Reset":
                return 6.0;
            case "Special Scan Legendary Reset":
                return 8.0;
            default:
                return 10.0;
        }
    }

    public static long getAverageAttemptsForAnyShiny(Hunt hunt) {
        double probability = ShinyOddsCalculator.getPerEncounterProbability(hunt);
        if (probability <= 0) {
            return Long.MAX_VALUE;
        }

        return Math.round(1.0 / probability);
    }

    public static String getAverageTimeForAnyShiny(Hunt hunt) {
        long averageAttempts = getAverageAttemptsForAnyShiny(hunt);
        double secondsPerAttempt = getSecondsPerAttempt(hunt);

        double totalSeconds = averageAttempts * secondsPerAttempt;
        return formatDuration(totalSeconds);
    }

    public static String getMilestoneTime(Hunt hunt, double targetProbability) {
        int attempts = ShinyOddsCalculator.getAttemptsForTargetProbability(hunt, targetProbability);
        double secondsPerAttempt = getSecondsPerAttempt(hunt);

        double totalSeconds = attempts * secondsPerAttempt;
        return formatDuration(totalSeconds);
    }

    public static int getMilestoneAttempts(Hunt hunt, double targetProbability) {
        return ShinyOddsCalculator.getAttemptsForTargetProbability(hunt, targetProbability);
    }

    public static int getEquivalentSpawnCount(Hunt hunt) {
        String game = hunt.getGame();
        String method = hunt.getMethod();

        if ("Legends: Z-A".equals(game)) {
            if ("Bench Reset".equals(method)) {
                return 8;
            }
            if ("Fast Travel Reset".equals(method)) {
                return hunt.isHyperspaceHunt() ? 1 : 8;
            }
            if ("Warp Pad Reset".equals(method)) {
                return 1;
            }
            if ("Teleport Reset".equals(method)) {
                return 1;
            }
            if ("Spawn Reset".equals(method)) {
                return 1;
            }
            if ("Special Scan Legendary Reset".equals(method)) {
                return 1;
            }
        }

        return 1;
    }

    public static String formatDuration(double totalSeconds) {
        long roundedSeconds = Math.max(0, Math.round(totalSeconds));

        long hours = roundedSeconds / 3600;
        long minutes = (roundedSeconds % 3600) / 60;
        long seconds = roundedSeconds % 60;

        return hours + "h " + minutes + "m " + seconds + "s";
    }
}