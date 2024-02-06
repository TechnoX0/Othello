package Othello.Panels;

import Othello.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static Othello.GameFrame.gamePanel;

public class StatsPanel extends JPanel implements ActionListener {
    Graphics2D g2D;
    Timer timer;

    int blackDiscCount = 0;
    int whiteDiscCount = 0;

    public StatsPanel() {
        this.setPreferredSize(new Dimension(Settings.GAME_SIZE, 80));
        this.setBackground(new Color(0x23674e));
        this.setLayout(null);

        timer = new Timer(Settings.MILLI_DELAY, this);
        timer.start();
    }

    public void drawText() {
        int fontSize = 60;
        int y = this.getHeight() - (this.getHeight() - fontSize);
        int wx = (this.getWidth() - getStringWidth(g2D, String.valueOf(whiteDiscCount))) - 20;

        // Draw scores
        g2D.setPaint(Color.BLACK);
        g2D.drawString(String.valueOf(blackDiscCount), 20, y);

        g2D.setPaint(Color.WHITE);
        g2D.drawString(String.valueOf(whiteDiscCount), wx, y);

        // Game Over
        if (gamePanel.checkGameOver()) {
            String str = "Game Over!";
            g2D.drawString(str, (this.getWidth() / 2) - (getStringWidth(g2D, str) / 2), y);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g2D = (Graphics2D) g;
        g2D.setFont(new Font("Mono", Font.BOLD, 60));

        g2D.setStroke(new BasicStroke(4));
        g2D.setPaint(Color.BLACK);
        g2D.drawLine(0, this.getHeight() - 2, this.getWidth(), this.getHeight() - 2);

        drawText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    // Getters and Setters
    public void setScore(int score, char player) {
        switch (player) {
            case 'b' -> blackDiscCount = score;
            case 'w' -> whiteDiscCount = score;
        }
    }

    private int getStringWidth(Graphics2D g, String str) {
        return (int) g.getFontMetrics().getStringBounds(str, g).getWidth();
    }
}
