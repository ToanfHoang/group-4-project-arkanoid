package arkanoid.sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sound {
    Clip clip;
    URL soundURL[] = new URL[30];

    public Sound() {
        soundURL[0] = getClass().getResource("/sound/bounceonwall.wav");
        soundURL[1] = getClass().getResource("/sound/bounceonpaddle.wav");
        soundURL[2] = getClass().getResource("/sound/tap.wav");
        soundURL[3] = getClass().getResource("/sound/fail.wav");
        soundURL[4] = getClass().getResource("/sound/bgs.wav");
    }
    public void setFile(int i){
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);

        } catch (Exception ignored){
        }
    }
    public void play(){
        clip.start();
    }
    public void stop(){
        clip.stop();
        clip.close();
    }

    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }
}
