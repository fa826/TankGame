package tankgame.game;
import java.awt.*;
import java.awt.image.BufferedImage;

// creating a wall
public class Wall {
    private int x, y;
    private BufferedImage image;

    public Wall(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }
}