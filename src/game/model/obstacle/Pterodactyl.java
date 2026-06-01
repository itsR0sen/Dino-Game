package game.model.obstacle;

public class Pterodactyl extends Obstacle {
    private int wingFrame = 0;
    private int animationTick = 0;

    public Pterodactyl(float x, float y, int width, int height, float speedX) {
        super(x, y, width, height, speedX);
    }

    @Override
    public void update() {
        super.update();
        animationTick++;
        if (animationTick % 10 == 0) {
            wingFrame = (wingFrame == 0) ? 1 : 0;
        }
    }

    public int getWingFrame() { return wingFrame; }
}