package tankgame.menus;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {
    public static void playSound(String soundFileName) {
        try {
            URL soundURL = SoundPlayer.class.getClassLoader().getResource("sounds/" + soundFileName);
            if (soundURL == null) {
                throw new IllegalArgumentException("Sound file not found: " + soundFileName);
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
