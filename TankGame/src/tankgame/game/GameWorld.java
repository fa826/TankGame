    package tankgame.game;

    import tankgame.GameConstants;
    import tankgame.Launcher;

    import javax.imageio.ImageIO;
    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.KeyEvent;
    import java.awt.image.BufferedImage;
    import java.io.IOException;
    import java.util.Objects;
    import java.util.ArrayList;
    import java.util.Iterator;
    import java.util.concurrent.CopyOnWriteArrayList;
    import tankgame.Resources;
    import tankgame.game.powerups.AddLifePowerUp;
    import tankgame.game.powerups.SlowRotationPowerUp;
    import tankgame.game.BackgroundMusicPlayer;
    import tankgame.game.MapLoader;
    import tankgame.menus.SoundPlayer;
    import java.util.List;


    public class GameWorld extends JPanel implements Runnable {

        private BufferedImage world;
        private Tank t1;
        private Tank t2;
        private final Launcher lf;
        private ArrayList<Wall> walls = new ArrayList<>();
        private boolean gameOver = false;
        private String winner = "";
        private BufferedImage floorImg;
        private PowerUpSpawner powerUpSpawner;
        private CopyOnWriteArrayList<PowerUp> powerUps = new CopyOnWriteArrayList<>();
        private CopyOnWriteArrayList<Explosion> explosions = new CopyOnWriteArrayList<>();


        public GameWorld(Launcher lf) {
            this.lf = lf;
        }

        @Override
        public void run() {
            try {
                while (!gameOver) {
                    this.t1.update(walls, t2); // update tank
                    this.t2.update(walls, t1);

                    // PowerUp Picking check
                    powerUps.removeIf(p -> {
                        boolean hit = false;
                        if (p.getHitbox().intersects(t1.getHitbox())) {
                            p.apply(t1);
                            SoundPlayer.playSound("powerup.wav");
                            hit = true;
                        } else if (p.getHitbox().intersects(t2.getHitbox())) {
                            p.apply(t2);
                            SoundPlayer.playSound("powerup.wav");
                            hit = true;
                        }
                        return hit;
                    });

                    // calling bullet collision check in run()
                    t1.checkBulletWallCollision(walls);
                    t2.checkBulletWallCollision(walls);

                    powerUpSpawner.trySpawn();

                    // Remove finished explosions safely in logic thread
                    synchronized (explosions) {
                        explosions.removeIf(Explosion::isFinished);
                    }

                    // Check for Game Over
                    if (t1.getLives() <= 0) {
                        gameOver = true;
                        winner = "Player 2 Wins!";
                        lf.setWinner(winner);
                        BackgroundMusicPlayer.stop();
                        lf.setFrame("win");
                        return;
                    } else if (t2.getLives() <= 0) {
                        gameOver = true;
                        winner = "Player 1 Wins!";
                        lf.setWinner(winner);
                        lf.setFrame("win");
                        return;
                    }

                    // Redraw everything
                    this.repaint();
                    /*
                     * Sleep for 1000/144 ms (~6.9ms). This is done to have our
                     * loop run at a fixed rate per/sec.
                     */
                    Thread.sleep(1000 / 144);
                }
                this.repaint();

            } catch (InterruptedException ignored) {
                System.out.println(ignored);
            }
        }

        /**
         * Reset game to its initial state.
         */
        public void resetGame() {
            this.gameOver = false;
            this.winner = "";
            this.powerUps.clear();
            this.explosions.clear();
            this.walls.clear();

            // Reload map and floor
            MapLoader mapLoader = new MapLoader(lf.getSelectedTheme());
            this.floorImg = mapLoader.getFloorImage();

            BufferedImage[] powerUpImages = new BufferedImage[] {
                    Resources.images.get("resetHealth"),
                    Resources.images.get("addLife"),
                    Resources.images.get("addSpeed"),
                    Resources.images.get("slowRotate")
            };

            this.walls = mapLoader.generateRandomMap(powerUps, powerUpImages);
            this.powerUpSpawner = new PowerUpSpawner(powerUps, powerUpImages);

            // Reset tank 1
            Point spawn1 = mapLoader.getTank1Spawn();
            t1.reset(spawn1.x, spawn1.y);

            // Reset tank 2
            Point spawn2 = mapLoader.getTank2Spawn();
            t2.reset(spawn2.x, spawn2.y);

            this.setFocusable(true);
            this.requestFocusInWindow();
        }

        /**
         * Load all resources for Tank Wars Game. Set all Game Objects to their
         * initial state as well.
         */
        public void InitializeGame() {
            //resources.initResources();
            this.gameOver = false;
            this.winner = "";
            this.world = new BufferedImage(GameConstants.GAME_SCREEN_WIDTH,
                    GameConstants.GAME_SCREEN_HEIGHT,
                    BufferedImage.TYPE_INT_RGB);

            // MapLoader
            MapLoader mapLoader = new MapLoader(lf.getSelectedTheme());
            this.floorImg = mapLoader.getFloorImage();

            BufferedImage[] powerUpImages = new BufferedImage[] {
                    Resources.images.get("resetHealth"),
                    Resources.images.get("addLife"),
                    Resources.images.get("addSpeed"),
                    Resources.images.get("slowRotate")
            };

            this.walls = mapLoader.generateRandomMap(powerUps, powerUpImages);

            powerUpSpawner = new PowerUpSpawner(powerUps, powerUpImages);

            BufferedImage t1img = null;
            try {
                /*
                 * note class loaders read files from the out folder (build folder in Netbeans) and not the
                 * current working directory. When running a jar, class loaders will read from within the jar.
                 */
                BufferedImage t1Raw = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/tank1.png")));
                t1img = scaleImage(t1Raw, 50, 50);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }

            Point spawn1 = mapLoader.getTank1Spawn();
            t1 = new Tank(spawn1.x, spawn1.y, 0, 0, (short) 0, t1img, "P1", this);
            this.addKeyListener(
                    new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE)
            );


            BufferedImage t2img = null;
            try {
                /*
                 * note class loaders read files from the out folder (build folder in Netbeans) and not the
                 * current working directory. When running a jar, class loaders will read from within the jar.
                 */
                BufferedImage t2Raw = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/tank2.png")));
                t2img = scaleImage(t2Raw, 50, 50);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }

            Point spawn2 = mapLoader.getTank2Spawn();
            t2 = new Tank(spawn2.x, spawn2.y, 0, 0, (short) 180, t2img, "P2", this);
            this.addKeyListener(
                    new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER)
            );


            // This runs the tanks on GUI
            this.setFocusable(true);
            this.requestFocusInWindow();

            //Loading a wall image
            BufferedImage wallImg = null;
            try {
                wallImg = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/wall1.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedImage hpImg = null;

            try{
                hpImg = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/resetHealth.png")));
                //powerUps.add(new PowerUp.HealthPowerUp(200, 200, hpImg));
            } catch(IOException e) {
                e.printStackTrace();
            }

            BufferedImage lifeImg = null;
            try{
                lifeImg = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/addLife.png")));
                //powerUps.add(new AddLifePowerUp(400, 400, lifeImg));
            } catch  (IOException e){
                e.printStackTrace();
            }

            BufferedImage speedImg = null;
            try{
                speedImg = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/addSpeed.png")));
                //powerUps.add(new AddLifePowerUp(600, 600, speedImg));
            } catch  (IOException e){
                e.printStackTrace();
            }

            BufferedImage slowImg = null;
            try {
                slowImg = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/slowRotate.png")));
                //powerUps.add(new SlowRotationPowerUp(350, 400, slowImg)); // Position it anywhere
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void addExplosion(int x, int y) {
            try {
                BufferedImage img = ImageIO.read(getClass().getClassLoader().getResource("images/explosion.png"));
                if (img != null) {
                    explosions.add(new Explosion(x, y, img));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private BufferedImage scaleImage(BufferedImage original, int width, int height) {
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resized.createGraphics();
            g.drawImage(original, 0, 0, width, height, null);
            g.dispose();
            return resized;
        }


        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints(GameConstants.RENDER_HINTS);
            Graphics2D buffer = world.createGraphics();

            int viewWidth = GameConstants.GAME_SCREEN_WIDTH / 2;
            int viewHeight = GameConstants.GAME_SCREEN_HEIGHT;

            if (floorImg != null) {
                for (int x = 0; x < world.getWidth(); x += floorImg.getWidth()) {
                    for (int y = 0; y < world.getHeight(); y += floorImg.getHeight()) {
                        buffer.drawImage(floorImg, x, y, null);
                    }
                }
            } else {
                // === Step 1: Draw full world to buffer ===
                buffer.setColor(Color.BLACK);
                buffer.fillRect(0, 0, world.getWidth(), world.getHeight());
            }

            for (Wall wall : walls) wall.draw(buffer);

            ArrayList<PowerUp> powerUpsCopy;
            synchronized (powerUps) {
                powerUpsCopy = new ArrayList<>(powerUps);
            }
            for (PowerUp p : powerUpsCopy) {
                p.draw(buffer);
            }

            // Avoid ConcurrentModificationException by using snapshot
            ArrayList<Explosion> copyExplosions;
            synchronized (explosions) {
                copyExplosions = new ArrayList<>(explosions);
            }
            for (int i = 0; i < copyExplosions.size(); i++) {
                try {
                    copyExplosions.get(i).draw(buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if (t1.getLives() > 0) t1.drawImage(buffer);
            if (t2.getLives() > 0) t2.drawImage(buffer);

            if (gameOver) {
                buffer.setColor(Color.WHITE);
                buffer.setFont(new Font("Arial", Font.BOLD, 48));
                buffer.drawString("GAME OVER", world.getWidth() / 2 - 150, world.getHeight() / 2 - 50);
                buffer.setFont(new Font("Arial", Font.BOLD, 32));
                buffer.drawString(winner, world.getWidth() / 2 - 100, world.getHeight() / 2 + 10);
            }

            // === Step 2: Calculate camera views ===
            int cam1x = (int) t1.getX() - viewWidth / 2;
            int cam1y = (int) t1.getY() - viewHeight / 2;
            int cam2x = (int) t2.getX() - viewWidth / 2;
            int cam2y = (int) t2.getY() - viewHeight / 2;

            cam1x = Math.max(0, Math.min(world.getWidth() - viewWidth, cam1x));
            cam1y = Math.max(0, Math.min(world.getHeight() - viewHeight, cam1y));
            cam2x = Math.max(0, Math.min(world.getWidth() - viewWidth, cam2x));
            cam2y = Math.max(0, Math.min(world.getHeight() - viewHeight, cam2y));

            // === Step 3: Draw split screen views ===
            g2.drawImage(world, 0, 0, viewWidth, viewHeight, cam1x, cam1y, cam1x + viewWidth, cam1y + viewHeight, null);
            g2.drawImage(world, viewWidth, 0, viewWidth * 2, viewHeight, cam2x, cam2y, cam2x + viewWidth, cam2y + viewHeight, null);

            // === Step 4: Mini-map ===
            int miniMapWidth = 250;
            int miniMapHeight = 200;

            int miniMapX = (GameConstants.GAME_SCREEN_WIDTH / 2) - (miniMapWidth / 2);
            int miniMapY = GameConstants.GAME_SCREEN_HEIGHT - miniMapHeight - 350;

            // Draw black background to cover divider line
            g2.setColor(Color.BLACK);
            g2.fillRect(miniMapX - 2, miniMapY - 2, miniMapWidth + 4, miniMapHeight + 4);

            // Draw scaled-down world
            g2.drawImage(world, miniMapX, miniMapY, miniMapX + miniMapWidth, miniMapY + miniMapHeight,
                    0, 0, world.getWidth(), world.getHeight(), null);

            // Tank Indicators
            float scaleX = (float) miniMapWidth / world.getWidth();
            float scaleY = (float) miniMapHeight / world.getHeight();

            g2.setColor(Color.BLUE);
            g2.fillOval(miniMapX + (int) (t1.getX() * scaleX), miniMapY + (int) (t1.getY() * scaleY), 6, 6);

            g2.setColor(Color.RED);
            g2.fillOval(miniMapX + (int) (t2.getX() * scaleX), miniMapY + (int) (t2.getY() * scaleY), 6, 6);

            // mini-map border
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);

            // === Step 5: Draw borders LAST ===
            g2.setColor(Color.GRAY);  // divider
            g2.setStroke(new BasicStroke(4));
            g2.drawLine(viewWidth, 0, viewWidth, viewHeight);

            // full screen border
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(6));
            g2.drawRect(0, 0, GameConstants.GAME_SCREEN_WIDTH - 1, GameConstants.GAME_SCREEN_HEIGHT - 1);

        }
    }

