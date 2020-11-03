package master;

import java.io.File;

public class FileInfo {

    // Δημιουργία αντικειμένου αρχείου
    private final File file;

    // Constructor για το FileInfo
    public FileInfo(File file) {
        this.file = file;
    }

    // Μέθοδος που μας επιστρέφει το αρχείο
    public File getFile() {
        return file;
    }

    /* Μέθοδος για να μας επιστραφεί το όνομα του αρχείου όταν
    χρησιμοποιούμε το file σε string */
    @Override
    public String toString() {
        /* Χρησιμοποιώντας regular expression κρατάει μόνο το μέρος χωρίς το
        extension του αρχείου */
        return this.file.getName().replaceFirst("[.][^.]+$", "");
    }
}