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
    private boolean pause = false;
    private long startTime;
    private long pauseTime;
    private boolean mouseDown = false;

    private final JLabel startLabel;
    private final JSlider timerSlider;
    private final JButton playButton;
    private Player player;

    public void setPlayer(Player player) {
        this.player = player;
        pause = false;
    }

    PlayingTimer(JLabel labelRecordTime, JSlider slider, JButton play, Player audioPlayer) {
        this.startLabel = labelRecordTime;
        this.timerSlider = slider;
        this.playButton = play;
        this.player = audioPlayer;
    }

    public void setMouseDown(boolean mouseDown) {
        this.mouseDown = mouseDown;
    }

    public void run() {

        startTime = System.currentTimeMillis();

        while (true) {
            try {
                sleep(100);
                if (player.clip.getMicrosecondPosition() != player.clip.getMicrosecondLength()) {
                    if (!pause) {
                        if (player.clip != null && player.clip.isRunning() && !mouseDown) {
                            if (!timerSlider.isEnabled()) {
                                timerSlider.setEnabled(true);
                            }
                            startLabel.setText(toTimeString());
                            int currentSecond = (int) player.clip.getMicrosecondPosition() / 1_000_000;
                            timerSlider.setValue(currentSecond);
                        }
                    } else {
                        pauseTime += 100;
                        if (timerSlider.isEnabled()) {
                            timerSlider.setEnabled(false);
                        }
                    }
                }
                else {
                    timerSlider.setValue(0);
                    startTime = System.currentTimeMillis();
                    player.setStatus("ended");
                    startLabel.setText("00:00:00");
                    playButton.setText("Play Song");
                    if (timerSlider.isEnabled()) {
                        timerSlider.setEnabled(false);
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    void pauseTimer() {
        pause = true;
    }

    void resumeTimer() {
        pause = false;
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
        player.jump(c);
    }
}