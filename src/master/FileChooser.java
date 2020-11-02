package master;
import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class FileChooser {
    
    public static File Choose(Point location) {
        // Δημιουργεί επιλογέα αρχείου
        JFileChooser fileChooser = new JFileChooser(FileSystemView.
                getFileSystemView().getHomeDirectory());

        fileChooser.setLocation(location);
        
        // Θέτει τον τίτλο του fileChooser σε Add wav audio file
        fileChooser.setDialogTitle("Add wav audio file");
        
        // Δημιουργεί φίλτρο αρχείων που δέχεται μόνο .wav αρχεία
        FileNameExtensionFilter filter = new 
        FileNameExtensionFilter(".wav audio files", "wav", "wave");
        
        // Απενεργοποιεί την επιλογή επιλογής κάθε αρχείου
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        // Προσθέτει το φίλτρο αρχείων στον επιλογέα αρχείου
        fileChooser.setFileFilter(filter);
        
        // Φέρνει τον επιλογέα αρχείου στο παρασκήνιο
        fileChooser.setVisible(true);
        
        // Δηιουργεί ένα αντικείμενο αρχείου
        File selectedFile = null;
        
        // Δημιουργεί έναν ακέραιο με τον οποίο ελέγχουμε αν ο χρήστης πάτησε Open παρακάτω
        int returnValue = fileChooser.showOpenDialog(null);
        
        // Αν ο χρήστης πάτησε άνοιγμα τότε το αρχείο αποθηκεύεται στο selectedFile
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
	}
        
        // Επιστρέφει το αρχείο selectedFile
        return selectedFile;

    }
    
}
