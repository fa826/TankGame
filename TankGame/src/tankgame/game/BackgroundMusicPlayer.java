package tankgame.game;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class BackgroundMusicPlayer {
    private static Clip musicClip;

    public static void playLoop(String fileName) {
        stop();  // Stop previous if any

        try {
            URL url = BackgroundMusicPlayer.class.getClassLoader().getResource("sounds/background_music.wav");
            if (url == null) {
                System.err.println("Sound file not found: " + fileName);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY); // 🎵 loop forever
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }
}
