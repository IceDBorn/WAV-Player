package master;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;

public class PlayingTimer extends Thread {
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private boolean isPause = false;
    private long startTime;
    private long pauseTime;
    private boolean mouseDown = false;

    private final JLabel labelRecordTime;
    private final JSlider slider;
    private final JButton play;
    private Player audioPlayer;

    public void setAudioPlayer(Player audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    public void setMouseDown(boolean mouseDown) {
        this.mouseDown = mouseDown;
    }

    PlayingTimer(JLabel labelRecordTime, JSlider slider, JButton play, Player audioPlayer) {
        this.labelRecordTime = labelRecordTime;
        this.slider = slider;
        this.play = play;
        this.audioPlayer = audioPlayer;
    }

    public void run() {

        startTime = System.currentTimeMillis();

        while (true) {
            try {
                sleep(100);
                if (audioPlayer.clip.getMicrosecondPosition() != audioPlayer.clip.getMicrosecondLength()) {
                    if (!isPause) {
                        if (audioPlayer.clip != null && audioPlayer.clip.isRunning() && !mouseDown) {
                            if (!slider.isEnabled()) {
                                slider.setEnabled(true);
                            }
                            labelRecordTime.setText(toTimeString());
                            int currentSecond = (int) audioPlayer.clip.getMicrosecondPosition() / 1_000_000;
                            slider.setValue(currentSecond);
                        }
                    } else {
                        pauseTime += 100;
                        if (slider.isEnabled()) {
                            slider.setEnabled(false);
                        }
                    }
                }
                else {
                    slider.setValue(0);
                    startTime = System.currentTimeMillis();
                    audioPlayer.setStatus("ended");
                    labelRecordTime.setText("00:00:00");
                    play.setText("Play Song");
                    if (slider.isEnabled()) {
                        slider.setEnabled(false);
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    void pauseTimer() {
        isPause = true;
    }

    void resumeTimer() {
        isPause = false;
    }

    private String toTimeString() {
        long now = System.currentTimeMillis();
        Date current = new Date(now - startTime - pauseTime);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(current);
    }

    public void jump(long c) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        long now = System.currentTimeMillis();
        long current = now - startTime - pauseTime;
        pauseTime += current - c / 1_000;
        audioPlayer.jump(c);
    }
}