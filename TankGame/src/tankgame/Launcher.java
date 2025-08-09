package tankgame;

import tankgame.game.GameWorld;
import tankgame.menus.EndGamePanel;
import tankgame.menus.StartMenuPanel;
import tankgame.menus.MapSelectionPanel;
import tankgame.menus.WinScreenPanel;
import tankgame.game.BackgroundMusicPlayer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class Launcher {
    private JPanel mainPanel;
    private GameWorld gamePanel;
    private final JFrame jf;
    private CardLayout cl;
    private String selectedTheme = "forest";
    private WinScreenPanel winScreenPanel;

    public Launcher() {
        this.jf = new JFrame();
        this.jf.setTitle("Tank Wars Game");
        this.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initUIComponents() {
        this.mainPanel = new JPanel();
        JPanel startPanel = new StartMenuPanel(this);
        JPanel mapSelectionPanel = new MapSelectionPanel(this);
        this.gamePanel = new GameWorld(this);
        this.gamePanel.InitializeGame();
        JPanel endPanel = new EndGamePanel(this);

        cl = new CardLayout();
        this.mainPanel.setLayout(cl);
        this.mainPanel.add(startPanel, "start");
        this.mainPanel.add(mapSelectionPanel, "mapSelect");
        this.mainPanel.add(gamePanel, "game");

        winScreenPanel = new WinScreenPanel(this);
        this.mainPanel.add(winScreenPanel, "win");
        this.mainPanel.add(endPanel, "end");

        this.jf.add(mainPanel);
        this.jf.setResizable(false);
        this.setFrame("start");
    }

    public void setFrame(String type) {
        this.jf.setVisible(false);
        switch (type) {
            case "start" -> this.jf.setSize(GameConstants.START_MENU_SCREEN_WIDTH, GameConstants.START_MENU_SCREEN_HEIGHT);
            case "mapSelect" -> this.jf.setSize(GameConstants.START_MENU_SCREEN_WIDTH, GameConstants.START_MENU_SCREEN_HEIGHT);
            case "game" -> {
                this.gamePanel.InitializeGame();
                this.jf.setSize(GameConstants.GAME_SCREEN_WIDTH, GameConstants.GAME_SCREEN_HEIGHT);
                BackgroundMusicPlayer.playLoop("background_music.wav");
                new Thread(this.gamePanel).start();
                this.gamePanel.requestFocusInWindow();
            }
            case "re-startGame" -> {
                if (this.gamePanel != null) {
                    this.mainPanel.remove(gamePanel);
                }
                GameWorld newGame = new GameWorld(this);
                newGame.InitializeGame();
                this.gamePanel = newGame;
                this.mainPanel.add(gamePanel, "game");
                this.jf.setSize(GameConstants.GAME_SCREEN_WIDTH, GameConstants.GAME_SCREEN_HEIGHT);
                this.cl.show(mainPanel, "game");
                new Thread(this.gamePanel).start();
                this.gamePanel.requestFocusInWindow();
                return;
            }
            case "win" -> {
                this.jf.setSize(GameConstants.GAME_SCREEN_WIDTH, GameConstants.GAME_SCREEN_HEIGHT);
                this.cl.show(mainPanel, "win");
                break;
            }
            case "end" -> {
                this.jf.setSize(GameConstants.END_MENU_SCREEN_WIDTH, GameConstants.END_MENU_SCREEN_HEIGHT);
                this.gamePanel.requestFocusInWindow();
            }
        }
        this.cl.show(mainPanel, type);
        this.jf.setVisible(true);
    }

    public JFrame getJf() {
        return jf;
    }

    public void setSelectedTheme(String theme) {
        this.selectedTheme = theme;
    }

    public String getSelectedTheme() {
        return this.selectedTheme;
    }

    public void setMapSelectionScreen() {
        this.setFrame("mapSelect");
    }

    public void startGameWithTheme(String theme) {
        setSelectedTheme(theme);
        Resources.initImages();
        setFrame("game");
    }

    public void closeGame() {
        this.jf.dispatchEvent(new WindowEvent(this.jf, WindowEvent.WINDOW_CLOSING));
    }

    public static void main(String[] args) {
        (new Launcher()).initUIComponents();
    }

    public void setWinner(String winner) {
        if (winScreenPanel != null) {
            winScreenPanel.setWinner(winner);
        }
    }
}
