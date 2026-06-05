package game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Particle {
    private double x, y;
    private double vx, vy;
    private int lifetime;
    private final int maxLifetime;
    private final int size;
    private static final Random random = new Random();

    public Particle(double x, double y) {
        this.x = x;
        this.y = y;

        // Random drift physics: force them backward and float them slightly upward
        this.vx = -random.nextDouble() * 2 - 1;
        this.vy = -random.nextDouble() * 1 - 0.5;

        // Give every puff of dust a randomized lifetime of 15-30 frames
        this.maxLifetime = 15 + random.nextInt(15);
        this.lifetime = maxLifetime;
        this.size = 3 + random.nextInt(4);
    }

    public void update() {
        x += vx;
        y += vy;
        lifetime--; // Count down until the particle burns out
    }

    // Flag for the engine update loop to drop dead particles from the tracking list
    public boolean isDead() {
        return lifetime <= 0;
    }

    public void render(Graphics2D g2d) {
        // Map the fading opacity percentage smoothly based on remaining lifespan
        float alpha = (float) lifetime / maxLifetime;
        if (alpha < 0) alpha = 0;

        // Draw a soft, fading gray box to look like kicked-up foot dust
        g2d.setColor(new Color(0.6f, 0.6f, 0.6f, alpha * 0.6f));
        g2d.fillRect((int) x, (int) y, size, size);
    }
}