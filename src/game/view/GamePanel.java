package game.view;



import game.constant.GameConfig;
import game.constant.GameState;
import game.core.GameEngine;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

//Renderers
import game.view.renderer.AbstractRenderer;
import game.view.renderer.ImageRenderer;
import game.view.renderer.TextRenderer;

public class GamePanel extends JPanel {
    private final GameEngine engine;
    private int animationTicks = 0;

    // Pattern: A Rendering Pipeline
    private final List<AbstractRenderer> renderingPipeline;
    private final ImageRenderer imageRenderer; // Kept reference specifically for mouse clicks

    public GamePanel(GameEngine engine) {
        this.engine = engine;
        this.setPreferredSize(new Dimension(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT));
        this.setBackground(new Color(245, 245, 245));
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        this.renderingPipeline = new ArrayList<>();
        this.imageRenderer = new ImageRenderer();

        // Add both systems to our rendering pipeline list
        renderingPipeline.add(imageRenderer);
        renderingPipeline.add(new TextRenderer());

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (engine.getState() == GameState.GAME_OVER) {
                    // We just ask the ImageRenderer if its button was clicked!
                    if (imageRenderer.isRestartClicked(e.getX(), e.getY())) {
                        engine.initGame();
                        GamePanel.this.requestFocus();
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (engine.getState() == game.constant.GameState.PLAYING) {
            animationTicks++;
        }

        // POLYMORPHISM: Render everything in the pipeline without caring what it is
        for (AbstractRenderer renderer : renderingPipeline) {
            renderer.render(g2d, engine, animationTicks);
        }

        g2d.dispose();
    }
}