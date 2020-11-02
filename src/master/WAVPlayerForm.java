package master;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class WAVPlayerForm extends javax.swing.JFrame {

    // Δημιουργεί αντικείμενο αρχείου
    File selectedFile;

    // Δημιουργεί λίστα αρχείων
    ArrayList<FileInfo> files = new ArrayList<>();

    // Δημιουργεί μοντέλο λίστας
    DefaultListModel fileListModel = new DefaultListModel();

    // Δημιουργεί νεο Player αντικείμενο το οποιό χρησιμοποιείται για αναπαραγωγή του αρχείου wave
    Player player;

    // Δημιουργεί ακέραιο που αποθηκεύει την θέση του τελευταίου αρχείου που αναπαράχθηκε απο την λίστα
    int lastPlayedIndex;

    // Δημιουργεί String που αποθηκεύει το κείμενο που είχε το κουμπί playButton κάθε φορά που πατάς κλικ σε αυτό
    String lastPlayedText = "Play Song";

    // Δημιουργεί νεο Timer αντικείμενο
    Timer timer;

    // Σταθερά για την μετατροπή των μικροδευτερόλεπτων σε δευτερόλεπτα
    private static final int MICROSECONDS_TO_SECONDS = 1_000_000;

    // Δημιουργία του JForm και αρχικοποίηση του timer
    public WAVPlayerForm() {
        initComponents();
        fileList.setModel(fileListModel);
        timer = new Timer(startLabel, timerSlider, playButton, player);
        timerSlider.setEnabled(false);
    }

    private void fileChooserButtonMouseClicked() {
        // Αποθηκεύει στο selectedFile το αρχείο που θα ανοίξει ο επιλογέας αρχείου
        selectedFile = FileChooser.Choose();

        // Αν δεν επιλέχθηκε αρχείο, τελειώνει η μέθοδος
        if (selectedFile == null) return;

        // Δημιουργεί νέο αντικείμενο FileInfo από το selectedFile
        FileInfo file = new FileInfo(selectedFile);

        // Αποθηκεύει το file στη λίστα αρχείων files
        files.add(file);

        // Σβήνει όλα τα υπάρχοντα στοιχεία απο το fileListModel
        fileListModel.removeAllElements();

        // Προσθέτει όλα τα στοιχεία της λίστας files στο fileListModel
        fileListModel.addAll(files);

        // Προσθέτει τα στοιχεία της fileListModel στη fileList
        fileList.setModel(fileListModel);
    }

    private void clearButtonMouseClicked() {
        // Σταματάει την αναπαραγωγή αν ο player δεν είναι κενός
        if (player != null) {
            player.stop();
        }

        // Καθαρίζει την λίστα με τα αρχεία files
        files.clear();

        // Καθαρίζει το fileListModel
        fileListModel.removeAllElements();

        // Προσθέτει την άδεια λίστα στην Jlist fileList
        fileList.setModel(fileListModel);

        // Επαναφορά του lastPlayedIndex
        lastPlayedIndex = -1;

        // Αρχικοποιεί το startLabel
        startLabel.setText("00:00:00");

        // Αρχικοποιεί το endLabel
        endLabel.setText("00:00:00");

        // Αρχικοποιεί την τιμή του timerSlider
        timerSlider.setValue(0);

        // Αρχικοποιεί την μέγιστη τιμή που μπορεί να δεχτεί το timerSlider
        timerSlider.setMaximum(0);

        // Αρχικοποιεί το playButton
        playButton.setText("Play Song");
    }

    private void playButtonMouseClicked() {
        if (fileList.getSelectedIndex() != -1) {
            // Αν δεν έχει  δημιουργηθεί player, τότε δημιουργεί έναν και κάνει αναπαραγωγή του αρχείου wave
            if (player == null) {
                try {
                    /* Δημιουργεί νέο player διαλέγοντας το αρχείο από την λίστα files σύμφωνα με την επιλογή απο το
                    fileList */
                    player = new Player(files.get(fileList.getSelectedIndex()));

                    // Θέτει την ένταση του player σύμφωνα με την τιμή του JSlider volume
                    player.ChangeVolume(volume.getValue());

                    // Θέτει το lastPlayedText ως Play Song
                    lastPlayedText = "Play Song";

                    // Θέτει το endLabel ως το μήκος του wave αρχείου σε μορφή ΩΩ:ΛΛ:ΔΔ
                    this.endLabel.setText(player.getClipLengthString());

                    /* Θέτει την μέγιστη τιμή του timerSlider στο μήκος του wave αρχείου διά ενα εκατομμύριο για
                    μετατροπή σε δευτερόλεπτα */
                    this.timerSlider.setMaximum((int) player.getClipLength() / MICROSECONDS_TO_SECONDS);

                    // Θέτει τον player που θα ακολουθεί ο Timer timer
                    timer.setPlayer(player);

                    // Ξεκινάει τον timer
                    timer.start();
                } catch (UnsupportedAudioFileException | IOException |
                        LineUnavailableException ex) {
                    Logger.getLogger(WAVPlayerForm.class.getName()).
                            log(Level.SEVERE, null, ex);
                }

                // Ξεκινάει την αναπαραγωγή του αρχείου
                player.play();

                // Αλλάζει το κείμενο του κουμπιού playButton σε Pause Song
                this.playButton.setText("Pause Song");

                // Αλλάζει το κείμενο του lastPlayedText σε Pause Song
                lastPlayedText = "Pause Song";
            }
            /* Αν υπάρχει ο player και αναπαράγει ένα αρχείο τότε σταματάει την
            αναπαραγωγή κρατώνας την τρέχουσα θέση του clip */
            else if (player.getStatus().equals("playing") && lastPlayedIndex == fileList.getSelectedIndex()) {
                // Παύει την αναπαραγωγή του αρχείου
                player.pause();

                // Παύει τον timer
                timer.pauseTimer();

                // Αλλάζει το κείμενο του κουμπιού playButton σε Resume Song
                this.playButton.setText("Resume Song");

                // Αλλάζει το lastPlayedText σε Resume Song
                lastPlayedText = "Resume Song";
            }
            /* Αλλιώς αν ο player δεν αναπαράγει κάποιο αρχείο αλλά έχει κάνει παύση κάποια αναπαραγωγή αρχείου,
            τότε συνεχίζει  την αναπαραγωγή απο το σημείο όπου είχε σταματήσει */
            else if (player.getStatus().equals("paused") && lastPlayedIndex == fileList.getSelectedIndex()) {
                try {
                    // Ξεκινάει την αναπαραγωγή
                    player.resumeAudio();

                    // Ξεκινάει τον timer
                    timer.resumeTimer();

                    // Αλλάζει το κείμενο του κουμπιού playButton σε Pause Song.
                    this.playButton.setText("Pause Song");

                    // Αλλάζει το lastPlayedText σε Pause Song
                    lastPlayedText = "Pause Song";
                } catch (UnsupportedAudioFileException | IOException |
                        LineUnavailableException ex) {
                    Logger.getLogger(WAVPlayerForm.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
            /* Αλλιώς αν γίνει αναπαραγωγή νέου αρχείου τότε σταματάει η αναπαραγωγή
            και ξεκινάει η αναπαραγωγή του νέου αρχείου ή αν τελείωσε η αναπαραγωγή του επιλεγμένου αρχείου τότε,
            ξαναξεκινάει την αναπαραγωγή του*/
            else {
                resetPlayer();
            }
        }
    }

    private void RemoveButtonMouseClicked() {
        // Σταματάει την αναπαραγωγή, αν ο player δεν είναι κενός
        if (player != null) {
            player.stop();
        }

        // Σβήνει το τελευταίο επιλεγμένο αρχείο απο την λίστα
        try {
            files.remove(fileList.getSelectedIndex());
        } catch (Exception ignored) {
        }

        // Σβήνει όλα τα αρχεία απο το μοντέλο λίστας
        fileListModel.removeAllElements();

        // Προσθέτει την λίστα files χωρίς το αφαιρεμένο αρχείο
        fileListModel.addAll(files);

        // Προσθέτει το μοντέλο λίστας στην Jlist fileList
        fileList.setModel(fileListModel);

        // Επαναφορά του lastPlayedIndex.
        lastPlayedIndex = -1;

        // Αλλάζει το κείμενο του playButton σε Play Song
        playButton.setText("Play Song");

        // Αλλάζει το lastPlayedText σε Play Song
        lastPlayedText = "Play Song";

        // Αρχικοποιεί το startLabel
        startLabel.setText("00:00:00");

        // Αρχικοποιεί το endLabel
        endLabel.setText("00:00:00");

        // Αρχικοποιεί την τιμή του timerSlider
        timerSlider.setValue(0);

        //Αρχικοποιεί την μέγιστη τιμή που μπορεί να δεχθεί ο timerSlider
        timerSlider.setMaximum(0);
    }

    private void volumeStateChanged(javax.swing.event.ChangeEvent evt) {
        // Αν ο player δεν είναι κενός τότε αλλάζει την τιμή έντασης του σύμφωνα με την τιμή του JSlider volume
        if (player != null) {
            player.ChangeVolume(volume.getValue());
        }
    }

    private void timerSliderMouseClicked() {
        // Αν ο timerSlider είναι ενεργοποιημένος και ο timer τρέχει τότε αλλάζει την θέση του slider
        try {
            if (timerSlider.isEnabled()) {
                if (timer.isAlive()) {
                    // Μετατρέπει την τιμή του timerSlider απο δευτερόλεπτα σε μικροδευτερόλεπτα
                    timer.jump((long) timerSlider.getValue() * MICROSECONDS_TO_SECONDS);
                }
            }
        } catch (UnsupportedAudioFileException | IOException |
                LineUnavailableException unsupportedAudioFileException) {
            unsupportedAudioFileException.printStackTrace();
        }
    }

    // Αποτρέπει τον timer να αλλάξει τιμή στον timerSlider αν το ποντίκι πατάει κλίκ
    private void timerSliderMousePressed() {
        timer.setMouseDown(true);
    }

    // Σταματάει να αποτρέπει τον timer να αλλάζει τιμή στον timerSlider αν το ποντίκι σταματάει να πατάει κλίκ
    private void timerSliderMouseReleased() {
        timer.setMouseDown(false);
    }

    private void fileListMouseClicked() {
        /* Αν το στοιχείο στο οποίο πατάς κλίκ στη λίστα είναι το αρχείο όπου κάνει αναπαραγωγή ο player τότε,
        αλλάζει το κείμενο του playButton στο lastPlayedText, aλλιώς αλλάζει το κείμενο του σε Play Song */
        if (lastPlayedIndex == fileList.getSelectedIndex()) {
            playButton.setText(lastPlayedText);
        } else {
            playButton.setText("Play Song");
        }
    }

    //Μέθοδοι
    public void resetPlayer() {
        try {
            // Σταματάει την αναπαραγωγή.
            player.stop();

            // Επαναφέρει την θέση του timer στο 0
            timer.jump(0);

            // Δημιουργεί νέο player με το επιλεγμένο αρχείο.
            player = new Player(files.get(fileList.getSelectedIndex()));

            // Θέτει τον player τον οποίο θα ακολουθήσει ο timer
            timer.setPlayer(player);

            // Θέτει την ένταση του player
            player.ChangeVolume(volume.getValue());

            // Κάνει αναπαραγωγή του νέου αρχείου.
            player.play();

            // Αλλάζει το κείμενο του κουμπιού playButton σε Pause Song.
            this.playButton.setText("Pause Song");

            // Αλλάζει το lastPlayedText σε Pause Song
            lastPlayedText = "Pause Song";

            // Αλλάζει την τιμή του endLabel στο μήκος του αρχείου wave
            this.endLabel.setText(player.getClipLengthString());

            /* Θέτει την μέγιστη τιμή του timerSlider στο μήκος του wave αρχείου διά ενα εκατομμύριο για μετατροπή
            σε δευτερόλεπτα */
            this.timerSlider.setMaximum((int) player.getClipLength() / MICROSECONDS_TO_SECONDS);

            // Θέτει το lastPlayedIndex ως το αρχείο που αναπαράχθηκε
            lastPlayedIndex = fileList.getSelectedIndex();
        } catch (UnsupportedAudioFileException | IOException |
                LineUnavailableException ex) {
            Logger.getLogger(WAVPlayerForm.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    //Αυτόματα δημιουργημένα απo το Netbeans
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        JButton fileChooserButton = new JButton();
        JScrollPane playlist = new JScrollPane();
        fileList = new javax.swing.JList<>();
        // Variables declaration - do not modify                     
        JButton clearButton = new JButton();
        playButton = new javax.swing.JButton();
        JLabel playlistLabel = new JLabel();
        JButton stopButton = new JButton();
        volume = new javax.swing.JSlider();
        timerSlider = new javax.swing.JSlider();
        startLabel = new javax.swing.JLabel();
        endLabel = new javax.swing.JLabel();
        JLabel volumeLabel = new JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WAV Player");
        setIconImage(getIconImage());
        setLocation(new java.awt.Point(0, 0));
        setName("WAV Audio Player"); // NOI18N
        setResizable(false);

        fileChooserButton.setFont(new java.awt.Font("Calibri", Font.PLAIN, 11)); // NOI18N
        fileChooserButton.setText("Add Song");
        fileChooserButton.setFocusable(false);
        fileChooserButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileChooserButtonMouseClicked();
            }
        });

        fileList.setFont(new java.awt.Font("Calibri", Font.PLAIN, 11)); // NOI18N
        fileList.setModel(new javax.swing.AbstractListModel<>() {
            final String[] strings = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6"};

            public int getSize() {
                return strings.length;
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });
        fileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fileList.setFocusable(false);
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileListMouseClicked();
            }
        });
        playlist.setViewportView(fileList);

        clearButton.setFont(new java.awt.Font("Calibri", Font.PLAIN, 11)); // NOI18N
        clearButton.setText("Clear Playlist");
        clearButton.setFocusable(false);
        clearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearButtonMouseClicked();
            }
        });

        playButton.setFont(new java.awt.Font("Calibri", Font.PLAIN, 11)); // NOI18N
        playButton.setText("Play Song");
        playButton.setFocusable(false);
        playButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playButtonMouseClicked();
            }
        });

        playlistLabel.setFont(new java.awt.Font("Calibri", Font.BOLD, 11)); // NOI18N
        playlistLabel.setText("Playlist:");

        stopButton.setFont(new java.awt.Font("Calibri", Font.PLAIN, 11)); // NOI18N
        stopButton.setText("Remove Song");
        stopButton.setFocusable(false);
        stopButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RemoveButtonMouseClicked();
            }
        });

        volume.setMajorTickSpacing(-6);
        volume.setMaximum(0);
        volume.setMinimum(-40);
        volume.setFocusable(false);
        volume.addChangeListener(this::volumeStateChanged);

        timerSlider.setMajorTickSpacing(-6);
        timerSlider.setMaximum(0);
        timerSlider.setFocusable(false);
        timerSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                timerSliderMouseClicked();
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                timerSliderMousePressed();
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                timerSliderMouseReleased();
            }
        });

        startLabel.setFont(new java.awt.Font("Calibri", Font.PLAIN, 11)); // NOI18N
        startLabel.setText("00:00:00");
        startLabel.setFocusable(false);

        endLabel.setFont(new java.awt.Font("Calibri", Font.PLAIN, 11)); // NOI18N
        endLabel.setText("00:00:00");
        endLabel.setFocusable(false);

        volumeLabel.setFont(new java.awt.Font("Calibri", Font.BOLD, 11)); // NOI18N
        volumeLabel.setText("Volume:");
        volumeLabel.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(playlist)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(18, 18, 18)
                                                                .addComponent(volume, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(volumeLabel)
                                                                .addGap(32, 32, 32))))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(playlistLabel)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(fileChooserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(startLabel)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(timerSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(endLabel)))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(playlistLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(playlist, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(timerSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(startLabel)))
                                        .addComponent(endLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(fileChooserButton)
                                                        .addComponent(playButton))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(clearButton)
                                                        .addComponent(stopButton))
                                                .addGap(0, 6, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(volumeLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(volume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WAVPlayerForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> {
            new WAVPlayerForm().setVisible(true);
        });
    }

    private javax.swing.JLabel endLabel;
    private javax.swing.JList<String> fileList;
    private javax.swing.JButton playButton;
    private javax.swing.JLabel startLabel;
    private javax.swing.JSlider timerSlider;
    private javax.swing.JSlider volume;
    // End of variables declaration                   
}
