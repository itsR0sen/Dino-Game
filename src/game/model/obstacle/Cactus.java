package game.model.obstacle;

import game.constant.GameConfig;

public class Cactus extends Obstacle {
    public Cactus(float x, int width, int height, float speedX) {
        super(x, GameConfig.GROUND_Y - height, width, height, speedX);
    }
}