package game;

import game.core.GameEngine;
import game.core.InputHandler;
import game.view.GamePanel;
import game.view.GameWindow;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameEngine engine = new GameEngine();
            GamePanel panel = new GamePanel(engine);
            engine.setGamePanel(panel);

            // FIX: Pass 'engine' instead of 'engine.getDinosaur()'.
            // The restart callback now only needs ONE simple job: reset the engine.
            InputHandler input = new InputHandler(engine, () -> {
                if (engine.getState() == game.constant.GameState.GAME_OVER) {
                    engine.initGame();

                    // Safe guard: Ensure the panel keeps keyboard focus when restarting via the 'R' key
                    panel.requestFocusInWindow();
                }
            });

            // Add the listener exactly once. It will work forever!
            panel.addKeyListener(input);

            new GameWindow(panel);
            engine.startGame();
        });
    }
}