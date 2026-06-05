package game.core;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundManager {
    private Clip jumpClip;
    private Clip dieClip;
    private Clip scoreClip;
    private Clip duckClip;

    public SoundManager() {
        // Pre-load all clips into memory to prevent lag when they trigger
        jumpClip = loadSound("/audio/jump.wav");
        dieClip = loadSound("/audio/die.wav");
        scoreClip = loadSound("/audio/milstone.wav"); // Matches file spelling
        duckClip = loadSound("/audio/duck.wav");
    }

    private Clip loadSound(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                System.err.println("[ERROR] SoundManager: Missing audio file at: " + path);
                return null;
            }

            // Using BufferedInputStream here because Java's audio system throws a fit without mark/reset support
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (Exception e) {
            System.err.println("[ERROR] SoundManager: Failed to parse or open audio clip: " + path);
            System.err.println("Details: " + e.getMessage());
            return null;
        }
    }

    public void playJump() { play(jumpClip); }
    public void playDie() { play(dieClip); }
    public void playScore() { play(scoreClip); }
    public void playDuck() { play(duckClip); }

    private void play(Clip clip) {
        if (clip == null) return;

        // Cut the sound short and snap back to start if it's already playing (stops delayed audio overlap)
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }
}