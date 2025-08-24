package org.garret.perst.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manages per-object read/write locks based on object identifiers (OIDs).
 * Locks are reentrant for the writer thread and allow readers to block until
 * the writer releases the lock.
 */
public class LockManager {
    private final ConcurrentHashMap<Integer, ReentrantReadWriteLock> locks =
        new ConcurrentHashMap<Integer, ReentrantReadWriteLock>();

    private ReentrantReadWriteLock getLock(int oid) {
        ReentrantReadWriteLock lock = locks.get(oid);
        if (lock == null) {
            ReentrantReadWriteLock newLock = new ReentrantReadWriteLock();
            ReentrantReadWriteLock existing = locks.putIfAbsent(oid, newLock);
            lock = existing != null ? existing : newLock;
        }
        return lock;
    }

    /** Acquire the read lock for the specified object id. */
    public void acquireRead(int oid) {
        getLock(oid).readLock().lock();
    }

    /** Release the read lock for the specified object id. */
    public void releaseRead(int oid) {
        ReentrantReadWriteLock lock = locks.get(oid);
        if (lock != null) {
            lock.readLock().unlock();
            if (!lock.isWriteLocked() && lock.getReadLockCount() == 0) {
                locks.remove(oid, lock);
            }
        }
    }

    /** Acquire the write lock for the specified object id. */
    public void acquireWrite(int oid) {
        getLock(oid).writeLock().lock();
    }

    /** Release the write lock for the specified object id. */
    public void releaseWrite(int oid) {
        ReentrantReadWriteLock lock = locks.get(oid);
        if (lock != null) {
            lock.writeLock().unlock();
            if (!lock.isWriteLocked() && lock.getReadLockCount() == 0) {
                locks.remove(oid, lock);
            }
        }
    }

    /** Check if the specified object id is currently write locked. */
    public boolean isWriteLocked(int oid) {
        ReentrantReadWriteLock lock = locks.get(oid);
        return lock != null && lock.isWriteLocked();
    }
}

