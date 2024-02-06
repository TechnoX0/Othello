package Othello;

import Othello.Panels.GamePanel;
import Othello.Panels.StatsPanel;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public static StatsPanel statsPanel = new StatsPanel();
    public static GamePanel gamePanel = new GamePanel();

    public GameFrame() {
        this.setTitle("Othello");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        this.setLayout(new BorderLayout());
        this.add(statsPanel, BorderLayout.NORTH);
        this.add(gamePanel, BorderLayout.CENTER);

        this.pack();

        centerFrame();
        this.setVisible(true);
    }

    private void centerFrame() {
        int frameWidth = this.getWidth();
        int frameHeight = this.getHeight();

        int nx = (Settings.SCREEN_WIDTH / 2) - (frameWidth / 2);
        int ny = (Settings.SCREEN_HEIGHT / 2) - (frameHeight / 2);

        this.setLocation(nx, ny);
    }
}
