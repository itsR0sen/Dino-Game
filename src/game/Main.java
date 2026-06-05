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
            InputHandler input = new InputHandler(engine, () -> {
                if (engine.getState() == game.constant.GameState.GAME_OVER) {
                    engine.initGame();
                    // Force keyboard focus
                    panel.requestFocusInWindow();
                }
            });
            panel.addKeyListener(input);

            new GameWindow(panel);
            engine.startGame();
        });
    }
}