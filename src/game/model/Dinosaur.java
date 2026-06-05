package game.model;

import game.constant.GameConfig;

public class Dinosaur extends Entity {
    private boolean isJumping = false;
    private boolean isDucking = false;
    private final int normalHeight; // Need to remember this to restore size after ducking

    public Dinosaur(float x, float y, int width, int height) {
        super(x, y, width, height);
        this.normalHeight = height;
    }

    @Override
    public void update() {
        // Simple gravity and physics routine for handling jumps
        if (isJumping) {
            speedY += GameConfig.GRAVITY;
            y += speedY;

            // Check if we hit the ground line to snap back down
            if (y >= GameConfig.GROUND_Y - height) {
                y = GameConfig.GROUND_Y - height;
                isJumping = false;
                speedY = 0;
            }
        }
    }

    public void jump() {
        // Block jumping if we are already airborne or crouching under a bird
        if (!isJumping && !isDucking) {
            isJumping = true;
            speedY = GameConfig.JUMP_FORCE;
        }
    }

    public void setDucking(boolean ducking) {
        if (isJumping) return; // Can't crouch mid-air

        this.isDucking = ducking;
        if (ducking) {
            // Cut the height in half and shift down so hitboxes match the animation frame
            this.height = normalHeight / 2;
            this.y = GameConfig.GROUND_Y - this.height;
        } else {
            // Reset back to original scale when they let go of the key
            this.height = normalHeight;
            this.y = GameConfig.GROUND_Y - this.height;
        }
    }

    public boolean isDucking() {
        return isDucking;
    }
}