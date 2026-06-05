package game.view.renderer;

import game.constant.GameState;
import game.core.GameEngine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.InputStream;

public class TextRenderer extends AbstractRenderer {

    private Font scoreFont;
    private Font gameOverFont;
    private Font restartFont;

    public TextRenderer() {
        try {
            // Custom retro font file
            InputStream is = getClass().getResourceAsStream("/other/PressStart2P-Regular.ttf");

            if (is == null) {
                throw new Exception("Missing TTF asset file mapping.");
            }

            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);

            // Derive individual layout scales (must use float literals here)
            scoreFont = baseFont.deriveFont(Font.PLAIN, 12f);
            gameOverFont = baseFont.deriveFont(Font.PLAIN, 24f);
            restartFont = baseFont.deriveFont(Font.PLAIN, 6f);

        } catch (Exception e) {
            System.err.println("[WARN] TextRenderer: Failed to load custom TTF font. Initializing standard system fallbacks.");
            System.err.println("Details: " + e.getMessage());

            // Backup styles in case file I/O breaks
            scoreFont = new Font("Monospaced", Font.BOLD, 12);
            gameOverFont = new Font("Arial", Font.BOLD, 24);
            restartFont = new Font("Monospaced", Font.BOLD, 6);
        }
    }

    @Override
    public void render(Graphics2D g2d, GameEngine engine, int animationTicks) {
        g2d.setColor(Color.DARK_GRAY);

        // Render zero-padded scoreboard layout (HI 00000  00000)
        String scoreText = String.format("HI %05d  %05d", engine.getHighScore(), engine.getScore());
        drawAlignedString(g2d, scoreText, scoreFont, 1, -1, -20, 10);

        // Draw HUD overlay overlays on death screen
        if (engine.getState() == GameState.GAME_OVER) {
            // Margins are fine-tuned to center-align perfectly around the restart arrow image bounds
            drawAlignedString(g2d, "GAME OVER", gameOverFont, 0, 0, 0, -80);
            drawAlignedString(g2d, "Click to RESTART or Press SPACE BAR", restartFont, 0, 0, 0, 0);
        }
    }
}