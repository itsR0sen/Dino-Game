package game.view.renderer;

import game.constant.GameConfig;
import game.core.GameEngine;
import game.model.Cloud;
import java.awt.Color;
import java.awt.Graphics2D;

public class BackgroundRenderer extends AbstractRenderer {

    @Override
    public void render(Graphics2D g2d, GameEngine engine, int animationTicks) {
        // 1. Draw rounded sky clouds
        g2d.setColor(new Color(235, 235, 235));
        for (Cloud cloud : engine.getClouds()) {
            g2d.fillRoundRect((int) cloud.getX(), (int) cloud.getY(),
                    cloud.getWidth(), cloud.getHeight(), 12, 12);
        }

        // 2. Draw horizon mountain ranges / sand dunes
        g2d.setColor(new Color(215, 215, 215));
        int hx = (int) engine.getDistantHorizonX();
        int hy = GameConfig.GROUND_Y - 25;

        // Double-render layout buffers sequentially to make the infinite seamless scroll work
        drawDunes(g2d, hx, hy);
        drawDunes(g2d, hx + GameConfig.WINDOW_WIDTH, hy);
    }

    // Connect-the-dots line vector paths for the decorative horizon
    private void drawDunes(Graphics2D g2d, int offset, int hy) {
        g2d.drawLine(offset, hy, offset + 150, hy - 15);
        g2d.drawLine(offset + 150, hy - 15, offset + 350, hy - 5);
        g2d.drawLine(offset + 350, hy - 5, offset + 550, hy - 18);
        g2d.drawLine(offset + 550, hy - 18, offset + GameConfig.WINDOW_WIDTH, hy);
    }
}