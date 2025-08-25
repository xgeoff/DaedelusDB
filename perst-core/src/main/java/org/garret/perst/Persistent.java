package org.garret.perst;

import org.garret.perst.impl.StorageImpl;
import java.util.EnumSet;

/**
 * Base class for all persistent capable objects.
 * Objects extending this class are treated as pinned and are managed explicitly.
 */
public class Persistent implements IPersistent, ICloneable, Pinned {
    /**
     * Load the object's state from storage.
     * This call blocks if another thread currently holds a write lock
     * on the object.
     */
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

    public Persistent() {}

    public Persistent(Storage storage) {
        this.storage = storage;
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

    public void onLoad() {
    }

    public void onStore() {
    }

    public void invalidate() {
        state.remove(PersistenceState.DIRTY);
        state.add(PersistenceState.RAW);
    }

    transient Storage storage;
    transient int     oid;
    transient EnumSet<PersistenceState> state = EnumSet.noneOf(PersistenceState.class);

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
        Persistent p = (Persistent)super.clone();
        p.oid = 0;
        p.state = EnumSet.noneOf(PersistenceState.class);
        return p;
    }

    public void readExternal(java.io.ObjectInput s) throws java.io.IOException, ClassNotFoundException
    {
        oid = s.readInt();
    }

    public void writeExternal(java.io.ObjectOutput s) throws java.io.IOException
    {
        if (s instanceof StorageImpl.PersistentObjectOutputStream) {
            makePersistent(((StorageImpl.PersistentObjectOutputStream)s).getStorage());
        }
        s.writeInt(oid);
    }
}
