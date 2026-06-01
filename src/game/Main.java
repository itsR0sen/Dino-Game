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

            // Hook Input listener along with atomic game reset callback hook routine
            InputHandler input = new InputHandler(engine.getDinosaur(), () -> {
                if (engine.getState() == game.constant.GameState.GAME_OVER) {
                    engine.initGame();
                    panel.removeKeyListener(panel.getKeyListeners()[0]);
                    InputHandler newInput = new InputHandler(engine.getDinosaur(), null); // update listener reference pointer
                    panel.addKeyListener(new InputHandler(engine.getDinosaur(), () -> {
                        if (engine.getState() == game.constant.GameState.GAME_OVER) {
                            engine.initGame();
                        }
                    }));
                }
            });

            panel.addKeyListener(input);
            new GameWindow(panel);
            engine.startGame();
        });
    }
}