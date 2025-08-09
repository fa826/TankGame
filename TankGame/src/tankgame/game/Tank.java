package tankgame.game;

import tankgame.GameConstants;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.ArrayList;
import tankgame.game.BackgroundMusicPlayer;
import tankgame.menus.SoundPlayer;
import tankgame.game.powerups.AnimatedTankSpawn;


public class Tank{
    private static final int MAX_HEALTH = 100;
    private static final int BULLET_DAMAGE = 10;
    private static final float DEFAULT_SPEED = 5f;
    private static final float DEFAULT_ROTATION = 1.25f;
    private static final float BOOSTED_SPEED = 10f;
    private static final float SLOWED_ROTATION = 0.5f;
    private AnimatedTankSpawn spawnEffect;

    private static final float TANK_DIST_FROM_EDGE_X = 85;
    private static final float TANK_DIST_FROM_EDGE_Y = 75;
    
    public enum Direction {UP, DOWN, LEFT, RIGHT, SHOOT}
    private final EnumSet<Direction> keysPressed = EnumSet.noneOf(Direction.class);

    private float x;
    private float y;
    private float vx;
    private float vy;

    private float angle;
    private float R = DEFAULT_SPEED;

    private BufferedImage img;

    private ArrayList<Bullet> bullets = new ArrayList<>();
    private boolean shotFired = false;

    private String playerId;
    private int health = MAX_HEALTH;
    private int lives = 3;

    private boolean speedBoostActive = false;
    private long speedBoostEndTime = 0;

    private float rotationSpeed = DEFAULT_ROTATION;

    private boolean rotationSlowed = false;
    private long slowRotationEndTime = 0;

    private GameWorld gameWorld;




    public Tank(float x, float y, float vx, float vy, float angle, BufferedImage img, String playerId, GameWorld gameWorld) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.img = img;
        this.angle = angle;
        this.playerId = playerId;
        this.gameWorld = gameWorld;
    }

    void setX(float x){ this.x = x; }

    void setY(float y) { this. y = y;}

   public void pressed(Direction dir) {
        keysPressed.add(dir);
    }

    public void released(Direction dir) {
        keysPressed.remove(dir);
    }

    void update(ArrayList<Wall> walls, Tank otherTank) {
        if (lives <= 0) {
            return;
        }

        if (this.keysPressed.contains(Direction.UP)) {
            this.moveForwards(walls, otherTank);
        }
        if (this.keysPressed.contains(Direction.DOWN)) {
            this.moveBackwards(walls, otherTank);
        }
        if (this.keysPressed.contains(Direction.LEFT)) {
            this.rotateLeft();
        }
        if (this.keysPressed.contains(Direction.RIGHT)) {
            this.rotateRight();
        }
        if (this.keysPressed.contains(Direction.SHOOT)) {
            if (!shotFired) {
                shotFired = true;  // Block future bullets until key is released

                BufferedImage bulletImg = null;
                try {
                    bulletImg = javax.imageio.ImageIO.read(getClass().getClassLoader().getResource("images/Rocket.gif"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bulletImg != null) {
                    Bullet b = new Bullet(x, y, angle, bulletImg);
                    bullets.add(b);
                    SoundPlayer.playSound("Shoot_tank.wav");
                }
            }
        } else {
            shotFired = false;  // Reset when key is released
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);

            if (b.getHitbox().intersects(otherTank.getHitbox())) {
                otherTank.getShot();
                gameWorld.addExplosion((int) b.getX(), (int) b.getY());
                SoundPlayer.playSound("bullet.wav");
                bullets.remove(i);
                continue;
            }

            b.update();

            if (!b.isVisible()) {
                bullets.remove(i);
            }
        }


        // Check if speed boost expired
        if (speedBoostActive && System.currentTimeMillis() > speedBoostEndTime) {
            speedBoostActive = false;
            R = 5;  // Reset to default speed
            rotationSpeed = DEFAULT_ROTATION;
        }

        // Check if slow rotation expired
        if (rotationSlowed && System.currentTimeMillis() > slowRotationEndTime) {
            rotationSlowed = false;
            rotationSpeed = DEFAULT_ROTATION;
        }
    }

    private void rotateLeft() {
        this.angle -= this.rotationSpeed;
    }

    private void rotateRight() {
        this.angle += this.rotationSpeed;
    }

    private void moveBackwards(ArrayList<Wall> walls, Tank otherTank) {
        float nextX = x - (float) Math.round(R * Math.cos(Math.toRadians(angle)));
        float nextY = y - (float) Math.round(R * Math.sin(Math.toRadians(angle)));

        Rectangle futureHitbox = getFutureHitbox(nextX, nextY);

        for (Wall wall : walls) {
            if (futureHitbox.intersects(wall.getHitbox())) {
                return; // Don't move if collision with a wall
            }
        }

        if (futureHitbox.intersects(otherTank.getHitbox())) {
            return;
        }

        x = nextX;
        y = nextY;
        checkBorder();
    }


    // trying to prevent the tank from moving through the walls
    private void moveForwards(ArrayList<Wall> walls, Tank otherTank) {
        float nextX = x + (float) Math.round(R * Math.cos(Math.toRadians(angle)));
        float nextY = y + (float) Math.round(R * Math.sin(Math.toRadians(angle)));

        Rectangle futureHitbox = getFutureHitbox(nextX, nextY);

        for (Wall wall : walls) {
            if (futureHitbox.intersects(wall.getHitbox())) {
                return; // Don't move if collision with a wall
            }
        }

        if (futureHitbox.intersects(otherTank.getHitbox())) {
            return;
        }

        x = nextX;
        y = nextY;
        checkBorder();
    }


    private void checkBorder() {
        if (x < 30) {
            x = 30;
        }
        if (x >= GameConstants.GAME_SCREEN_WIDTH - TANK_DIST_FROM_EDGE_X) {
            x = GameConstants.GAME_SCREEN_WIDTH - TANK_DIST_FROM_EDGE_X;
        }
        if (y < 30) {
            y = 30;
        }
        if (y >= GameConstants.GAME_SCREEN_HEIGHT - TANK_DIST_FROM_EDGE_Y) {
            y = GameConstants.GAME_SCREEN_HEIGHT - TANK_DIST_FROM_EDGE_Y;
        }
    }

    //checkBulletWallCollision
    public void checkBulletWallCollision(ArrayList<Wall> walls) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            for (int j = 0; j < walls.size(); j++) {
                Wall w = walls.get(j);
                if (b.getHitbox().intersects(w.getHitbox())) {
                    // Handle breakable wall
                    if (w instanceof BreakableWall) {
                        BreakableWall bw = (BreakableWall) w;
                        bw.takeDamage();
                        if (bw.isDestroyed()) {
                            walls.remove(j);
                        }
                    }
                    bullets.remove(i);
                    break;
                }
            }
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }

    private Rectangle getFutureHitbox(float nextX, float nextY) {
        return new Rectangle((int) nextX, (int) nextY, img.getWidth(), img.getHeight());
    }

    public void getShot() {
        if (lives <= 0) return;

        health -= BULLET_DAMAGE;

        if (health <= 0) {
            lives--;

            if (lives > 0){
                health = MAX_HEALTH;
            } else {
                health = 0;
            }
        }
    }

    public int getLives() {
        return this.lives;
    }

    public void restoreHealth() {
        if (lives > 0 && health < 100) {
            health = 100;
        }
    }

    public void addLife() {
        if (lives < 3) {
            lives++;
        }
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public void applySpeedBoost(long durationMillis) {
        R = BOOSTED_SPEED;  // Increase speed
        speedBoostActive = true;
        speedBoostEndTime = System.currentTimeMillis() + durationMillis;
    }


    public void applyRotationSlow(long durationMillis) {
        rotationSpeed = SLOWED_ROTATION;  // slow down rotation
        rotationSlowed = true;
        slowRotationEndTime = System.currentTimeMillis() + durationMillis;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setAngle(float angle) { this.angle = angle; }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void clearAmmo() {
        this.bullets.clear();
    }


    public void reset(int x, int y) {
        this.setX(x);
        this.setY(y);
        this.setAngle(0);  // set to 180 for tank2 if needed
        this.health = MAX_HEALTH;
        this.setLives(5);
        this.clearAmmo();
    }


    void drawImage(Graphics g) {
        if (lives <= 0) return;

        Graphics2D g2d = (Graphics2D) g;
        if (spawnEffect != null && spawnEffect.isActive()) {
            spawnEffect.draw(g2d, img, x, y, angle);
        } else {
            AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
            rotation.rotate(Math.toRadians(angle), img.getWidth() / 2.0, img.getHeight() / 2.0);
            g2d.drawImage(img, rotation, null);
        }

        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        g2d.drawImage(this.img, rotation, null);

        ArrayList<Bullet> copy = new ArrayList<>(bullets);
        for (Bullet b: bullets){
            b.drawImage(g);
        }

        // set health color
        if(health >= 70) {
            g2d.setColor(Color.GREEN);
        } else if (health >= 40) {
            g2d.setColor(Color.ORANGE);
        } else {
            g2d.setColor(Color.RED);
        }

        // Draw outline + filler bar
        g2d.drawRect((int)x - 25, (int)y - 30, 100, 10);
        g2d.fillRect((int)x - 25, (int)y - 30, health, 10);

        g2d.setColor(Color.WHITE);  // Lives as white circles
        for (int i = 0; i < lives; i++) {
            g2d.fillOval((int)x - 25 + (i * 15), (int)y + 55, 10, 10);
        }

    }
}
