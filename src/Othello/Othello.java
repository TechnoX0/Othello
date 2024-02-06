package Othello;

import java.util.Random;

public class Othello {
    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame();
    }

    public static int random(int r) {
        Random rand = new Random();
        return rand.nextInt(r);
    }
}