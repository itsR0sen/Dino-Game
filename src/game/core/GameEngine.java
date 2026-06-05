package game.core;

import game.constant.GameConfig;
import game.constant.GameState;
import game.model.Dinosaur;
import game.model.Cloud;
import game.model.Particle;
import game.model.obstacle.Cactus;
import game.model.obstacle.Obstacle;
import game.model.obstacle.Pterodactyl;
import game.view.GamePanel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine implements Runnable {
    // Game loop and thread controls
    private Thread gameThread;
    private boolean running = false;

    // Core game components
    private Dinosaur dinosaur;
    private List<Obstacle> obstacles;
    private GamePanel gamePanel;
    private GameState state;

    // Internal managers
    private final CollisionDetector collisionDetector;
    private final SoundManager soundManager;
    private final Random random = new Random();

    // Environment and parallax effects
    private List<Cloud> clouds;
    private List<Particle> particles;
    private int particleSpawnTimer = 0;
    private float distantHorizonX = 0;

    // Gameplay data
    private int score = 0;
    private float currentSpeed;
    private int highScore = 0;
    private final String HIGH_SCORE_FILE = "res/other/High_Score.txt";

    public GameEngine() {
        this.collisionDetector = new CollisionDetector();
        this.soundManager = new SoundManager(); // Self-managed to keep Main clean
        initGame();
        loadHighScore(); // Load right away on startup
    }

    public void initGame() {
        this.dinosaur = new Dinosaur(50, GameConfig.GROUND_Y - 50, 40, 50);
        this.obstacles = new ArrayList<>();

        // Reset environment states when a new game restarts
        this.clouds = new ArrayList<>();
        this.particles = new ArrayList<>();
        this.distantHorizonX = 0;

        // Spawn initial clouds so the sky isn't empty at start
        this.clouds.add(new Cloud(200));
        this.clouds.add(new Cloud(500));

        this.state = GameState.PLAYING;
        this.score = 0;
        this.currentSpeed = GameConfig.BASE_SPEED;
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
        double amountOfTicks = 60.0; // Dynamic 60 FPS target
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                update();
                if (gamePanel != null) {
                    gamePanel.repaint();
                }
                delta--;
            }

            try {
                Thread.sleep(10); // Keeps CPU usage low and prevents rendering lag
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        // Everything freezes completely on GAME_OVER state
        if (state == GameState.PLAYING) {
            dinosaur.update();
            score++;

            // Increase speed dynamically as the player survives longer
            if (score % 500 == 0) {
                currentSpeed += 0.5f;
                soundManager.playScore(); // Checkpoint milestone audio chime
            }

            // Procedural obstacle generation loop
            if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).getX() < GameConfig.WINDOW_WIDTH - 300) {
                if (random.nextInt(100) < 2) {
                    if (random.nextBoolean()) {
                        obstacles.add(new Cactus(GameConfig.WINDOW_WIDTH, 20 + random.nextInt(15), 40 + random.nextInt(20), currentSpeed));
                    } else {
                        // Randomize bird altitude between low and high hurdles
                        int altitude = random.nextBoolean() ? GameConfig.GROUND_Y - 80 : GameConfig.GROUND_Y - 40;
                        obstacles.add(new Pterodactyl(GameConfig.WINDOW_WIDTH, altitude, 45, 35, currentSpeed));
                    }
                }
            }

            // Move and prune off-screen obstacles
            for (int i = 0; i < obstacles.size(); i++) {
                Obstacle obs = obstacles.get(i);
                obs.update();
                if (obs.isOffScreen()) {
                    obstacles.remove(i);
                    i--;
                }
            }

            // 1. Scroll background mountains at 25% game speed for depth
            distantHorizonX -= (currentSpeed * 0.25f);
            if (distantHorizonX <= -GameConfig.WINDOW_WIDTH) {
                distantHorizonX = 0;
            }

            // 2. Manage and spawn sky background clouds
            for (int i = 0; i < clouds.size(); i++) {
                clouds.get(i).update(currentSpeed);
                if (clouds.get(i).isOffScreen()) {
                    clouds.remove(i);
                    i--;
                }
            }
            if (clouds.size() < 4 && random.nextInt(300) < 1) {
                clouds.add(new Cloud(GameConfig.WINDOW_WIDTH));
            }

            // 3. Generate running foot dust particles only when grounded
            boolean isOnGround = dinosaur.getY() >= (GameConfig.GROUND_Y - dinosaur.getHeight());
            if (isOnGround) {
                particleSpawnTimer++;
                if (particleSpawnTimer >= 8) { // Limits particle density spam
                    particles.add(new Particle(dinosaur.getX() + 5, GameConfig.GROUND_Y - 4));
                    particleSpawnTimer = 0;
                }
            }
            for (int i = 0; i < particles.size(); i++) {
                particles.get(i).update();
                if (particles.get(i).isDead()) {
                    particles.remove(i);
                    i--;
                }
            }

            // Handle hitboxes and collision routines
            if (collisionDetector.checkCollision(dinosaur, obstacles)) {
                state = GameState.GAME_OVER;
                soundManager.playDie(); // Crash sound on fatal hit
                saveHighScore(); // Record immediately upon dying
            }
        }
    }

    // --- FILE I/O HIGH SCORE HANDLERS ---

    private void loadHighScore() {
        try {
            Path path = Paths.get(HIGH_SCORE_FILE);
            if (Files.exists(path)) {
                String content = Files.readString(path).trim();
                if (!content.isEmpty()) {
                    highScore = Integer.parseInt(content);
                }
            }
        } catch (Exception e) {
            // Need a quick terminal alert if file loading breaks
            System.err.println("[ERROR] GameEngine: Failed to load high score data file.");
            System.err.println("Details: " + e.getMessage());
        }
    }

    private void saveHighScore() {
        if (score > highScore) {
            highScore = score;
            try {
                Path path = Paths.get(HIGH_SCORE_FILE);
                Files.writeString(path, String.valueOf(highScore));
            } catch (Exception e) {
                // Alert the console if high scores fail to write out
                System.err.println("[ERROR] GameEngine: Failed to write high score update to file.");
                System.err.println("Details: " + e.getMessage());
            }
        }
    }

    // --- GETTERS AND SETTERS ---

    public void setGamePanel(GamePanel gamePanel) { this.gamePanel = gamePanel; }
    public Dinosaur getDinosaur() { return dinosaur; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public GameState getState() { return state; }
    public SoundManager getSoundManager() { return soundManager; }
    public List<Cloud> getClouds() { return clouds; }
    public List<Particle> getParticles() { return particles; }
    public float getDistantHorizonX() { return distantHorizonX; }
}