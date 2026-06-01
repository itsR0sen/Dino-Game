package game.view;

import game.constant.GameConfig;
import game.constant.GameState;
import game.core.GameEngine;
import game.model.obstacle.Obstacle;
import game.model.obstacle.Pterodactyl;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel {
    private final GameEngine engine;

    public GamePanel(GameEngine engine) {
        this.engine = engine;
        this.setPreferredSize(new Dimension(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Render Ground Horizon Strips
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawLine(0, GameConfig.GROUND_Y, GameConfig.WINDOW_WIDTH, GameConfig.GROUND_Y);

        // Render Dinosaur Frame Box Coordinates
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect((int) engine.getDinosaur().getX(), (int) engine.getDinosaur().getY(),
                engine.getDinosaur().getWidth(), engine.getDinosaur().getHeight());

        // Render Polymorphic Obstacles Array Elements
        for (Obstacle obs : engine.getObstacles()) {
            if (obs instanceof Pterodactyl) {
                g2d.setColor(Color.RED); // Flight objects rendered red
            } else {
                g2d.setColor(Color.GREEN); // Cacti objects rendered green
            }
            g2d.fillRect((int) obs.getX(), (int) obs.getY(), obs.getWidth(), obs.getHeight());
        }

        // Render Dashboard Metrics Text Layer
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("SCORE: " + engine.getScore(), GameConfig.WINDOW_WIDTH - 160, 35);

        // Render Modal Dialog State Box Overlays
        if (engine.getState() == GameState.GAME_OVER) {
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            g2d.drawString("G A M E   O V E R", GameConfig.WINDOW_WIDTH / 2 - 150, GameConfig.WINDOW_HEIGHT / 2 - 20);

            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            g2d.drawString("Press 'R' to Restart Execution", GameConfig.WINDOW_WIDTH / 2 - 100, GameConfig.WINDOW_HEIGHT / 2 + 20);
        }

        g2d.dispose();
    }
}