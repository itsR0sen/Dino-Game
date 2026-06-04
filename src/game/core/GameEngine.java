package game.core;

import game.constant.GameConfig;
import game.constant.GameState;
import game.model.Dinosaur;
import game.model.obstacle.Cactus;
import game.model.obstacle.Obstacle;
import game.model.obstacle.Pterodactyl;
import game.view.GamePanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine implements Runnable {
    private Thread gameThread;
    private boolean running = false;

    private Dinosaur dinosaur;
    private List<Obstacle> obstacles;
    private final CollisionDetector collisionDetector;
    private GameState state;
    private int score = 0;
    private float currentSpeed;
    private final Random random = new Random();

    private GamePanel gamePanel;

    // --- HIGH SCORE VARIABLES ---
    private int highScore = 0;
    private final String HIGH_SCORE_FILE = "res/High_Score.txt";

    public GameEngine() {
        this.collisionDetector = new CollisionDetector();
        initGame();

        // Load the high score the moment the engine is created
        loadHighScore();
    }

    public void initGame() {
        this.dinosaur = new Dinosaur(50, GameConfig.GROUND_Y - 50, 40, 50);
        this.obstacles = new ArrayList<>();
        this.state = GameState.PLAYING;
        this.score = 0;
        this.currentSpeed = GameConfig.BASE_SPEED;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public synchronized void startGame() {
        if (running) return;
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0; // This is FPS
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            // This block strictly executes 60 times per second
            if (delta >= 1) {
                update();

                // FIXED: We moved repaint() INSIDE the 60 FPS timer!
                if (gamePanel != null) {
                    gamePanel.repaint();
                }

                delta--;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (state == GameState.PLAYING) {
            dinosaur.update();
            score++;

            // Speed scaling over time
            if (score % 500 == 0) {
                currentSpeed += 0.5f;
            }

            // Procedural Obstacle Spawning Logic
            if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).getX() < GameConfig.WINDOW_WIDTH - 300) {
                if (random.nextInt(100) < 2) { // 2% chance per tick once buffer distance clears
                    if (random.nextBoolean()) {
                        obstacles.add(new Cactus(GameConfig.WINDOW_WIDTH, 20 + random.nextInt(15), 40 + random.nextInt(20), currentSpeed));
                    } else {
                        // High/Low alternative altitudes for Pterodactyl flying layers
                        int altitude = random.nextBoolean() ? GameConfig.GROUND_Y - 80 : GameConfig.GROUND_Y - 40;
                        obstacles.add(new Pterodactyl(GameConfig.WINDOW_WIDTH, altitude, 45, 35, currentSpeed));
                    }
                }
            }

            for (int i = 0; i < obstacles.size(); i++) {
                Obstacle obs = obstacles.get(i);
                obs.update();
                if (obs.isOffScreen()) {
                    obstacles.remove(i);
                    i--;
                }
            }

            // Check for Game Over
            if (collisionDetector.checkCollision(dinosaur, obstacles)) {
                state = GameState.GAME_OVER;

                // Instantly save the high score when the player dies
                saveHighScore();
            }
        }
    }

    public Dinosaur getDinosaur() { return dinosaur; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public int getScore() { return score; }
    public GameState getState() { return state; }

    // --- HIGH SCORE METHODS ---

    // GamePanel will call this to draw the score on the screen
    public int getHighScore() {
        return highScore;
    }

    // Reads the saved score from the text file
    private void loadHighScore() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(HIGH_SCORE_FILE);
            if (java.nio.file.Files.exists(path)) {
                String content = java.nio.file.Files.readString(path).trim();
                if (!content.isEmpty()) {
                    highScore = Integer.parseInt(content);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load high score: " + e.getMessage());
        }
    }

    // Saves the new high score to the text file if the player beat it
    private void saveHighScore() {
        if (score > highScore) {
            highScore = score;
            try {
                java.nio.file.Path path = java.nio.file.Paths.get(HIGH_SCORE_FILE);
                java.nio.file.Files.writeString(path, String.valueOf(highScore));
            } catch (Exception e) {
                System.err.println("Could not save high score: " + e.getMessage());
            }
        }
    }
}