package Othello;

import java.awt.*;

public class Settings {
    public static int FPS = 60;
    public static int MILLI_DELAY = 1000 / FPS;
    public static int ANIMATION_TRIGGER = 60;
    public static int DELAY_BETWEEN_FLIPS = 80;
    public static int FRAME_LENGTH = 7;

    public static int GAME_SIZE = 800;
    public static int GRID_SIZE = 8;
    public static int TILE_SIZE = GAME_SIZE / GRID_SIZE;

    public static int GRID_CENTER = GRID_SIZE / 2;

    // Screen dimension
    static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public final static int SCREEN_WIDTH = SCREEN_SIZE.width;
    public final static int SCREEN_HEIGHT = SCREEN_SIZE.height;
}
