package cottontex.graphdep.services;

import cottontex.graphdep.utils.LoggerUtil;
import javafx.scene.image.Image;

import java.io.InputStream;

public class DialogService {

    public Image loadImage(String imagePath) {
        try {
            LoggerUtil.info("Loading image: " + imagePath);
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                return new Image(imageStream);
            } else {
                LoggerUtil.error("Failed to load image: " + imagePath);
                return null;
            }
        } catch (Exception e) {
            LoggerUtil.error("Error loading image: " + e.getMessage(), e);
            return null;
        }
    }
}