package tankgame.game.powerups;

import tankgame.game.Tank;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class AnimatedPowerUps {
    protected int x, y;
    protected BufferedImage image;
    private double pulseScale = 1.0;
    private boolean scaleUp = true;

    public AnimatedPowerUps(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int imgW = image.getWidth();
        int imgH = image.getHeight();
        int centerX = x + imgW / 2;
        int centerY = y + imgH / 2;

        AffineTransform at = new AffineTransform();
        at.translate(centerX - (imgW * pulseScale) / 2, centerY - (imgH * pulseScale) / 2);
        at.scale(pulseScale, pulseScale);

        g2d.drawImage(image, at, null);
        updatePulse();
    }

    private void updatePulse() {
        if (scaleUp) {
            pulseScale += 0.01;
            if (pulseScale >= 1.15) scaleUp = false;
        } else {
            pulseScale -= 0.01;
            if (pulseScale <= 0.85) scaleUp = true;
        }
    }

    public abstract void apply(Tank tank);
}
