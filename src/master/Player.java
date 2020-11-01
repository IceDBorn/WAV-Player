package master;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class Player {

    // Πραγματικός για την αποθήκευση του Millisecond της αναπαραγωγής.
    Long currentMicrosecond;

    // Δημιουργία αντικειμένου clip.
    Clip clip;

    // String για την κατάσταση της αναπαραγωγής.
    private String status = "";

    // Δημιουργία αντικειμένου audioInputStream.
    AudioInputStream audioInputStream;

    // Δημιουργία String για την τοποθεσία του αρχείου.
    static String filePath;

    /* Δημιουργία σταθερών για την μετατροπή της ώρας, του λεπτού σε 
    δευτερόλεπτα και των μικροδευτερόλεπτων σε δευτερόλεπτα. */
    private static final int HOURS_TO_SECONDS = 3600;
    private static final int MINUTES_TO_SECONDS = 60;
    private static final int MICROSECONDS_TO_SECONDS = 1_000_000;

    public Player(FileInfo file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        /* Προσθέτουμε το αρχείο που είναι επιλεγμένο στη λίστα στο 
        audioInputStream. */
        audioInputStream = AudioSystem.getAudioInputStream(file.getFile());

        // Προσθέτουμε την τοποθεσία του αρχείου στο filePath.
        filePath = file.getFile().getAbsolutePath();

        // Δημιουργία αναφοράς για το clip
        clip = AudioSystem.getClip();

        // Άνοιγμα του audioInputStream στο clip
        clip.open(audioInputStream);
    }

    // Μέθοδος για να επιστραφεί η κατάσταση της αναπαραγωγής.
    public String getStatus() {
        return status;
    }

    // Μέθοδος για να επιστραφεί το μήκος του αρχείου σε δευτερόλεπτα.
    public long getClipLength() {
        return clip.getMicrosecondLength();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /* Μέθοδος για να επιστραφεί το String με το μήκος του αρχείου απο
        δευτερόλεπτα σε μορφή HH:MM:SS (ΩΩ:ΛΛ:ΔΔ). */
    public String getClipLengthString() {
        String length = "";
        long hour = 0;
        long minute;
        long seconds = clip.getMicrosecondLength() / MICROSECONDS_TO_SECONDS;

        if (seconds >= HOURS_TO_SECONDS) {
            hour = seconds / HOURS_TO_SECONDS;
            length = String.format("%02d:", hour);
        } else {
            length += "00:";
        }

        minute = seconds - hour * HOURS_TO_SECONDS;

        if (minute >= MINUTES_TO_SECONDS) {
            minute = minute / MINUTES_TO_SECONDS;
            length += String.format("%02d:", minute);
        } else {
            minute = 0;
            length += "00:";
        }

        long second = seconds - hour * HOURS_TO_SECONDS - minute *
                MINUTES_TO_SECONDS;

        length += String.format("%02d", second);

        return length;
    }


    // Μέθοδος για την αρχή αναπαραγωγής.
    public void play() {
        // Αρχή αναπαραγωγής
        clip.start();

        // Αλλάζει το status σε playing.
        status = "playing";
    }

    // Μέθοδος για την παύση αναπαραγωγής .
    public void pause() {
        // Αποθηκεύει το Millisecond στο οποιό σταμάτησε η αναπαραγωγή.
        currentMicrosecond = clip.getMicrosecondPosition();
        // Σταματάει την αναπαραγωγή.
        clip.stop();
        // Αλλάζει το status σε paused.
        status = "paused";
    }

    // Μέθοδος για συνέχεια αναπαραγωγής.
    public void resumeAudio() throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {
        // Κλείνει το clip.
        clip.close();
        // Καλεί την μέθοδο resetAudioStream.
        resetAudioStream(); 
        /* Θέτει την θέση αναπαραγωγής του clip στα Millisecond που 
        αποθηκεύτηκαν κατά την παύση της αναπαραγωγής. */
        clip.setMicrosecondPosition(currentMicrosecond);
        // Καλεί την μέθοδο play.
        play();
    }

    // Μέθοδος για το σταμάτημα της αναπαραγωγής. 
    public void stop() {
        currentMicrosecond = 0L;
        clip.stop();
        clip.close();
    }

    // Μέθοδος μεταπήδησης σε σημείο του clip.
    public void jump(long c) throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        if (c > 0 && c < clip.getMicrosecondLength()) {
            clip.stop();
            clip.close();
            resetAudioStream();
            currentMicrosecond = c;
            clip.setMicrosecondPosition(c);
            this.play();
        }
    }

    // Μέθοδος για επαναφορά του AudioStream.
    public void resetAudioStream() throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        audioInputStream = AudioSystem.getAudioInputStream(
                new File(filePath).getAbsoluteFile());
        clip.open(audioInputStream);
    }
    
    // Μέθοδος για αλλαγή έντασης.
    public void ChangeVolume(int value) {
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        //FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
        volume.setValue(value);
        //FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        //float range = control.getMinimum();
        //float result = range * (1 - value / 100.0f);
        //control.setValue(result);
    }
} 
