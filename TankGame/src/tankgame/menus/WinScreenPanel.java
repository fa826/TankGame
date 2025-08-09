package tankgame.menus;

import tankgame.Launcher;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class WinScreenPanel extends JPanel {
    private final Launcher launcher;
    private final JLabel winnerLabel;
    private final JLabel tankImageLabel;
    private BufferedImage backgroundImg;

    public WinScreenPanel(Launcher launcher) {
        this.launcher = launcher;
        this.setLayout(null);

        try {
            backgroundImg = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("images/win_background.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // WINNER LABEL
        winnerLabel = new JLabel("", SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Impact", Font.BOLD, 48));
        winnerLabel.setForeground(Color.BLACK);
        winnerLabel.setBounds(380, 100, 600, 120); // ⬅️ Make it taller and lower on screen
        winnerLabel.setVerticalAlignment(SwingConstants.CENTER); // ⬅️ Ensures full vertical rendering
        this.add(winnerLabel);


        // TANK IMAGE
        tankImageLabel = new JLabel();
        tankImageLabel.setBounds(600, 420, 100, 100);  // Centered under text
        this.add(tankImageLabel);

        // RESTART BUTTON
        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Courier New", Font.BOLD, 18));
        restartButton.setBounds(670, 300, 120, 45);
        restartButton.setBackground(Color.BLACK);
        restartButton.setForeground(new Color(255, 140, 0));
        restartButton.setFocusPainted(false);
        restartButton.addActionListener((ActionEvent e) -> launcher.setMapSelectionScreen());
        this.add(restartButton);

        // EXIT BUTTON
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Courier New", Font.BOLD, 18));
        exitButton.setBounds(540, 300, 120, 45);
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(new Color(255, 140, 0));
        exitButton.setFocusPainted(false);
        exitButton.addActionListener((ActionEvent e) -> launcher.closeGame());
        this.add(exitButton);
    }

    public void setWinner(String winnerText) {
        winnerLabel.setText("<html><center>GAME OVER<br>" + winnerText + "</center></html>");

        String tankImgPath = winnerText.contains("1") ? "images/tank1.png" : "images/tank2.png";
        try {
            BufferedImage original = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResource(tankImgPath)));

            // Scale first
            Image scaledImg = original.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

            // Create a rotated version
            BufferedImage rotated = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = rotated.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.rotate(Math.toRadians(-90), 50, 50); // Rotate around center
            g2d.drawImage(scaledImg, 0, 0, null);
            g2d.dispose();

            tankImageLabel.setIcon(new ImageIcon(rotated));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
