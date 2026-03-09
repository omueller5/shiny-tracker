package net.owen.shinytracker;

public final class ShinyOddsCalculator {

    private ShinyOddsCalculator() {
    }

    public static int getDisplayedOddsDenominator(Hunt hunt) {
        GameInfo gameInfo = GameDisplayUtil.findByDisplayName(hunt.getGame());

        int baseOdds = gameInfo != null ? gameInfo.getShinyOddsDenominator() : 8192;
        boolean charmSupported = gameInfo != null && gameInfo.isShinyCharmSupported();
        boolean fossilMethod = "Fossil Reset".equals(hunt.getMethod());

        if (fossilMethod) {
            charmSupported = false;
        }

        if (gameInfo != null && "Legends: Z-A".equals(gameInfo.getDisplayName()) && !fossilMethod) {
            int bonusRolls = 0;

            if (hunt.isShinyCharmEnabled()) {
                bonusRolls += 3;
            }

            String doughnutLevel = hunt.getDoughnutLevel();
            if ("Level 1 (+1 roll)".equals(doughnutLevel)) {
                bonusRolls += 1;
            } else if ("Level 2 (+2 rolls)".equals(doughnutLevel)) {
                bonusRolls += 2;
            } else if ("Level 3 (+3 rolls)".equals(doughnutLevel)) {
                bonusRolls += 3;
            }

            return Math.max(1, baseOdds / (1 + bonusRolls));
        }

        if (charmSupported && hunt.isShinyCharmEnabled()) {
            return Math.max(1, baseOdds / 2);
        }

        return baseOdds;
    }

    public static boolean isShinyCharmAvailable(Hunt hunt) {
        GameInfo gameInfo = GameDisplayUtil.findByDisplayName(hunt.getGame());
        if (gameInfo == null) {
            return false;
        }

        boolean fossilMethod = "Fossil Reset".equals(hunt.getMethod());
        return gameInfo.isShinyCharmSupported() && !fossilMethod;
    }

    public static double getPerEncounterProbability(Hunt hunt) {
        int denominator = getDisplayedOddsDenominator(hunt);
        return 1.0 / denominator;
    }

    public static double getCumulativeProbability(Hunt hunt) {
        return getCumulativeProbability(hunt.getResetCount(), getPerEncounterProbability(hunt));
    }

    public static double getCumulativeProbability(int attempts, double perAttemptProbability) {
        if (attempts <= 0 || perAttemptProbability <= 0) {
            return 0.0;
        }

        return 1.0 - Math.pow(1.0 - perAttemptProbability, attempts);
    }

    public static int getAttemptsForTargetProbability(Hunt hunt, double targetProbability) {
        return getAttemptsForTargetProbability(getPerEncounterProbability(hunt), targetProbability);
    }

    public static int getAttemptsForTargetProbability(double perAttemptProbability, double targetProbability) {
        if (perAttemptProbability <= 0) {
            return Integer.MAX_VALUE;
        }

        if (targetProbability <= 0) {
            return 0;
        }

        if (targetProbability >= 1.0) {
            return Integer.MAX_VALUE;
        }

        return (int) Math.ceil(Math.log(1.0 - targetProbability) / Math.log(1.0 - perAttemptProbability));
    }

    public static String formatPercent(double probability) {
        return String.format("%.2f%%", probability * 100.0);
    }

    public static String formatProbabilityLabel(Hunt hunt) {
        return "Odds: 1 / " + getDisplayedOddsDenominator(hunt);
    }
}