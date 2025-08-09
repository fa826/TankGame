package tankgame.menus;

import tankgame.Launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import tankgame.GameConstants;

public class StartMenuPanel extends JPanel {

    private BufferedImage menuBackground;
    private final Launcher lf;
    private JButton startButton;
    private JButton exitButton;

    public StartMenuPanel(Launcher lf) {
        Color darkOrange = new Color(255, 140, 0);
        this.lf = lf;

        try {
            menuBackground = ImageIO.read(this.getClass().getClassLoader().getResource("images/title.png"));
        } catch (IOException e) {
            System.out.println("Error cant read menu background");
            e.printStackTrace();
            System.exit(-3);
        }
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        startButton = new JButton("Start");
        startButton.setFont(new Font("Courier New", Font.BOLD, 24));
        startButton.setBounds(350, 500, 200, 60);
        startButton.setBackground(Color.BLACK);
        startButton.setForeground(darkOrange);
        startButton.setFocusPainted(false);
        startButton.setOpaque(true);                  // ← Required
        startButton.setBorderPainted(false);         // ← Optional, for a cleaner look
        startButton.setContentAreaFilled(true);      // ← Required on macOS
        startButton.addActionListener(e -> lf.setMapSelectionScreen());

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Courier New", Font.BOLD, 24));
        exitButton.setBounds(350, 570, 200, 60);
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(darkOrange);
        exitButton.setFocusPainted(false);
        exitButton.setOpaque(true);                  // ← Required
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(true);       // ← Required on macOS
        exitButton.addActionListener(e -> lf.closeGame());

        this.add(startButton);
        this.add(exitButton);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call super
        Graphics2D g2 = (Graphics2D) g;
        if (menuBackground != null) {
            g2.drawImage(menuBackground, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
