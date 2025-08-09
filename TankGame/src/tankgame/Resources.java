package tankgame;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Resources {
    public static final HashMap<String, BufferedImage> images = new HashMap<>();

    public static void initImages() {
        try {
            images.put("resetHealth", ImageIO.read(Objects.requireNonNull(Resources.class.getClassLoader().getResource("images/resetHealth.png"))));
            images.put("addLife", ImageIO.read(Objects.requireNonNull(Resources.class.getClassLoader().getResource("images/addLife.png"))));
            images.put("addSpeed", ImageIO.read(Objects.requireNonNull(Resources.class.getClassLoader().getResource("images/addSpeed.png"))));
            images.put("slowRotate", ImageIO.read(Objects.requireNonNull(Resources.class.getClassLoader().getResource("images/slowRotate.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
