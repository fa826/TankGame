package tankgame.game;

// creating a bullet
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import tankgame.GameConstants;


public class Bullet {
    private float x,y;
    private float angle;
    private float speed = 18f;
    //private Rectangle hitBox;
    private BufferedImage image;
    private boolean visible = true;

    public Bullet(float x, float y, float angle, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.image = image;this.visible = true;
        //this.hitBox = new Rectangle((int)x, (int)y, this.img.getWidth(), this.img.getHeight());
    }

    public void update() {
        x += speed * Math.cos(Math.toRadians(angle));
        y += speed * Math.sin(Math.toRadians(angle));
        checkBounds();
    }

    private void checkBounds() {
        if (x < 0 || y< 0 ||
                x > GameConstants.GAME_SCREEN_WIDTH ||
                y > GameConstants.GAME_SCREEN_HEIGHT) {
            visible = false;
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle((int)x, (int)y, image.getWidth(), image.getHeight());
    }


    public void drawImage(Graphics g) {
        if (!visible) return;

        Graphics2D g2d = (Graphics2D) g;

        // Draws bullet image
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);;
        at.rotate(Math.toRadians(angle), image.getWidth() / 2.0, image.getHeight() / 2.0);
        g2d.drawImage(image, at, null);

        // Drawing red rectangle around bullet (for visibility)
        g2d.setColor(Color.RED);
        g2d.draw(getHitbox());
    }

    public boolean isVisible() {
        return visible;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

}
