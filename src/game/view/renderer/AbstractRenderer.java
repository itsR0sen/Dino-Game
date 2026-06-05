package game.view.renderer;

import game.constant.GameConfig;
import game.core.GameEngine;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public abstract class AbstractRenderer {

    // Main entry point
    public abstract void render(Graphics2D g2d, GameEngine engine, int animationTicks);

    // alignX/Y layout codes: -1 = left/top, 0 = center, 1 = right/bottom

    protected void drawAlignedString(Graphics2D g2d, String text, Font font, int alignX, int alignY, int marginX, int marginY) {
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);

        int textWidth = metrics.stringWidth(text);
        int textAscent = metrics.getAscent();
        int textDescent = metrics.getDescent();
        int textHeight = metrics.getHeight();

        int x = 0;
        int y = 0;
        int padding = 0; // Safe screen edge buffer

        // X-axis mapping formulas
        if (alignX == -1) x = padding + marginX;
        else if (alignX == 0) x = (GameConfig.WINDOW_WIDTH - textWidth) / 2 + marginX;
        else if (alignX == 1) x = GameConfig.WINDOW_WIDTH - textWidth - padding + marginX;

        // Y-axis mapping formulas with font baseline compensation
        if (alignY == -1) y = textAscent + padding + marginY;
        else if (alignY == 0) y = (GameConfig.WINDOW_HEIGHT - textHeight) / 2 + textAscent + marginY;
        else if (alignY == 1) y = GameConfig.WINDOW_HEIGHT - textDescent - padding + marginY;

        g2d.drawString(text, x, y);
    }
}