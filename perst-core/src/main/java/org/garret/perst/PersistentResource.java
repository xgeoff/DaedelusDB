package org.garret.perst;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Base class for persistent capable objects.
 * <p>
 * Historically this class provided per-instance read/write locks.
 * The database now relies on a single writer thread queue and no longer
 * requires object level locking. The lock related methods are retained for
 * API compatibility but they only notify the storage and perform no
 * synchronization.
 */
public class PersistentResource extends Persistent implements IResource {
    /** Simple mutual exclusion lock. Not persisted. */
    private transient final java.util.concurrent.locks.ReentrantReadWriteLock rwLock =
            new java.util.concurrent.locks.ReentrantReadWriteLock();

    public void sharedLock() {
        if (storage != null) {
            storage.lockObject(this);
        }
        rwLock.readLock().lock();
    }

    public boolean sharedLock(long timeout) {
        try {
            if (storage != null) {
                storage.lockObject(this);
            }
            return rwLock.readLock().tryLock(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void exclusiveLock() {
        if (storage != null) {
            storage.lockObject(this);
        }
        rwLock.writeLock().lock();
    }

    public boolean exclusiveLock(long timeout) {
        try {
            if (storage != null) {
                storage.lockObject(this);
            }
            return rwLock.writeLock().tryLock(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void unlock() {
        if (rwLock.isWriteLockedByCurrentThread()) {
            rwLock.writeLock().unlock();
        } else if (rwLock.getReadLockCount() > 0) {
            rwLock.readLock().unlock();
        }
    }

    public void reset() {
        while (rwLock.isWriteLockedByCurrentThread()) {
            rwLock.writeLock().unlock();
        }
        while (rwLock.getReadHoldCount() > 0) {
            rwLock.readLock().unlock();
        }
    }

    public PersistentResource() {}

    public PersistentResource(Storage storage) {
        super(storage);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
