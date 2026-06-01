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

    public GameEngine() {
        this.collisionDetector = new CollisionDetector();
        initGame();
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
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                update();
                delta--;
            }

            if (gamePanel != null) {
                gamePanel.repaint();
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

            if (collisionDetector.checkCollision(dinosaur, obstacles)) {
                state = GameState.GAME_OVER;
            }
        }
    }

    public Dinosaur getDinosaur() { return dinosaur; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public int getScore() { return score; }
    public GameState getState() { return state; }
}