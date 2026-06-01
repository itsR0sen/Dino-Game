package game.core;

import game.model.Dinosaur;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    private final Dinosaur dinosaur;
    private final Runnable restartAction;

    public InputHandler(Dinosaur dinosaur, Runnable restartAction) {
        this.dinosaur = dinosaur;
        this.restartAction = restartAction;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) {
            dinosaur.jump();
        }
        if (code == KeyEvent.VK_DOWN) {
            dinosaur.setDucking(true);
        }
        if (code == KeyEvent.VK_R) {
            restartAction.run();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            dinosaur.setDucking(false);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
}