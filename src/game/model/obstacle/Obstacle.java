package game.model.obstacle;

import game.model.Entity;

public abstract class Obstacle extends Entity {
    public Obstacle(float x, float y, int width, int height, float speedX) {
        super(x, y, width, height);
        this.speedX = speedX;
    }

    @Override
    public void update() {
        x -= speedX;
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }
}