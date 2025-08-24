package org.garret.perst;

import java.nio.file.Path;

/**
 * Service interface for storage lifecycle operations.
 */
public interface StorageLifecycle {
    /**
     * Constant specifying that page pool should be dynamically extended to contain all database file pages.
     */
    int INFINITE_PAGE_POOL = 0;

    /**
     * Constant specifying default pool size.
     */
    int DEFAULT_PAGE_POOL_SIZE = 4*1024*1024;

    /**
     * Open the storage.
     *
     * @param filePath path to the database file
     * @param pagePoolSize size of page pool (in bytes). Page pool should contain at least
     * ten 4kb pages, so minimal page pool size should be at least 40Kb. But larger page pool
     * usually leads to better performance (unless it could not fit in memory and cause swapping).
     * Value 0 of this parameter corresponds to an infinite page pool (all pages are cached in memory).
     */
    void open(Path filePath, long pagePoolSize);

    /**
     * Open the storage with default page pool size.
     *
     * @param filePath path to the database file
     */
    void open(Path filePath);

    /**
     * Check if database is opened.
     *
     * @return <code>true</code> if database was opened by <code>open</code> method,
     * <code>false</code> otherwise
     */
    boolean isOpened();

    /**
     * Get storage root. Storage can have exactly one root object.
     *
     * @return root object or <code>null</code> if root is not specified
     */
    <T> T getRoot();

    /**
     * Set new storage root object.
     *
     * @param root object to become new storage root
     */
    void setRoot(Object root);

    /**
     * Commit transaction (if needed) and close the storage.
     */
    void close();
}
