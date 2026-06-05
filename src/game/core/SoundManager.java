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
        // Pre-load sounds into memory for zero latency during gameplay
        jumpClip = loadSound("/audio/jump.wav");
        dieClip = loadSound("/audio/die.wav");
        scoreClip = loadSound("/audio/milstone.wav");
        duckClip = loadSound("/audio/duck.wav");
    }

    private Clip loadSound(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                System.err.println("AUDIO ERROR: Cannot find file: " + path);
                return null;
            }

            // Wrapping it in a BufferedInputStream prevents "mark/reset" bugs in Java Audio
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (Exception e) {
            System.err.println("AUDIO ERROR: Failed to load: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public void playJump() { play(jumpClip); }
    public void playDie() { play(dieClip); }
    public void playScore() { play(scoreClip); }
    public void playDuck() { play(duckClip); }

    private void play(Clip clip) {
        if (clip == null) return;

        // If the sound is already playing, stop it and rewind it to frame 0
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }
}