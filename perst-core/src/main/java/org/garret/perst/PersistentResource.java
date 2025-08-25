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
    public void sharedLock() {
        if (storage != null) {
            storage.lockObject(this);
        }
    }

    public boolean sharedLock(long timeout) {
        sharedLock();
        return true;
    }

    public void exclusiveLock() {
        sharedLock();
    }

    public boolean exclusiveLock(long timeout) {
        sharedLock();
        return true;
    }

    public void unlock() {}

    public void reset() {}

    public PersistentResource() {}

    public PersistentResource(Storage storage) {
        super(storage);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
