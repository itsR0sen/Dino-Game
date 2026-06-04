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
            // 1. Load the font file from the 'res' folder
            // Make sure the file name exactly matches the one you downloaded!
            InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");

            if (is == null) {
                throw new Exception("Font file not found!");
            }

            // 2. Create the base font from the file
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);

            // 3. Derive specific sizes for our game (MUST use a float like 20f, not an int)
            scoreFont = baseFont.deriveFont(Font.PLAIN, 12f);
            gameOverFont = baseFont.deriveFont(Font.PLAIN, 24f);
            restartFont = baseFont.deriveFont(Font.PLAIN, 6f);


        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to load custom font. Using default fallback fonts.");
            e.printStackTrace();

            // Fallback just in case the file is missing
            scoreFont = new Font("Monospaced", Font.BOLD, 12);
            gameOverFont = new Font("Arial", Font.BOLD, 24);
            restartFont = new Font("Monospaced", Font.BOLD, 6);
        }
    }

    @Override
    public void render(Graphics2D g2d, GameEngine engine, int animationTicks) {
        g2d.setColor(Color.DARK_GRAY);

        // Draw Score using the custom font
        String scoreText = String.format("HI %05d  %05d", engine.getHighScore(), engine.getScore());
        drawAlignedString(g2d, scoreText, scoreFont, 1, -1, -20, 10);

        // Draw Game Over using the custom font
        if (engine.getState() == GameState.GAME_OVER) {
            // Note: Since this font is already very stylized, you might not need the spaces
            // between the letters anymore. You can change it to "GAME OVER" if it looks too wide!
            drawAlignedString(g2d, "GAME OVER", gameOverFont, 0, 0, 0, -80);
            drawAlignedString(g2d, "Click to RESTART", restartFont, 0, 0, 0, 0);
        }
    }
}