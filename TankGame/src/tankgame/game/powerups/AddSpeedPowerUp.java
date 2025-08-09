package tankgame.game.powerups;

import tankgame.game.PowerUp;
import tankgame.game.Tank;

import java.awt.image.BufferedImage;

public class AddSpeedPowerUp extends PowerUp {
    public AddSpeedPowerUp(int x, int y, BufferedImage image) {
        super(x, y, image);
    }

    @Override
    public void apply(Tank tank) {
        tank.applySpeedBoost(5000);  // boost for 5 seconds
    }
}
