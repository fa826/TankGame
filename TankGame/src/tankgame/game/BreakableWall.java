package tankgame.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BreakableWall extends Wall {
    private int health = 3; // You can adjust this

    public BreakableWall(int x, int y, BufferedImage img) {
        super(x, y, img);
    }

    public void takeDamage() {
        health--;
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    @Override
    public void draw(Graphics g) {
        if (!isDestroyed()) {
            super.draw(g);
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), getImage().getWidth(), getImage().getHeight());
    }
}
