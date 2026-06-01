package game.core;

import game.model.Dinosaur;
import game.model.obstacle.Obstacle;
import java.util.List;

public class CollisionDetector {
    public boolean checkCollision(Dinosaur dino, List<Obstacle> obstacles) {
        for (Obstacle obs : obstacles) {
            if (dino.getHitbox().intersects(obs.getHitbox())) {
                return true;
            }
        }
        return false;
    }
}