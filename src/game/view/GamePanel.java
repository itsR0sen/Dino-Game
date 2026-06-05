package game.view;

import game.constant.GameConfig;
import game.constant.GameState;
import game.core.GameEngine;
import game.view.renderer.AbstractRenderer;
import game.view.renderer.BackgroundRenderer;
import game.view.renderer.ImageRenderer;
import game.view.renderer.ParticleRenderer;
import game.view.renderer.TextRenderer;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private final GameEngine engine;
    private int animationTicks = 0;

    // rendering pipeline to draw layers
    private final List<AbstractRenderer> renderingPipeline;
    // Keep a direct reference to this one so I can grab the restart button's bounding box later
    private final ImageRenderer imageRenderer;

    public GamePanel(GameEngine engine) {
        this.engine = engine;
        this.setPreferredSize(new Dimension(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT));
        this.setBackground(new Color(245, 245, 245));
        this.setDoubleBuffered(true); // Smooth swing drawing to stop screen flickering
        this.setFocusable(true);

        this.renderingPipeline = new ArrayList<>();
        this.imageRenderer = new ImageRenderer();

        renderingPipeline.add(new BackgroundRenderer()); // Layer 0: Sky and mountains
        renderingPipeline.add(new ParticleRenderer());   // Layer 1: Dust particles
        renderingPipeline.add(imageRenderer);            // Layer 2: Dino, birds, cacti, and restart icon
        renderingPipeline.add(new TextRenderer());       // Layer 3: HUD and game over overlays

        // Catch clicks on the restart icon
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (engine.getState() == GameState.GAME_OVER) {
                    // check mouse coordinates intersected the button bounds
                    if (imageRenderer.isRestartClicked(e.getX(), e.getY())) {
                        engine.initGame();
                        // keyboard focus back
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

        // animationTick update
        if (engine.getState() == GameState.PLAYING) {
            animationTicks++;
        }
        for (AbstractRenderer renderer : renderingPipeline) {
            renderer.render(g2d, engine, animationTicks);
        }

        g2d.dispose(); // Prevent Swing memory leaks
    }
}