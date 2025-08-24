package org.garret.perst;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Service interface for database backup operations.
 */
public interface BackupService {
    /**
     * Backup current state of database.
     *
     * @param out output stream to which backup is done
     */
    void backup(OutputStream out) throws IOException;

    /**
     * Backup current state of database to the file with specified path.
     *
     * @param filePath path to the backup file
     * @param cipherKey cipher key for the encryption of the backup file, null to disable encryption
     */
    void backup(String filePath, String cipherKey) throws IOException;
}
