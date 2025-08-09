package tankgame.menus;

import tankgame.Launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class MapSelectionPanel extends JPanel {
    private final Launcher launcher;
    private BufferedImage dungeonPreview;
    private BufferedImage forestPreview;
    private BufferedImage metalPreview;
    private BufferedImage backgroundImg;

    public MapSelectionPanel(Launcher launcher) {
        this.launcher = launcher;
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        try {
            backgroundImg = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("images/map_preview.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            metalPreview = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/map_metal.png")));
            dungeonPreview = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/map_dungeon.png")));
            forestPreview = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/map_forest.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Layout constants
        int buttonWidth = 180;
        int buttonHeight = 120;
        int spacing = 60;
        int totalWidth = (3 * buttonWidth) + (2 * spacing);
        int startX = (900 - totalWidth) / 2; // assuming width = 900
        int y = 250;

        // Create centered map buttons
        JButton dungeonButton = createMapButton("Dungeon", dungeonPreview, startX, y);
        JButton forestButton = createMapButton("Forest", forestPreview, startX + buttonWidth + spacing, y);
        JButton metalButton = createMapButton("Metal", metalPreview, startX + 2 * (buttonWidth + spacing), y);

        JLabel title = new JLabel("Choose Your Map");
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBounds(330, 100, 400, 40);
        this.add(title);

        //  Add map labels below each preview
        JLabel dungeonLabel = new JLabel("Dungeon", SwingConstants.CENTER);
        dungeonLabel.setBounds(startX, y + buttonHeight + 10, buttonWidth, 30);
        dungeonLabel.setForeground(Color.BLACK);
        dungeonLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        this.add(dungeonLabel);

        JLabel forestLabel = new JLabel("Forest", SwingConstants.CENTER);
        forestLabel.setBounds(startX + buttonWidth + spacing, y + buttonHeight + 10, buttonWidth, 30);
        forestLabel.setForeground(Color.BLACK);
        forestLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        this.add(forestLabel);

        JLabel metalLabel = new JLabel("Metal", SwingConstants.CENTER);
        metalLabel.setBounds(startX + 2 * (buttonWidth + spacing), y + buttonHeight + 10, buttonWidth, 30);
        metalLabel.setForeground(Color.BLACK);
        metalLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        this.add(metalLabel);


        // Add them to panel
        this.add(dungeonButton);
        this.add(forestButton);
        this.add(metalButton);
    }

    private JButton createMapButton(String theme, BufferedImage preview, int x, int y) {
        JButton button = new JButton(new ImageIcon(preview.getScaledInstance(180, 120, Image.SCALE_SMOOTH)));
        button.setBounds(x, y, 180, 120);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setToolTipText("Play " + theme + " Map");

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                launcher.startGameWithTheme(theme.toLowerCase());
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }

}
