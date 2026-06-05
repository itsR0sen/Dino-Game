package game.core;

import game.constant.GameState;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    private final GameEngine engine;
    private final Runnable restartAction;

    // Use simple flags to prevent keyboard auto-repeat from messing up inputs
    private boolean jumpPlayState = true;
    private boolean duckPlayState = true;

    public InputHandler(GameEngine engine, Runnable restartAction) {
        this.engine = engine;
        this.restartAction = restartAction;
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        System.out.println("KEY PRESSED! Code: " + e.getKeyCode());
        int code = e.getKeyCode();

        // --- ACTIVE GAMEPLAY CONTROLS ---
        if (engine.getState() == GameState.PLAYING) {

            // --- JUMP LOGIC ---
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) {
                // Wrap the whole jump in a lock so holding spacebar won't cause infinite auto-jumping
                if (jumpPlayState) {
                    engine.getDinosaur().jump();

                    if (engine.getSoundManager() != null) {
                        engine.getSoundManager().playJump();
                    }
                    this.jumpPlayState = false; // Lock it until they release the key
                }
            }

            // --- DUCK LOGIC ---
            if (code == KeyEvent.VK_DOWN) {
                engine.getDinosaur().setDucking(true);
                // Lock the duck audio too so the system repeat doesn't spam it constantly
                if (engine.getSoundManager() != null && duckPlayState) {
                    engine.getSoundManager().playDuck();
                    this.duckPlayState = false;
                }
            }
        }

        // --- DEATH SCREEN CONTROLS ---
        if (engine.getState() == GameState.GAME_OVER) {
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) {
                // Only restart if they deliberately press the key down. Stops instant restarts if they held space on impact!
                if (jumpPlayState) {
                    restartAction.run();

                    // Snap lock it on restart so the press doesn't make the dinosaur jump right when the map loads
                    this.jumpPlayState = false;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        // Lift finger to release jump locks
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) {
            this.jumpPlayState = true;
        }

        // Lift finger to stop ducking and release sound locks
        if (code == KeyEvent.VK_DOWN) {
            this.duckPlayState = true;

            // Only clear ducking state if the game is still live
            if (engine.getState() == GameState.PLAYING) {
                engine.getDinosaur().setDucking(false);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}