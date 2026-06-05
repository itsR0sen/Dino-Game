package game.model;

import java.util.Random;

public class Cloud {
    private double x, y;
    private final double driftSpeed;
    private final int width, height;
    private static final Random random = new Random();

    public Cloud(double startX) {
        this.x = startX;
        // Keep them high up in the sky area with randomized altitudes
        this.y = 30 + random.nextInt(60);
        this.driftSpeed = 0.2 + random.nextDouble() * 0.4;

        // Randomize the dimensions so every cloud looks unique
        this.width = 60 + random.nextInt(40);
        this.height = 15 + random.nextInt(15);
    }

    public void update(float gameSpeed) {
        // Parallax scroll math: combine passive drift with a tiny percentage of core map speed
        this.x -= (driftSpeed + (gameSpeed * 0.15f));
    }

    // Tells the runner when a cloud completely clears the left window boundary
    public boolean isOffScreen() {
        return x + width < 0;
    }

    // --- SKY GETTERS ---
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}