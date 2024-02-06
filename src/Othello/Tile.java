package Othello;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import static Othello.GameFrame.gamePanel;

public class Tile extends JButton {
    private final int x, y;
    private final int ix, iy;
    private Image image;
    private String color, imageName;

    // Animation variables
    HashMap<String, String[]> frames = new HashMap<>();
    public boolean isFlipped = false;
    public int animationDelay = 0;
    int frameIndex = 0;

    public Tile(int x, int y, int ix, int iy) {
        this.setBounds(x, y, Settings.TILE_SIZE, Settings.TILE_SIZE);
        this.x = x;
        this.y = y;
        this.ix = ix;
        this. iy = iy;

        this.setBorder(null);
        this.setFocusPainted(false);
        this.setContentAreaFilled(false);

        this.setFocusable(false);

        // Add animation frames
        frames.put("blackToWhite", new String[]{
                "Disc - Black full.png",
                "Disc - Black almost full.png",
                "Disc - Black not almost full.png",
                "Disc - Both.png",
                "Disc - White not almost full.png",
                "Disc - White almost full.png",
                "Disc - White full.png",
        });

        frames.put("whiteToBlack", new String[]{
                "Disc - White full.png",
                "Disc - White almost full.png",
                "Disc - White not almost full.png",
                "Disc - Both.png",
                "Disc - Black not almost full.png",
                "Disc - Black almost full.png",
                "Disc - Black full.png",
        });

        addListener();
    }

    private void addListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gamePanel.playMove(ix, iy);
            }
        });
    }

    public void flip(String turn) {
        if (animationDelay > 0 || !isFlipped) return;

        if (frameIndex > Settings.FRAME_LENGTH - 1) {
            isFlipped = false;
            frameIndex = 0;
            animationDelay = 0;
            return;
        }

        String flipTo = turn.equals("black") ? "blackToWhite" : "whiteToBlack";
        setImage(frames.get(flipTo)[frameIndex]);
        frameIndex++;
    }

    public void draw(Graphics2D g) {
        if (this.color == null) return;

        double sub = Settings.TILE_SIZE * 0.2;
        int gap = (int) (sub / 2);
        int diameter = (int) (Settings.TILE_SIZE - sub);

        g.drawImage(image, this.x + gap, this.y + gap, diameter, diameter, null);
    }

    // Getters and Setters
    public boolean isEmpty() {
        return color == null;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        if (image != null) return;

        switch (color) {
            case "black" -> setImage("Disc - Black full.png");
            case "white" -> setImage("Disc - White full.png");
        }
    }

    public void setImage(String name) {
        String sourceFolder = "src/Othello/Images/";

        imageName = name;
        image = new ImageIcon(sourceFolder + name).getImage();
    }

    public String getImageName() {
        return imageName;
    }
}
