package game.core;

import game.constant.GameState;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    private final GameEngine engine;
    private final Runnable restartAction;

    private boolean jumpPlayState = true;
    private boolean duckPlayState = true;

    public InputHandler(GameEngine engine, Runnable restartAction) {
        this.engine = engine;
        this.restartAction = restartAction;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("KEY PRESSED! Code: " + e.getKeyCode());
        int code = e.getKeyCode();

        // --- PLAYING STATE ACTIONS ---
        if (engine.getState() == GameState.PLAYING) {

            // --- JUMP LOGIC ---
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) {
                // FIX 1: Guard the ENTIRE jump action with the lock, not just the audio.
                // This stops accidental auto-bunnyhopping if the player holds spacebar!
                if (jumpPlayState) {
                    engine.getDinosaur().jump();

                    if (engine.getSoundManager() != null) {
                        engine.getSoundManager().playJump();
                    }
                    this.jumpPlayState = false; // Lock out further jumps/restarts until key is released
                }
            }

            // --- DUCK LOGIC ---
            if (code == KeyEvent.VK_DOWN) {
                engine.getDinosaur().setDucking(true);
                if (engine.getSoundManager() != null && duckPlayState) {
                    engine.getSoundManager().playDuck();
                    this.duckPlayState = false;
                }
            }
        }

        // --- GAME OVER STATE ACTIONS ---
        if (engine.getState() == GameState.GAME_OVER) {
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) {
                // FIX 2: Guard the restart with the jump lock.
                // If they were holding spacebar when they died, jumpPlayState is FALSE,
                // meaning this block is safely skipped until they lift their finger!
                if (jumpPlayState) {
                    restartAction.run();

                    // FIX 3: Lock it instantly upon restart so the held key press
                    // doesn't accidentally bleed into a regular jump in the new game loop.
                    this.jumpPlayState = false;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        // When they physically lift their finger, unlock the system
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) {
            this.jumpPlayState = true;
        }

        if (code == KeyEvent.VK_DOWN) {
            this.duckPlayState = true;

            if (engine.getState() == GameState.PLAYING) {
                engine.getDinosaur().setDucking(false);
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
}