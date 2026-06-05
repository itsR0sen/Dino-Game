package game.view.renderer;

import game.core.GameEngine;
import game.model.Particle;
import java.awt.Graphics2D;

public class ParticleRenderer extends AbstractRenderer {

    @Override
    public void render(Graphics2D g2d, GameEngine engine, int animationTicks) {
        // Hand off rendering to individual instances since particles track their own alpha states
        for (Particle p : engine.getParticles()) {
            p.render(g2d);
        }
    }
}