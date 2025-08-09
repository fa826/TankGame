package tankgame.game.powerups;

import tankgame.game.PowerUp;
import tankgame.game.Tank;
import java.awt.image.BufferedImage;

public class SlowRotationPowerUp extends PowerUp {
    public SlowRotationPowerUp(int x, int y, BufferedImage image) {
        super(x, y, image);
    }

    @Override
    public void apply(Tank tank) {
        tank.applyRotationSlow(5000);  // Slow rotation for 5 seconds
    }
}
