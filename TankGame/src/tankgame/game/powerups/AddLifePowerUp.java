package tankgame.game.powerups;

import tankgame.game.PowerUp;
import tankgame.game.Tank;

import java.awt.image.BufferedImage;

public class AddLifePowerUp extends PowerUp {
    public AddLifePowerUp(int x, int y, BufferedImage image) {
        super(x, y, image);
    }

    @Override
    public void apply(Tank tank) {
        tank.addLife();  // You’ll create this method in Tank.java
    }
}
