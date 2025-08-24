package org.garret.perst;

import org.garret.perst.impl.QueryImpl;
import org.garret.perst.impl.StorageImpl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Base class for persistent collections. This class delegates all
 * standard {@link java.util.Collection} behaviour to
 * {@link java.util.AbstractCollection} and implements only
 * persistence-specific functionality.
 */
public abstract class PersistentCollection<T> extends AbstractCollection<T>
        implements ITable<T>, IPersistent, IResource, ICloneable, Pinned {

    /* =======================================
     *  Persistent and resource state handling
     * ======================================= */

    transient Storage storage;
    transient int     oid;
    transient int     state;

    static public final int RAW   = 1;
    static public final int DIRTY = 2;
    static public final int DELETED = 4;

    private transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // IResource implementation
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

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        lock = new ReentrantReadWriteLock();
    }

    // IPersistent implementation
    public synchronized void load() {
        if (oid != 0 && (state & RAW) != 0) {
            storage.checkReadLock(getOid());
            storage.loadObject(this);
        }
    }

    public synchronized void loadAndModify() {
        load();
        modify();
    }

    public final boolean isRaw() {
        return (state & RAW) != 0;
    }

    public final boolean isModified() {
        return (state & DIRTY) != 0;
    }

    public final boolean isDeleted() {
        return (state & DELETED) != 0;
    }

    public final boolean isPersistent() {
        return oid != 0;
    }

    public void makePersistent(Storage storage) {
        if (oid == 0) {
            storage.makePersistent(this);
        }
    }

    public void store() {
        if ((state & RAW) != 0) {
            throw new StorageError(StorageError.ACCESS_TO_STUB);
        }
        if (storage != null) {
            storage.storeObject(this);
            state &= ~DIRTY;
        }
    }

    public void modify() {
        if ((state & DIRTY) == 0 && oid != 0) {
            if ((state & RAW) != 0) {
                throw new StorageError(StorageError.ACCESS_TO_STUB);
            }
            Assert.that((state & DELETED) == 0);
            storage.modifyObject(this);
            state |= DIRTY;
        }
    }

    public final int getOid() {
        return oid;
    }

    public void deallocate() {
        if (oid != 0) {
            storage.deallocateObject(this);
        }
    }

    public boolean recursiveLoading() {
        return true;
    }

    public final Storage getStorage() {
        return storage;
    }

    public void invalidate() {
        state &= ~DIRTY;
        state |= RAW;
    }

    public void unassignOid() {
        oid = 0;
        state = DELETED;
        storage = null;
    }

    public void assignOid(Storage storage, int oid, boolean raw) {
        this.oid = oid;
        this.storage = storage;
        if (raw) {
            state |= RAW;
        } else {
            state &= ~RAW;
        }
    }

    protected void clearState() {
        state = 0;
        oid = 0;
    }

    public Object clone() throws CloneNotSupportedException {
        @SuppressWarnings("unchecked")
        PersistentCollection<T> p = (PersistentCollection<T>)super.clone();
        p.oid = 0;
        p.state = 0;
        return p;
    }

    public void readExternal(ObjectInput s) throws IOException, ClassNotFoundException {
        oid = s.readInt();
    }

    public void writeExternal(ObjectOutput s) throws IOException {
        if (s instanceof StorageImpl.PersistentObjectOutputStream) {
            makePersistent(((StorageImpl.PersistentObjectOutputStream)s).getStorage());
        }
        s.writeInt(oid);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (oid == 0) {
            return super.equals(o);
        }
        return o instanceof IPersistent && ((IPersistent)o).getOid() == oid;
    }

    public int hashCode() {
        return oid;
    }

    public void onLoad() {}
    public void onStore() {}

    /* =========================
     *  Collection functionality
     * ========================= */

    public IterableIterator<T> select(Class cls, String predicate) {
        Query<T> query = new QueryImpl<T>(getStorage());
        return query.select(cls, iterator(), predicate);
    }

    public void deallocateMembers() {
        Iterator<T> i = iterator();
        while (i.hasNext()) {
            storage.deallocate(i.next());
        }
        clear();
    }

    // Constructors
    public PersistentCollection() {}

    public PersistentCollection(Storage storage) {
        this.storage = storage;
    }
}

