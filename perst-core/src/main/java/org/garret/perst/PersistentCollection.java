package org.garret.perst;

import org.garret.perst.impl.QueryImpl;
import org.garret.perst.impl.StorageImpl;
import java.util.EnumSet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * Base class for persistent collections. This class delegates all
 * standard {@link java.util.Collection} behaviour to
 * {@link java.util.AbstractCollection} and implements only
 * persistence-specific functionality.
 * <p>
 * Per-object locks were removed in favour of the single writer thread queue;
 * locking methods now simply notify the storage and perform no synchronization.
 */
public abstract class PersistentCollection<T> extends AbstractCollection<T>
        implements ITable<T>, IPersistent, IResource, ICloneable, Pinned {

    /* =======================================
     *  Persistent and resource state handling
     * ======================================= */

    transient Storage storage;
    transient int     oid;
    transient EnumSet<PersistenceState> state = EnumSet.noneOf(PersistenceState.class);


    // IResource implementation
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

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    // IPersistent implementation
    public synchronized void load() {
        if (oid != 0 && state.contains(PersistenceState.RAW)) {
            storage.checkReadLock(getOid());
            storage.loadObject(this);
        }
    }

    public synchronized void loadAndModify() {
        load();
        modify();
    }

    public final boolean isRaw() {
        return state.contains(PersistenceState.RAW);
    }

    public final boolean isModified() {
        return state.contains(PersistenceState.DIRTY);
    }

    public final boolean isDeleted() {
        return state.contains(PersistenceState.DELETED);
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
        if (state.contains(PersistenceState.RAW)) {
            throw new StorageError(StorageError.ACCESS_TO_STUB);
        }
        if (storage != null) {
            storage.storeObject(this);
            state.remove(PersistenceState.DIRTY);
        }
    }

    public void modify() {
        if (!state.contains(PersistenceState.DIRTY) && oid != 0) {
            if (state.contains(PersistenceState.RAW)) {
                throw new StorageError(StorageError.ACCESS_TO_STUB);
            }
            Assert.that(!state.contains(PersistenceState.DELETED));
            storage.modifyObject(this);
            state.add(PersistenceState.DIRTY);
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
        state.remove(PersistenceState.DIRTY);
        state.add(PersistenceState.RAW);
    }

    public void unassignOid() {
        oid = 0;
        state = EnumSet.of(PersistenceState.DELETED);
        storage = null;
    }

    public void assignOid(Storage storage, int oid, boolean raw) {
        this.oid = oid;
        this.storage = storage;
        if (raw) {
            state.add(PersistenceState.RAW);
        } else {
            state.remove(PersistenceState.RAW);
        }
    }

    protected void clearState() {
        state.clear();
        oid = 0;
    }

    public Object clone() throws CloneNotSupportedException {
        @SuppressWarnings("unchecked")
        PersistentCollection<T> p = (PersistentCollection<T>)super.clone();
        p.oid = 0;
        p.state = EnumSet.noneOf(PersistenceState.class);
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

