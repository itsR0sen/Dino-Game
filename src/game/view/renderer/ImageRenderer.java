package game.view.renderer;

import game.constant.GameConfig;
import game.constant.GameState;
import game.core.GameEngine;
import game.model.obstacle.Obstacle;
import game.model.obstacle.Pterodactyl;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ImageRenderer extends AbstractRenderer {

    private BufferedImage dinoLeft, dinoRight, dinoDead, dinoDuck;
    private BufferedImage cactus1, cactus3, pterodactylUp, pterodactylDown;
    private BufferedImage restart;

    public ImageRenderer() {
        loadImages();
    }

    private void loadImages() {
        try {
            dinoLeft = loadImage("/img/Dino_Left_Run.png");
            dinoRight = loadImage("/img/Dino_Right_Run.png");
            dinoDead = loadImage("/img/Dino_Dead.png");
            dinoDuck = loadImage("/img/Dino_Duck.png");
            cactus1 = loadImage("/img/1_Cactus.png");
            cactus3 = loadImage("/img/3_Cactus.png");
            pterodactylUp = loadImage("/img/Pterodactyl_Up.png");
            pterodactylDown = loadImage("/img/Pterodactyl_Down.png");
            restart = loadImage("/img/restart.png");
        } catch (Exception e) {
            System.err.println("[ERROR] ImageRenderer: Failed to cache raw sprites.");
            System.err.println("Details: " + e.getMessage());
        }
    }

    private BufferedImage loadImage(String path) throws Exception {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new Exception("Missing asset resource: " + path);
        }
        return ImageIO.read(is);
    }

    @Override
    public void render(Graphics2D g2d, GameEngine engine, int animationTicks) {
        // Toggle frames every 18 ticks for walking/flapping speeds
        int animFrame = (animationTicks / 18) % 2;

        // Base ground floor line divider
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawLine(0, GameConfig.GROUND_Y, GameConfig.WINDOW_WIDTH, GameConfig.GROUND_Y);

        // State machine for deciding the current dinosaur sprite
        BufferedImage currentDino = dinoLeft;
        if (engine.getState() == GameState.GAME_OVER) {
            currentDino = dinoDead; // Freeze frame on death
        } else if (engine.getDinosaur().getY() < GameConfig.GROUND_Y - engine.getDinosaur().getHeight()) {
            currentDino = dinoLeft; // Force stationary frame when airborne
        } else {
            currentDino = engine.getDinosaur().isDucking() ? dinoDuck : ((animFrame == 0) ? dinoLeft : dinoRight);
        }

        if (currentDino != null) {
            g2d.drawImage(currentDino, (int) engine.getDinosaur().getX(), (int) engine.getDinosaur().getY(),
                    engine.getDinosaur().getWidth(), engine.getDinosaur().getHeight(), null);
        }

        // Loop and paint individual active hazards
        for (Obstacle obs : engine.getObstacles()) {
            BufferedImage currentObs = null;
            if (obs instanceof Pterodactyl) {
                currentObs = (animFrame == 0) ? pterodactylUp : pterodactylDown;
            } else {
                currentObs = (obs.getWidth() > 45) ? cactus3 : cactus1; // Pick size based on hitbox width
            }

            if (currentObs != null) {
                g2d.drawImage(currentObs, (int) obs.getX(), (int) obs.getY(), obs.getWidth(), obs.getHeight(), null);
            }
        }

        // Draw the restart button on game over
        if (engine.getState() == GameState.GAME_OVER && restart != null) {
            // Hardcoded to 50x50 layout bounding dimensions
            int rx = (GameConfig.WINDOW_WIDTH - 50) / 2 + 5;
            int ry = GameConfig.WINDOW_HEIGHT / 2 - 60;

            g2d.drawImage(restart, rx, ry, 50, 50, null);
        }
    }

    // Keep this mouse checker mathematically tied to the render positions above!
    public boolean isRestartClicked(int mouseX, int mouseY) {
        if (restart == null) return false;

        int rx = (GameConfig.WINDOW_WIDTH - 50) / 2 + 5;
        int ry = GameConfig.WINDOW_HEIGHT / 2 - 60;

        return (mouseX >= rx && mouseX <= rx + 50 &&
                mouseY >= ry && mouseY <= ry + 50);
    }
}