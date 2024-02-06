package Othello.Panels;

import Othello.Move;
import Othello.Settings;
import Othello.Tile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.Clock;
import java.util.ArrayList;

import static Othello.GameFrame.statsPanel;
import static Othello.Othello.random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    ArrayList<ArrayList<Tile>> board = new ArrayList<>();
    ArrayList<Move> moves;

    String turn = "black";

    Graphics2D g2D;
    Timer timer;

    // Animation variables
    Clock clock = Clock.systemDefaultZone();
    long animationTrig = clock.millis();
    long lastTime = clock.millis();

    // Booleans
    boolean showValidMoves = true;

    public GamePanel() {
        this.setPreferredSize(new Dimension(Settings.GAME_SIZE, Settings.GAME_SIZE));
        this.setBackground(new Color(0x23674e));
        this.setLayout(null);

        this.setFocusable(true);
        this.requestFocusInWindow();
        addKeyListener(this);

        initGrid();

        timer = new Timer(Settings.MILLI_DELAY, this);
        timer.start();
    }

    // Initialize methods
    private void initGrid() {
        board.clear();

        for (int row = 0; row < Settings.GRID_SIZE; row++) {
            ArrayList<Tile> arrRow = new ArrayList<>();
            for (int col = 0; col < Settings.GRID_SIZE; col++) {
                int x = col * Settings.TILE_SIZE;
                int y = row * Settings.TILE_SIZE;

                Tile tile = new Tile(x, y, col, row);
                arrRow.add(tile);

                this.add(tile);
            }
            board.add(arrRow);
        }

        int center = Settings.GRID_CENTER - 1;

        // Starting discs
        placeDisc(center, center, "white");
        placeDisc(center+1, center, "black");
        placeDisc(center+1, center+1, "white");
        placeDisc(center, center+1, "black");
    }

    // Draw methods
    private void drawBoard(Graphics2D g) {
        g.setStroke(new BasicStroke(2));
        g.setPaint(Color.BLACK);

        for (int i = 1; i < Settings.GRID_SIZE; i++) {
            int pos = i * Settings.TILE_SIZE;

            g.drawLine(pos, 0, pos, Settings.GAME_SIZE);
            g.drawLine(0, pos, Settings.GAME_SIZE, pos);
        }
    }

    private void drawTiles(Graphics2D g) {
        for (ArrayList<Tile> tiles : board) {
            for (Tile tile : tiles) {
                tile.draw(g);
            }
        }
    }

    public void drawValidMoves(Graphics2D g) {
        if (!showValidMoves || isAnimating()) return;
        switch (turn) {
            case "black" -> g.setPaint(Color.BLACK);
            case "white" -> g.setPaint(Color.WHITE);
        }

        g.setStroke(new BasicStroke(1));
        for (Move move : moves) {
            int x = move.col * Settings.TILE_SIZE;
            int y = move.row * Settings.TILE_SIZE;

            double sub = Settings.TILE_SIZE * 0.2;
            int gap = (int) (sub / 2);
            int diameter = (int) (Settings.TILE_SIZE - sub);

            g.drawOval(x + gap, y + gap, diameter, diameter);
        }
    }

    public void flipTurn() {
        turn = turn.equals("black") ? "white" : "black";
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        this.g2D = (Graphics2D) g;

        drawBoard(g2D);
        drawTiles(g2D);
        drawValidMoves(g2D);
        countDiscs("black");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moves = generateValidMoves(turn);

        // Change turn if no valid moves
        if (moves.size() == 0) {
            flipTurn();
        }

        // Animation
        long animTrig = clock.millis() - animationTrig;
        long deltaTime = clock.millis() - lastTime;

        if (animTrig >= Settings.ANIMATION_TRIGGER) {
            animationTrig = clock.millis();
            for (ArrayList<Tile> tiles : board) {
                for (Tile tile : tiles) {
                    tile.flip(turn);
                    tile.animationDelay -= deltaTime;
                }
            }
        }

        statsPanel.setScore(countDiscs("black"), 'b');
        statsPanel.setScore(countDiscs("white"), 'w');

        lastTime = clock.millis();
        repaint();
    }

    public void restartGame() {
        if (isAnimating()) return;

        initGrid();
        moves.clear();
        turn = "black";

        // Animation variables
        Clock clock = Clock.systemDefaultZone();
        animationTrig = clock.millis();
        lastTime = clock.millis();

        timer.start();
    }

    public void playMove(int col, int row) {
        if (isAnimating() || !isValidMove(row, col, turn)) return;

        executeMove(row, col);
        flipTurn();
    }

    public boolean checkGameOver() {
        // Check if the board is full
        boolean isBoardFull = true;
        for (int row = 0; row < Settings.GRID_SIZE; row++) {
            for (int col = 0; col < Settings.GRID_SIZE; col++) {
                if (getTile(col, row).isEmpty()) {
                    isBoardFull = false;
                    break;
                }
            }
            if (!isBoardFull) {
                break;
            }
        }

        // Check if there are no valid moves for either player
        boolean noValidMovesBlack = generateValidMoves("black").isEmpty();
        boolean noValidMovesWhite = generateValidMoves("white").isEmpty();

        // Declare the winner or a tie based on the game state
        return isBoardFull || (noValidMovesBlack && noValidMovesWhite);
    }

    private int countDiscs(String player) {
        int count = 0;

        for (int row = 0; row < Settings.GRID_SIZE; row++) {
            for (int col = 0; col < Settings.GRID_SIZE; col++) {
                if (getTile(col, row).isEmpty()) continue;
                if (getTile(col, row).getColor().equals(player)) {
                    count++;

                    // TESTING purposes
                    if (g2D != null) {
                        g2D.setFont(new Font("Mono", Font.BOLD, 20));
                        g2D.setPaint(Color.RED);
                        g2D.drawString(String.valueOf(count), (col * Settings.TILE_SIZE) + 20, (row * Settings.TILE_SIZE) + 20);
                    }
                }
            }
        }

        return count;
    }

    public ArrayList<Move> generateValidMoves(String turn) {
        ArrayList<Move> validMoves = new ArrayList<>();

        for (int row = 0; row < Settings.GRID_SIZE; row++) {
            for (int col = 0; col < Settings.GRID_SIZE; col++) {
                if (isValidMove(row, col, turn)) {
                    validMoves.add(new Move(col, row));
                }
            }
        }

        return validMoves;
    }

    public boolean isValidMove(int row, int col, String turn) {
        // Check if the cell is already occupied
        if (!getTile(col, row).isEmpty()) {
            return false;
        }

        // Check if the move flips opponent discs in any direction
        for (int dirRow = -1; dirRow <= 1; dirRow++) {
            for (int dirCol = -1; dirCol <= 1; dirCol++) {
                if (dirRow == 0 && dirCol == 0) {
                    continue;  // Skip the current cell
                }

                int r = row + dirRow;
                int c = col + dirCol;
                boolean foundOpponentDisc = false;
                while (isValidCell(r, c) && !getTile(c, r).isEmpty()) {
                    if (getTile(c, r).getColor().equals(getOpponent(turn))) {
                        foundOpponentDisc = true;
                    } else if (getTile(c,r).getColor().equals(turn) && foundOpponentDisc) {
                        return true;  // Valid move: opponent discs are flipped
                    } else {
                        break;
                    }

                    r += dirRow;
                    c += dirCol;
                }
            }
        }

        return false;  // No valid move found
    }

    public void executeMove(int row, int col) {
        // Place the player's disc in the selected cell
        placeDisc(col, row, turn);

        // Iterate over each direction and flip the opponent's discs
        for (int dirRow = -1; dirRow <= 1; dirRow++) {
            for (int dirCol = -1; dirCol <= 1; dirCol++) {
                if (dirRow == 0 && dirCol == 0) continue; // Skip the current cell

                int r = row + dirRow;
                int c = col + dirCol;
                boolean foundOpponentDisc = false;
                boolean validMove = false;

                while (isValidCell(r, c) && !getTile(c, r).isEmpty()) {
                    if (getTile(c, r).getColor().equals(getOpponent(turn))) {
                        foundOpponentDisc = true;
                    } else if (getTile(c, r).getColor().equals(turn) && foundOpponentDisc) {
                        validMove = true; // Valid move: opponent discs are flipped
                        break;
                    } else {
                        break;
                    }

                    r += dirRow;
                    c += dirCol;
                }

                // If a valid move was found, flip the opponent's discs
                if (validMove) {
                    int delayMulti = 0;
                    r = row + dirRow;
                    c = col + dirCol;

                    while (getTile(c, r).getColor().equals(getOpponent(turn))) {
                        getTile(c, r).setColor(turn);
                        getTile(c, r).isFlipped = true;
                        getTile(c, r).animationDelay = delayMulti * Settings.DELAY_BETWEEN_FLIPS;

                        r += dirRow;
                        c += dirCol;

                        delayMulti++;
                    }
                }
            }
        }
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < Settings.GRID_SIZE && col >= 0 && col < Settings.GRID_SIZE;
    }

    private String getOpponent(String player) {
        return player.equals("black") ? "white" : "black";
    }

    private void placeDisc(int x, int y, String color) {
        switch (color) {
            case "black" -> getTile(x, y).setColor("black");
            case "white" -> getTile(x, y).setColor("white");
        }
    }

    private void placeRandomDisc() {
        if (moves.size() == 0) return;

        int randomIndex = random(moves.size());
        Move randomMove = moves.get(randomIndex);

        playMove(randomMove.col, randomMove.row);
    }

    // Key Listener
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 68 -> placeRandomDisc();
            case 69 -> debug();
            case 82 -> restartGame();
        }
    }

    private void debug() {
        for (ArrayList<Tile> tiles : board) {
            for (Tile tile : tiles) {
                String color = tile.getColor();
                String imageName = tile.getImageName();

                if (color != null && imageName != null) {

                    if (!imageName.toLowerCase().contains(color)) {
                        System.out.println();
                        System.out.println(">\t Error");
                        System.out.println(color);
                        System.out.println(imageName);
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    // Getters and Setters
    public Tile getTile(int x, int y) {
        return board.get(y).get(x);
    }

    public String getTurn() {
        return turn;
    }

    public boolean isAnimating() {
        for (ArrayList<Tile> tiles : board) {
            for (Tile tile : tiles) {
                if (tile.isFlipped || tile.animationDelay > 0) return true;
            }
        }

        return false;
    }
}
