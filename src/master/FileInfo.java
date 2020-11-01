package master;
import java.io.File;

public class FileInfo {
    
    // Δημιουργία αντικείμενου αρχείου
    private final File file;

    // Constructor για το FileInfo
    public FileInfo(File file) {
        this.file = file;
    }

    // Μέθοδος που μας επιστρέφει το αρχείο
    public File getFile() {
        return file;
    }
    
    /* Μέθοδος για να μας επιστρέφει το όνομα του αρχείου όταν 
    χρησιμοποιούμαι το file σε string */
    @Override
    public String toString() {
        /* Χρησιμοποιώντας regular expression κρατάει μόνο το κομμάτι χωρίς το 
        extension του αρχείου */
        return this.file.getName().replaceFirst("[.][^.]+$", "");
    }
}