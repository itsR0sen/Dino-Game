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
            System.err.println("CRITICAL: Failed to load images.");
            e.printStackTrace();
        }
    }

    private BufferedImage loadImage(String path) throws Exception {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) throw new Exception("Cannot find file: " + path);
        return ImageIO.read(is);
    }

    @Override
    public void render(Graphics2D g2d, GameEngine engine, int animationTicks) {
        int animFrame = (animationTicks / 18) % 2;

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawLine(0, GameConfig.GROUND_Y, GameConfig.WINDOW_WIDTH, GameConfig.GROUND_Y);

        BufferedImage currentDino = dinoLeft;
        if (engine.getState() == GameState.GAME_OVER) currentDino = dinoDead;
        else if (engine.getDinosaur().getY() < GameConfig.GROUND_Y - engine.getDinosaur().getHeight()) currentDino = dinoLeft;
        else currentDino = engine.getDinosaur().isDucking() ? dinoDuck : ((animFrame == 0) ? dinoLeft : dinoRight);

        if (currentDino != null) {
            g2d.drawImage(currentDino, (int) engine.getDinosaur().getX(), (int) engine.getDinosaur().getY(),
                    engine.getDinosaur().getWidth(), engine.getDinosaur().getHeight(), null);
        }

        for (Obstacle obs : engine.getObstacles()) {
            BufferedImage currentObs = null;
            if (obs instanceof Pterodactyl) currentObs = (animFrame == 0) ? pterodactylUp : pterodactylDown;
            else currentObs = (obs.getWidth() > 45) ? cactus3 : cactus1;

            if (currentObs != null) {
                g2d.drawImage(currentObs, (int) obs.getX(), (int) obs.getY(), obs.getWidth(), obs.getHeight(), null);
            }
        }

        if (engine.getState() == GameState.GAME_OVER && restart != null) {
            // FIXED: Replaced restart.getWidth() with 50 to match the scaled draw size
            int rx = (GameConfig.WINDOW_WIDTH - 50) / 2 + 5;
            int ry = GameConfig.WINDOW_HEIGHT / 2 - 60;

            g2d.drawImage(restart, rx, ry, 50, 50, null);
        }
    }

    // ENCAPSULATION: The click bounds now perfectly match the 50x50 drawing coordinates!
    public boolean isRestartClicked(int mouseX, int mouseY) {
        if (restart == null) return false;

        // Exact same X and Y math as your render method above
        int rx = (GameConfig.WINDOW_WIDTH - 50) / 2 + 5;
        int ry = GameConfig.WINDOW_HEIGHT / 2 - 60;

        // Exact same width and height (50) as your render method above
        return (mouseX >= rx && mouseX <= rx + 50 &&
                mouseY >= ry && mouseY <= ry + 50);
    }
}