package org.garret.perst;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Base class for persistent capable objects supporting locking
 */

public class PersistentResource extends Persistent implements IResource {
    public void sharedLock() {
        if (lock.isWriteLockedByCurrentThread()) {
            lock.writeLock().lock();
        } else {
            lock.readLock().lock();
            if (storage != null && lock.getReadLockCount() == 1 && !lock.isWriteLocked()) {
                storage.lockObject(this);
            }
        }
    }

    public boolean sharedLock(long timeout) {
        if (lock.isWriteLockedByCurrentThread()) {
            lock.writeLock().lock();
            return true;
        }
        try {
            if (lock.readLock().tryLock(timeout, TimeUnit.MILLISECONDS)) {
                if (storage != null && lock.getReadLockCount() == 1 && !lock.isWriteLocked()) {
                    storage.lockObject(this);
                }
                return true;
            }
            return false;
        } catch (InterruptedException x) {
            return false;
        }
    }

    public void exclusiveLock() {
        lock.writeLock().lock();
        if (storage != null && lock.getReadLockCount() == 0 && lock.getWriteHoldCount() == 1) {
            storage.lockObject(this);
        }
    }

    public boolean exclusiveLock(long timeout) {
        if (lock.isWriteLockedByCurrentThread()) {
            lock.writeLock().lock();
            return true;
        }
        try {
            if (lock.writeLock().tryLock(timeout, TimeUnit.MILLISECONDS)) {
                if (storage != null && lock.getReadLockCount() == 0 && lock.getWriteHoldCount() == 1) {
                    storage.lockObject(this);
                }
                return true;
            }
            return false;
        } catch (InterruptedException x) {
            return false;
        }
    }

    public void unlock() {
        if (lock.isWriteLockedByCurrentThread()) {
            lock.writeLock().unlock();
        } else {
            lock.readLock().unlock();
        }
    }

    public void reset() {
        while (lock.isWriteLockedByCurrentThread()) {
            lock.writeLock().unlock();
        }
        int n = lock.getReadHoldCount();
        for (int i = 0; i < n; i++) {
            lock.readLock().unlock();
        }
    }

    public PersistentResource() {}

    public PersistentResource(Storage storage) {
        super(storage);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        lock = new ReentrantReadWriteLock();
    }

    private transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
}
