package tankgame.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Explosion {
    private int x, y;
    private BufferedImage img;
    private long startTime;
    private long duration = 500; // ms

    public Explosion(int x, int y, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.img = img;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime > duration;
    }

    public void draw(Graphics g) {
        g.drawImage(img, x, y, null);
    }
}
