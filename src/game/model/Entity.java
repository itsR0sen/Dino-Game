package game.model;

import java.awt.Rectangle;

public abstract class Entity {
    protected float x, y;
    protected int width, height;
    protected float speedX, speedY;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update();

    public Rectangle getHitbox() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}