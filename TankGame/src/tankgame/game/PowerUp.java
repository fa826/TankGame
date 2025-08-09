package tankgame.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class PowerUp {
    protected int x, y;
    protected BufferedImage image;

    public PowerUp(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public Rectangle getHitbox() {
        if (image == null) return new Rectangle(x, y, 0, 0);
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }


    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    public abstract void apply(Tank tank);

    public static class HealthPowerUp extends PowerUp {
        public HealthPowerUp(int x, int y, BufferedImage image) {
            super(x, y, image);
        }

        @Override
        public void apply(Tank tank) {
            tank.restoreHealth();  // You’ll add this method in Tank.java
        }
    }
}
