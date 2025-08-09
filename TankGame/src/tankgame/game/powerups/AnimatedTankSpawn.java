package tankgame.game.powerups;

import tankgame.game.Tank;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class AnimatedTankSpawn {
    private boolean active = true;
    private final long startTime;
    private final int durationMs;

    public AnimatedTankSpawn(int durationMs) {
        this.durationMs = durationMs;
        this.startTime = System.currentTimeMillis();
    }

    public void draw(Graphics2D g2d, BufferedImage img, float x, float y, float angle) {
        long elapsed = System.currentTimeMillis() - startTime;
        float alpha = Math.min(1.0f, elapsed / (float) durationMs);

        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), img.getWidth() / 2.0, img.getHeight() / 2.0);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.drawImage(img, rotation, null);
        g2d.setComposite(AlphaComposite.SrcOver);

        if (alpha >= 1.0f) {
            active = false;
        }
    }

    public boolean isActive() {
        return active;
    }
}
