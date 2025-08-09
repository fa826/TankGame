package tankgame.game;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.List;


public class PowerUpSpawner {
    private long lastSpawnTime = 0;
    private final int spawnInterval = 5000; // 5 seconds
    private final BufferedImage[] powerUpImages;
    private final Random rand = new Random();
    private final int TILE_SIZE = 50;
    private final int ROWS = 20;
    private final int COLS = 30;
    private final List<PowerUp> powerUps;

    public PowerUpSpawner(List<PowerUp> powerUps, BufferedImage[] powerUpImages) {
        this.powerUps = powerUps;
        this.powerUpImages = powerUpImages;
    }

    public void trySpawn() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime >= spawnInterval && powerUps.size() < 10) {
            int row = rand.nextInt(ROWS - 4) + 2;
            int col = rand.nextInt(COLS - 4) + 2;
            int x = col * TILE_SIZE;
            int y = row * TILE_SIZE;

            int type = rand.nextInt(powerUpImages.length);
            BufferedImage img = powerUpImages[type];
            switch (type) {
                case 0 -> powerUps.add(new PowerUp.HealthPowerUp(x, y, img));
                case 1 -> powerUps.add(new tankgame.game.powerups.AddLifePowerUp(x, y, img));
                case 2 -> powerUps.add(new tankgame.game.powerups.AddSpeedPowerUp(x, y, img));
                case 3 -> powerUps.add(new tankgame.game.powerups.SlowRotationPowerUp(x, y, img));
            }

            lastSpawnTime = currentTime;
        }
    }
}

