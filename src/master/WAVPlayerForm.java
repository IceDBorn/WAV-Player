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

    // Δημιουργεί αντικείμενο αρχείου.
    File selectedFile;

    // Δημιουργεί λίστα αρχείων.
    ArrayList<FileInfo> files = new ArrayList<>();

    // Δημιουργεί λίστα.
    DefaultListModel fileListModel = new DefaultListModel();

    // Δημιουργεί νεο Player αντικείμενο.
    Player player;

    /* Δημιουργεί ακέραιο που αποθηκεύει την θέση του τελευταίου αρχείου που
    αναπαράχθηκε απο την λίστα. */
    int lastPlayedIndex;

    String lastPlayedText = "Play Song";

    PlayingTimer timer;

    // Δημιουργία του JForm.
    public WAVPlayerForm() {
        initComponents();
        fileList.setModel(fileListModel);
        timer = new PlayingTimer(startLabel, timerSlider, playButton, player);
    }

    private void fileChooserButtonMouseClicked() {//GEN-FIRST:event_fileChooserButtonMouseClicked
        /* Αποθηκεύει στο selectedFile το αρχείο που θα ανοίξει ο επιλογέας 
        αρχείου. */
        selectedFile = FileChooser.Choose();

        if (selectedFile == null) return;

        FileInfo file = new FileInfo(selectedFile);
        // Αποθηκεύει το selectedFile στη λίστα αρχείων files.
        files.add(file);

        // Σβήνει όλα τα υπάρχοντα στοιχεία απο το fileListModel.
        fileListModel.removeAllElements();

        // Προσθέτει όλα τα στοιχεία της λίστας files στο fileListModel.
        fileListModel.addAll(files);

        // Προσθέτει τα στοιχεία της fileListModel στη fileList.
        fileList.setModel(fileListModel);
    }//GEN-LAST:event_fileChooserButtonMouseClicked

    private void clearButtonMouseClicked() {//GEN-FIRST:event_clearButtonMouseClicked
        // Σταματάει την αναπαραγωγή
        if (player != null) {
            player.stop();
        }
        // Καθαρίζει την λίστα με τα αρχεία files.
        files.clear();
        // Καθαρίζει το fileListModel.
        fileListModel.removeAllElements();
        // Προσθέτει την άδεια λίστα στην Jlist fileList.
        fileList.setModel(fileListModel);
        // Αφαίρεση του lastPlayedIndex
        lastPlayedIndex = -1;
        startLabel.setText("00:00:00");
        endLabel.setText("00:00:00");
        timerSlider.setValue(0);
        playButton.setText("Play Song");
    }//GEN-LAST:event_clearButtonMouseClicked

    private void playButtonMouseClicked() {//GEN-FIRST:event_playButtonMouseClicked
        /* Αν δεν έχει  δημιουργηθεί player, τότε δημιουργεί έναν και κάνει
        αναπαραγωγή του αρχείου ήχου. */
        if (player == null) {
            try {
                player = new Player(files.get(fileList.getSelectedIndex()));
                this.playButton.setText("Play Song");
                lastPlayedText = "Play Song";
                this.endLabel.setText(player.getClipLengthString());
                this.timerSlider.setMaximum((int) player.getClipLength() / 1_000_000);
                timer.setAudioPlayer(player);
                timer.start();
            } catch (UnsupportedAudioFileException | IOException |
                    LineUnavailableException ex) {
                Logger.getLogger(WAVPlayerForm.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            player.play();
            // Αλλάζει το κείμενο του κουμπιού playButton σε Pause Song.
            this.playButton.setText("Pause Song");
            lastPlayedText = "Pause Song";
        }
        /* Αν υπάρχει ο player και αναπαράγει ένα αρχείο τότε σταματάει την
        αναπαραγωγή κρατώνας την τρέχουσα θέση του clip. */
        else if (player.getStatus().equals("playing") && lastPlayedIndex == fileList.getSelectedIndex()) {
            player.pause();
            timer.pauseTimer();
            // Αλλάζει το κείμενο του κουμπιού playButton σε Resume.
            this.playButton.setText("Resume Song");
            lastPlayedText = "Resume Song";
        }
        /* Αλλιώς αν ο player δεν αναπαράγει κάποιο αρχείο αλλά έχει κάνει παύση
        κάποια αναπαραγωγή αρχείου, τότε το συνεχίζει απο το σημείο που
        είχε σταματήσει η αναπαραγωγή. */
        else if (player.getStatus().equals("paused") && lastPlayedIndex == fileList.getSelectedIndex()) {
            try {
                player.resumeAudio();
                timer.resumeTimer();
                // Αλλάζει το κείμενο του κουμπιού playButton σε Pause Song.
                this.playButton.setText("Pause Song");
                lastPlayedText = "Pause Song";
            } catch (UnsupportedAudioFileException | IOException |
                    LineUnavailableException ex) {
                Logger.getLogger(WAVPlayerForm.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        } else if (player.getStatus().equals("ended") && lastPlayedIndex == fileList.getSelectedIndex()) {
            resetPlayer();
        }
        /* Αλλιώς αν γίνει αναπαραγωγή νέου αρχείου τότε σταματάει η αναπαραγωγή
        και ξεκινάει η αναπαραγωγή του νέου αρχείου */
        else {
            resetPlayer();
        }

    }//GEN-LAST:event_playButtonMouseClicked

    private void stopButtonMouseClicked() {//GEN-FIRST:event_stopButtonMouseClicked
        // Σταματάει την αναπαραγωγή
        if (player != null) {
            player.stop();
        }
        // Σβήνει το τελευταίο επιλεγμένο αρχείο απο την λίστα.
        try {
            files.remove(fileList.getSelectedIndex());
        } catch (Exception ignored) {
        }
        // Σβήνει όλα τα αρχεία απο το μοντέλο λίστας.
        fileListModel.removeAllElements();
        // Προσθέτει την λίστα files με το αφαιρεμένο πια αρχείο.
        fileListModel.addAll(files);
        // Προσθέτει το μοντέλο λίστας στην Jlist fileList.
        fileList.setModel(fileListModel);
        // Μηδένιση του lastPlayedIndex.
        lastPlayedIndex = -1;
        playButton.setText("Play Song");
        lastPlayedText = "Play Song";
        startLabel.setText("00:00:00");
        endLabel.setText("00:00:00");
        timerSlider.setValue(0);
    }//GEN-LAST:event_stopButtonMouseClicked

    private void volumeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_volumeStateChanged
        player.ChangeVolume(volume.getValue());
    }//GEN-LAST:event_volumeStateChanged

    private void timerSliderMouseClicked() {//GEN-FIRST:event_timerSliderMouseClicked
        try {
            if (timerSlider.isEnabled()) {
                if (timer.isAlive()) {
                    timer.jump((long) timerSlider.getValue() * 1_000_000);
                }
            }
        } catch (UnsupportedAudioFileException | IOException |
                LineUnavailableException unsupportedAudioFileException) {
            unsupportedAudioFileException.printStackTrace();
        }
    }//GEN-LAST:event_timerSliderMouseClicked

    private void timerSliderMousePressed() {//GEN-FIRST:event_timerSliderMousePressed
        timer.setMouseDown(true);
    }//GEN-LAST:event_timerSliderMousePressed

    private void timerSliderMouseReleased() {//GEN-FIRST:event_timerSliderMouseReleased
        timer.setMouseDown(false);
    }//GEN-LAST:event_timerSliderMouseReleased

    private void fileListMouseClicked() {//GEN-FIRST:event_fileListMouseClicked
        if (lastPlayedIndex == fileList.getSelectedIndex()) {
            playButton.setText(lastPlayedText);
        } else {
            playButton.setText("Play Song");
        }
    }//GEN-LAST:event_fileListMouseClicked

    //Μέθοδοι
    public void resetPlayer() {
        try {
            // Σταματάει την αναπαραγωγή.
            player.stop();
            timer.jump(0);
            // Δημιουργεί νέο player με το επιλεγμένο αρχείο.
            player = new Player(files.get(fileList.getSelectedIndex()));
            timer.setAudioPlayer(player);
            // Κάνει αναπαραγωγή του νέου αρχείου.
            player.play();
            // Αλλάζει το κείμενο του κουμπιού playButton σε Pause Song.
            this.playButton.setText("Pause Song");
            lastPlayedText = "Pause Song";
            this.endLabel.setText(player.getClipLengthString());
            this.timerSlider.setMaximum((int) player.getClipLength() / 1_000_000);
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
                stopButtonMouseClicked();
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

    /**
     * @param args the command line arguments
     */
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
