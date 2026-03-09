package net.owen.shinytracker;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public final class SpriteUtil {

    private static final String BASE_PATH = "/pokemon/sprites/pokemon/";
    private static final String SHINY_PATH = "/pokemon/sprites/pokemon/shiny/";
    private static final String FALLBACK_FILE = "0.png";

    private SpriteUtil() {
    }

    public static Image loadPokemonSprite(int dexNumber, boolean shiny) {
        String fileName = dexNumber + ".png";
        String path = shiny ? SHINY_PATH + fileName : BASE_PATH + fileName;

        InputStream spriteStream = SpriteUtil.class.getResourceAsStream(path);
        if (spriteStream != null) {
            return new Image(spriteStream);
        }

        String fallbackPath = shiny ? SHINY_PATH + FALLBACK_FILE : BASE_PATH + FALLBACK_FILE;
        InputStream fallbackStream = SpriteUtil.class.getResourceAsStream(fallbackPath);

        if (fallbackStream != null) {
            return new Image(fallbackStream);
        }

        return null;
    }

    public static ImageView createPokemonSpriteView(int dexNumber, boolean shiny, double fitWidth, double fitHeight) {
        Image image = loadPokemonSprite(dexNumber, shiny);

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);

        if (image != null) {
            imageView.setImage(image);
        }

        return imageView;
    }
}