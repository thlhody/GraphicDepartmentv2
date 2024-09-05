package ctgraphdep.services;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.utils.LoggerUtil;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class LogoService {

    public void setMainImage(ImageView imageView) {
        setImage(imageView, AppPaths.CREATIVE_TIME_TASK_TRACKER);
        imageView.setFitWidth(400);
        imageView.setFitHeight(400);
        imageView.setPreserveRatio(true);
    }

    public void setHeaderLogo(ImageView imageView) {
        setImage(imageView, AppPaths.COTTONTEX_LOGO);
    }

    private void setImage(ImageView imageView, String imagePath) {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            if (image.isError()) {
                throw new IllegalArgumentException("Image failed to load: " + imagePath);
            }
            imageView.setImage(image);
            LoggerUtil.info("Image set successfully: " + imagePath);
        } catch (Exception e) {
            LoggerUtil.error("Failed to load image: " + imagePath, e);
            // Set a default image or leave the ImageView empty
            imageView.setImage(null);
        }
    }
}