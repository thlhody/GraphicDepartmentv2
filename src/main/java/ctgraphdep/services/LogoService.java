package cottontex.graphdep.services;

import cottontex.graphdep.constants.AppPathsIMG;
import cottontex.graphdep.utils.LoggerUtil;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LogoService {

    public void setMainImage(ImageView imageView) {
        setImage(imageView, AppPathsIMG.CREATIVE_TIME_TASK_TRACKER);
    }

    public void setHeaderLogo(ImageView imageView) {
        setImage(imageView, AppPathsIMG.COTTONTEX_LOGO);
    }

    public void setDialogBoxImage(ImageView imageView) {
        setImage(imageView, AppPathsIMG.DIALOG_BOX_IMAGE);
    }

    public void setRefreshIcon(ImageView imageView) {
        setImage(imageView, AppPathsIMG.REFRESH_ICON);
    }

    private void setImage(ImageView imageView, String imagePath) {
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
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