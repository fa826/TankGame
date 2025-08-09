package tankgame.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.List;

public class MapLoader {
    private BufferedImage wallImage;
    private BufferedImage breakableWallImage;
    private BufferedImage floorImage;
    private final String theme;

    private final int TILE_SIZE = 50;
    private final int ROWS = 20;
    private final int COLS = 30;

    private final Point tank1Spawn = new Point(2, 2);
    private final Point tank2Spawn = new Point(COLS - 6, ROWS - 3);

    public MapLoader(String theme) {
        this.theme = theme;
        try {
            this.breakableWallImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("images/breakableWall.png")));

            switch (theme) {
                case "forest" -> {
                    wallImage = scaleImage(ImageIO.read(Objects.requireNonNull(
                            getClass().getClassLoader().getResource("images/wall_forest.png"))), 50, 50);
                    floorImage = ImageIO.read(Objects.requireNonNull(
                            getClass().getClassLoader().getResource("images/map_forest.png")));
                }
                case "metal" -> {
                    wallImage = scaleImage(ImageIO.read(Objects.requireNonNull(
                            getClass().getClassLoader().getResource("images/wall_metal.png"))), 50, 50);
                    floorImage = ImageIO.read(Objects.requireNonNull(
                            getClass().getClassLoader().getResource("images/map_metal.png")));
                }
                case "dungeon" -> {
                    wallImage = scaleImage(ImageIO.read(Objects.requireNonNull(
                            getClass().getClassLoader().getResource("images/wall_dungeon.png"))), 50, 50);
                    floorImage = ImageIO.read(Objects.requireNonNull(
                            getClass().getClassLoader().getResource("images/map_dungeon.png")));
                }
                default -> throw new IllegalArgumentException("Unknown theme: " + theme);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getFloorImage() {
        return floorImage;
    }

    public ArrayList<Wall> generateRandomMap(List<PowerUp> powerUps, BufferedImage[] powerUpImages) {
        ArrayList<Wall> walls = new ArrayList<>();
        Random rand = new Random();
        boolean[][] blocked = new boolean[ROWS][COLS];

        // Mark spawn points as blocked
        blocked[tank1Spawn.y][tank1Spawn.x] = true;
        blocked[tank2Spawn.y][tank2Spawn.x] = true;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if ((row == tank1Spawn.y && col == tank1Spawn.x) || (row == tank2Spawn.y && col == tank2Spawn.x)) continue;

                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;

                // Add border walls
                if (row == 0 || col == 0 || row == ROWS - 1 || col == COLS - 1) {
                    walls.add(new Wall(x, y, wallImage));
                    blocked[row][col] = true;
                    continue;
                }

                if (isAdjacentToBlocked(blocked, row, col)) continue;

                int chance = rand.nextInt(100);

                boolean solidWall = false;
                boolean breakableWall = false;

                switch (theme) {
                    case "dungeon" -> {
                        solidWall = chance < 18; // increase solid walls
                        breakableWall = !solidWall && chance < 38;
                    }
                    case "forest" -> {
                        solidWall = chance < 10;
                        breakableWall = !solidWall && chance < 28;
                    }
                    case "metal" -> {
                        solidWall = (row + col) % 4 == 0 && chance < 30;
                        breakableWall = !solidWall && chance < 45;
                    }
                    default -> {
                        solidWall = chance < 12;
                        breakableWall = !solidWall && chance < 30;
                    }
                }

                if (solidWall) {
                    walls.add(new Wall(x, y, wallImage));
                    blocked[row][col] = true;
                } else if (breakableWall) {
                    walls.add(new BreakableWall(x, y, breakableWallImage));
                    blocked[row][col] = true;
                } else if (chance < 48 && !isAdjacentToBlocked(blocked, row, col)) {
                    addPowerUp(x, y, rand, powerUps, powerUpImages);
                    blocked[row][col] = true;
                }
            }
        }

        return walls;
    }

    private boolean isAdjacentToBlocked(boolean[][] blocked, int row, int col) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int r = row + dr;
                int c = col + dc;
                if (r >= 0 && r < blocked.length && c >= 0 && c < blocked[0].length) {
                    if (blocked[r][c]) return true;
                }
            }
        }
        return false;
    }

    private void addPowerUp(int x, int y, Random rand, List<PowerUp> powerUps, BufferedImage[] powerUpImages) {
        if (powerUpImages == null || powerUpImages.length == 0) return;

        int type = rand.nextInt(powerUpImages.length);
        BufferedImage img = powerUpImages[type];

        switch (type) {
            case 0 -> powerUps.add(new PowerUp.HealthPowerUp(x, y, img));
            case 1 -> powerUps.add(new tankgame.game.powerups.AddLifePowerUp(x, y, img));
            case 2 -> powerUps.add(new tankgame.game.powerups.AddSpeedPowerUp(x, y, img));
            case 3 -> powerUps.add(new tankgame.game.powerups.SlowRotationPowerUp(x, y, img));
        }
    }

    private BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    public Point getTank1Spawn() {
        return new Point(tank1Spawn.x * TILE_SIZE, tank1Spawn.y * TILE_SIZE);
    }

    public Point getTank2Spawn() {
        return new Point(tank2Spawn.x * TILE_SIZE, tank2Spawn.y * TILE_SIZE);
    }
}