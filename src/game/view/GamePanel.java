package game.view;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import game.constant.GameConfig;

public class GamePanel extends JPanel {

    public GamePanel() {
        // Sets the exact resolution of the game canvas
        this.setPreferredSize(new Dimension(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT));
        this.setBackground(java.awt.Color.WHITE);
        this.setDoubleBuffered(true); // Prevents screen flickering in Swing
        this.setFocusable(true);      // Allows this panel to receive key presses
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clears the screen
        Graphics2D g2d = (Graphics2D) g;

        // Your rendering calls go here:
        // dino.draw(g2d);
        // obstacle.draw(g2d);

        g2d.dispose(); // Clean up graphics memory
    }
}