package master;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class Player {

    // Πραγματικός για την αποθήκευση του μικροδευτερόλεπτου της αναπαραγωγής
    Long currentMicrosecond;

    // Δημιουργία αντικειμένου clip
    Clip clip;

    // String για την κατάσταση της αναπαραγωγής
    private String status = "";

    // Δημιουργία αντικειμένου audioInputStream
    AudioInputStream audioInputStream;

    // Δημιουργία String για την τοποθεσία του αρχείου
    static String filePath;

    /* Δημιουργία σταθερών για την μετατροπή της ώρας, του λεπτού σε
    δευτερόλεπτα και των μικροδευτερόλεπτων σε δευτερόλεπτα */
    private static final int HOURS_TO_SECONDS = 3600;
    private static final int MINUTES_TO_SECONDS = 60;
    private static final int MICROSECONDS_TO_SECONDS = 1_000_000;

    // Constructor του Player
    public Player(FileInfo file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        // Προσθέτουμε το αρχείο που είναι επιλεγμένο στη λίστα στο audioInputStream
        audioInputStream = AudioSystem.getAudioInputStream(file.getFile());

        // Προσθέτουμε την τοποθεσία του αρχείου στο filePath
        filePath = file.getFile().getAbsolutePath();

        // Δημιουργία αναφοράς για το clip
        clip = AudioSystem.getClip();

        // Άνοιγμα του audioInputStream στο clip
        clip.open(audioInputStream);
    }

    // Μέθοδοι

    // Μέθοδος για να επιστραφεί η κατάσταση της αναπαραγωγής
    public String getStatus() {
        return status;
    }

    // Μέθοδος για να επιστραφεί το μήκος του αρχείου σε μικροδευτερόλεπτα
    public long getClipLength() {
        return clip.getMicrosecondLength();
    }

    // Μέθοδος όπου θέτει το status
    public void setStatus(String status) {
        this.status = status;
    }

    // Μέθοδος για να επιστραφεί το String με το μήκος του αρχείου απο μικροδευτερόλεπτα σε μορφή HH:MM:SS (ΩΩ:ΛΛ:ΔΔ)
    public String getClipLengthString() {
        // Δημιουργία String για το μήκος του αρχείου σε ΩΩ:ΛΛ:ΔΔ
        String length = "";

        // Δημιουργία πραγματικού για τις ώρες
        long hours = 0;

        // Δημιουργία πραγματικού για τα λεπτά
        long minutes;

        // Δημιουργία πραγματικού για την αποθήκευση των δευτερόλεπτων
        long seconds = clip.getMicrosecondLength() / MICROSECONDS_TO_SECONDS;

        /* Αν τα δευτερόλεπτα είναι περισσότερα απο τα δευτερόλεπτα  μίας ώρας τότε αποθηκεύσει στο hours τα
        δευτερόλεπτα διά την σταθερά μετατροπής δευτερολέπτων σε ώρες, αλλιώς θέτει τις ώρες σε 00 */
        if (seconds >= HOURS_TO_SECONDS) {
            hours = seconds / HOURS_TO_SECONDS;
            length = String.format("%02d:", hours);
        } else {
            length += "00:";
        }

        // Αφαιρεί απο τα δευτερόλεπτα τις ώρες και τις αποθηκεύει στο minutes
        minutes = seconds - hours * HOURS_TO_SECONDS;

        /* Αν τα δευτερόλεπτα είναι περισσότερα απο τα δευτερόλεπτα ενός λεπτού τότε αποθηκεύσει στο minutes τα
        δευτερόλεπτα διά την σταθερά μετατροπής δευτερολέπτων σε λεπτά, αλλιώς θέτει τα λεπτά σε 00 */
        if (minutes >= MINUTES_TO_SECONDS) {
            minutes = minutes / MINUTES_TO_SECONDS;
            length += String.format("%02d:", minutes);
        } else {
            minutes = 0;
            length += "00:";
        }

        // Αφαιρεί απο τα δευτερόλεπτα τις ώρες και τα λεπτά και τα αποθηκεύσει στο restSeconds
        long restSeconds = seconds - hours * HOURS_TO_SECONDS - minutes *
                MINUTES_TO_SECONDS;

        // Αποθηκεύει στο length το μήκος του αρχείου σε ΩΩ:ΛΛ:ΔΔ
        length += String.format("%02d", restSeconds);

        // Επιστρέφει το μήκος του αρχείου
        return length;
    }


    // Μέθοδος για την αρχή αναπαραγωγής
    public void play() {
        // Αρχή αναπαραγωγής
        clip.start();

        // Αλλάζει το status σε playing
        status = "playing";
    }

    // Μέθοδος για την παύση αναπαραγωγής
    public void pause() {
        // Αποθηκεύει το μικροδευτερόλεπτο στο οποιό σταμάτησε η αναπαραγωγή
        currentMicrosecond = clip.getMicrosecondPosition();
        // Σταματάει την αναπαραγωγή
        clip.stop();
        // Αλλάζει το status σε paused
        status = "paused";
    }

    // Μέθοδος για συνέχεια αναπαραγωγής
    public void resumeAudio() throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {
        // Κλείνει το clip
        clip.close();
        // Καλεί την μέθοδο resetAudioStream
        resetAudioStream(); 
        // Θέτει την θέση αναπαραγωγής του clip στα μικροδευτερόλεπτα που αποθηκεύτηκαν κατά την παύση της αναπαραγωγής
        clip.setMicrosecondPosition(currentMicrosecond);
        // Καλεί την μέθοδο play
        play();
    }

    // Μέθοδος για το σταμάτημα της αναπαραγωγής
    public void stop() {
        currentMicrosecond = 0L;
        clip.stop();
        clip.close();
    }

    // Μέθοδος μεταπήδησης σε σημείο του clip
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

    // Μέθοδος για επαναφορά του AudioStream
    public void resetAudioStream() throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        audioInputStream = AudioSystem.getAudioInputStream(
                new File(filePath).getAbsoluteFile());
        clip.open(audioInputStream);
    }
    
    // Μέθοδος για αλλαγή έντασης
    public void ChangeVolume(int value) {
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(value);
    }
} 
