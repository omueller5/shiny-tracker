package net.owen.shinytracker;

import javafx.scene.media.AudioClip;

public final class ShinySoundPlayer {

    private ShinySoundPlayer() {}

    public static void playForGame(String game) {
        if (game == null) return;

        String g = game.toLowerCase();
        String soundPath;

        if (g.contains("legends arceus") || g.contains("pokemon legends arceus")
                || g.contains("legends za") || g.contains("pokemon legends za")
                || g.contains("z-a") || g.contains("za")) {
            soundPath = "/sounds/shiny_pla.mp3";
        }
        else if (g.contains("scarlet") || g.contains("violet")
                || g.contains("sword") || g.contains("shield")
                || g.contains("brilliant diamond") || g.contains("shining pearl")
                || g.contains("bdsp")) {
            soundPath = "/sounds/shiny_gen9.mp3";
        }
        else if (
                g.contains("x") || g.contains("y") ||
                        g.contains("omega ruby") || g.contains("alpha sapphire") ||
                        g.contains("sun") || g.contains("moon") ||
                        g.contains("ultra sun") || g.contains("ultra moon") ||
                        g.contains("let's go")
        ) {
            soundPath = "/sounds/shiny_gen7.mp3";
        }
        else {
            soundPath = "/sounds/shiny_gen3.mp3";
        }

        try {
            AudioClip clip = new AudioClip(
                    ShinySoundPlayer.class.getResource(soundPath).toExternalForm()
            );
            clip.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}