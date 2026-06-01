package game.model;

import game.constant.GameConfig;

public class Dinosaur extends Entity {
    private boolean isJumping = false;
    private boolean isDucking = false;
    private final int normalHeight;

    public Dinosaur(float x, float y, int width, int height) {
        super(x, y, width, height);
        this.normalHeight = height;
    }

    @Override
    public void update() {
        if (isJumping) {
            speedY += GameConfig.GRAVITY;
            y += speedY;

            if (y >= GameConfig.GROUND_Y - height) {
                y = GameConfig.GROUND_Y - height;
                isJumping = false;
                speedY = 0;
            }
        }
    }

    public void jump() {
        if (!isJumping && !isDucking) {
            isJumping = true;
            speedY = GameConfig.JUMP_FORCE;
        }
    }

    public void setDucking(boolean ducking) {
        if (isJumping) return;
        this.isDucking = ducking;
        if (ducking) {
            this.height = normalHeight / 2;
            this.y = GameConfig.GROUND_Y - this.height;
        } else {
            this.height = normalHeight;
            this.y = GameConfig.GROUND_Y - this.height;
        }
    }

    public boolean isDucking() {
        return isDucking;
    }
}