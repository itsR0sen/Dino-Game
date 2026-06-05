package game.model.obstacle;

import game.model.Entity;

public abstract class Obstacle extends Entity {

    public Obstacle(float x, float y, int width, int height, float speedX) {
        super(x, y, width, height);
        this.speedX = speedX; // Sets the scroll rate based on current game speed
    }

    @Override
    public void update() {
        x -= speedX; // Universal movement: everything scrolls left toward the player
    }

    // Quick cleanup check so the engine knows exactly when to drop it from memory
    public boolean isOffScreen() {
        return x + width < 0;
    }
}