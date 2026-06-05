package game.model;

import java.awt.Rectangle;

public abstract class Entity {
    // Protected variables so child classes (Dino, Cactus, Bird) can change coordinates easily
    protected float x, y;
    protected int width, height;
    protected float speedX, speedY;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Force every entity to implement its own movement/physics routine
    public abstract void update();

    // Box calculation used directly by the collision system
    public Rectangle getHitbox() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // --- BASE GETTERS ---
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}