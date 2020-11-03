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

public class Timer extends Thread {

    // Δημιουργία νέας μορφής ημερομηνίας (ΩΩ:ΛΛ:ΔΔ)
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    // Δημιουργία boolean για τον έλεγχο παύσης του player
    private boolean pause = false;

    // Δημιουργία πραγματικού αριθμού για την αποθήκευση του χρόνου έναρξης του timer
    private long startTime;

    // Δημιουργία πραγματικού αριθμού για την αποθήκευση του χρόνου παύσης του timer
    private long pauseTime;

    // Δημιουργία boolean για τον έλεγχο της κατάστασης του κλίκ του ποντικιού πάνω στον timerSlider
    private boolean mouseDown = false;

    // Δημιουργία JLabel startLabel για την σύνδεση του JLabel της κύριας κλάσης σε αυτή
    private final JLabel startLabel;

    // Δημιουργία JSlider timerSlider για την σύνδεση του JSlider της κύριας κλάσης σε αυτή
    private final JSlider timerSlider;

    // Δημιουργία JButton playButton για την σύνδεση του JButton της κύριας κλάσης σε αυτή
    private final JButton playButton;

    // Δημιουργία Player player για την σύνδεση του Player της κύριας κλάσης σε αυτή
    private Player player;

    // Σταθερά για την μετατροπή των μικροδευτερόλεπτων σε δευτερόλεπτα
    private static final int MICROSECONDS_TO_SECONDS = 1_000_000;

    // Constructor του Timer
    Timer(JLabel labelRecordTime, JSlider slider, JButton play, Player audioPlayer) {
        this.startLabel = labelRecordTime;
        this.timerSlider = slider;
        this.playButton = play;
        this.player = audioPlayer;
    }

    // Μέθοδοι

    // Μέθοδος για την αλλαγή του mouseDown
    public void setMouseDown(boolean mouseDown) {
        this.mouseDown = mouseDown;
    }

    // Μέθοδος για έναρξη ενός thread το οποίο ανανεώνει τον timer
    public void run() {
        // Αποθήκευει την ώρα έναρξης του timer στο startTime
        startTime = System.currentTimeMillis();
        // Όσο αναπαράγεται ένα αρχείο, ανανεώνει τον timer ανα δευτερόλεπτο
        while (true) {
            try {
                // Παύση του thread για 100 μιλιδευτερόλεπτα
                sleep(100);
                // Αν ο player δεν έχει φτάσει στο τέλος του ανανεώνει τον timer, αλλιώς αρχικοποιεί τον timer
                if (player.clip.getMicrosecondPosition() != player.clip.getMicrosecondLength()) {
                    /* Αν ο player δεν έχει παύσει ανανεώνει τον timer, αλλιώς αυξάνει το pauseTime κατά 100
                    μιλιδευτερόλεπτα και απενεργοποιεί τον timerSlider */
                    if (!pause) {
                        /* Αν ο player δεν είναι κενός, αναπαράγει αρχείο και το κλίκ του ποντικιού δεν είναι πατημένο
                        πάνω στον timerSlider τότε ανανεώνει τον timer */
                        if (player.clip != null && player.clip.isRunning() && !mouseDown) {
                            // Αν ο timerSlider δεν είναι ενεργοποιημένος, τότε τον ενεργοποιεί
                            if (!timerSlider.isEnabled()) {
                                timerSlider.setEnabled(true);
                            }
                            // Θέτει το κείμενο του startLabel στην τωρινή χρονική στιγμή σε μορφή ΩΩ:ΛΛ:ΔΔ
                            startLabel.setText(toTimeString());

                            /* Θέτει το currentSecond στη τωρινή θέση του αρχείου / 1_000_000 για μετατροπή σε
                            δευτερόλεπτα */
                            int currentSecond = (int) player.clip.getMicrosecondPosition() / MICROSECONDS_TO_SECONDS;

                            // Θέτει την θέση του timerSlider στο currentSecond
                            timerSlider.setValue(currentSecond);
                        }
                    } else {
                        // Αυξάνει το pauseTime κατά 100
                        pauseTime += 100;

                        // Αν ο timerSlider είναι ενεργοποιημένος τότε τον απενεργοποιεί
                        if (timerSlider.isEnabled()) {
                            timerSlider.setEnabled(false);
                        }
                    }
                } else {
                    // Θέτει το status σε ended
                    player.setStatus("ended");

                    // Επαναφέρει τον timerSlider
                    timerSlider.setValue(0);

                    // Επαναφέρει το startTime
                    startTime = System.currentTimeMillis();

                    // Επαναφέρει το startLabel
                    startLabel.setText("00:00:00");

                    // Επαναφέρει το playButton σε Play Song
                    playButton.setText("Play Song");

                    // Αν ο timerSlider είναι ενεργοποιημένος τότε τον απενεργοποιεί
                    if (timerSlider.isEnabled()) {
                        timerSlider.setEnabled(false);
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    // Θέτει το pause σε true το οποίο παύει τον timer
    void pauseTimer() {
        pause = true;
    }

    // Θέτει το pause σε false το οποίο επαναφέρει σε λειτουργία τον timer
    void resumeTimer() {
        pause = false;
    }

    // Μετατρέπει τη θέση του timer απο μιλιδευτερόλεπτα σε μορφή ΩΩ:ΛΛ:ΔΔ
    private String toTimeString() {
        // Δημιουργία πραγματικού για την αποθήκευση της τωρινής στιγμής
        long now = System.currentTimeMillis();

        /* Δημιουργία ημερομηνίας απο την αφαίρεση της τωρινής στιγμής, του startTime και του pauseTime για να
        υπολογιστεί πόση ώρα αναπαράγεται το αρχείο. Δηλάδή απο την τωρινή στιγμή αφαιρούμε την στιγμή που είχε
        ξεκινήσει  η αναπαραγωγή για να βγάλουμε την διαφορά τους που είναι τα μιλιδευτερόλεπτα όπου αναπαράγεται το
        αρχείο και αφαιρούμε και τα μιλιδευτερόλεπτα όπου είχε γίνει παύση του αρχείου για να βγεί το ακριβές
        αποτέλεσμα σε περίπτωση που είχε γίνει παύση */
        Date current = new Date(now - startTime - pauseTime);

        // Θέτει την ζώνη ώρας σε GMT
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        // Επιστρέφει την ημερομηνία σε μορφή ΩΩ:ΛΛ:ΔΔ
        return dateFormat.format(current);
    }

    public void jump(long jumpTime) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        // Δημιουργία πραγματικού για την αποθήκευση της τωρινής στιγμής
        long now = System.currentTimeMillis();

        /* Αποθήκευση της τωρινής θέσης του αρχείου απο την αφαίρεση της τωρινής στιγμής, της στιγμής εκκίνησης της
        αναπαραγωγής και του χρόνου παύσης της αναπαραγωγής */
        long current = now - startTime - pauseTime;

        // Προσθέτουμε στο pauseTime την τωρινή στιγμή μείον το jumpTime / 1_000 για μετατροπή σε μιλιδευτερόλεπτα
        pauseTime += current - jumpTime / 1_000;

        // Καλεί την μέθοδο jump του player για να μεταπηδήσει στην χρονική στιγμή που δόθηκε μέσω του jumpTime
        player.jump(jumpTime);
    }

    // Μέθοδος που θέτει τον player τον οποιό θα ακολουθεί ο timer
    public void setPlayer(Player player) {
        this.player = player;
        pause = false;
    }
}