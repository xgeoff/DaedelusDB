/*
 * Created on Jan 24, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.garret.perst.aspectj;

/**
 * @author Patrick Morris-Suzuki
 *
 */

import org.garret.perst.*;
import java.util.EnumSet;

privileged public aspect PersistenceAspect {
    declare parents: AutoPersist extends IPersistent;

    pointcut notPerstCode(): !within(org.garret.perst.*) && !within(org.garret.perst.impl.*) && !within(org.garret.perst.aspectj.*);
    
    pointcut persistentMethod(): 
        execution(!static * ((Persistent+ && !Persistent) || (AutoPersist+ && !(StrictAutoPersist+) && !AutoPersist)).*(..))
        && !execution(void *.recursiveLoading());
                
    /*
     * Load object at the beginning of each instance mehtod of persistent capable object
     */         
    before(IPersistent t) : persistentMethod() && this(t) {
        t.load();
    }

    /*
     * Read access to fields of persistent object
     */ 
    before(StrictAutoPersist t): get(!transient !static * StrictAutoPersist+.*) && notPerstCode() && target(t)
    {
        t.load();
    }

    /*
     * Read access to fields of persistent object
     */ 
    before(StrictAutoPersist t): set(!transient !static * StrictAutoPersist+.*) && notPerstCode() && target(t) 
    {
        t.loadAndModify();
    }

    /*
     * Automatically notice modifications to any fields.
     */
    before(AutoPersist t):  set(!transient !static * (AutoPersist+ && !(StrictAutoPersist+)).*)
        && notPerstCode() && !withincode(*.new(..)) && target(t)  
    {
        t.modify();
    }
    
    public void AutoPersist.unassignOid() {
        oid = 0;
        state = EnumSet.of(PersistenceState.DELETED);
        storage = null;
    }

    public void AutoPersist.assignOid(Storage s, int o, boolean raw) {
        oid = o;
        storage = s;
        if (raw) {
            state.add(PersistenceState.RAW);
        } else {
            state.remove(PersistenceState.RAW);
        }
    }
    
    boolean around(AutoPersist me, Object other):
    execution(boolean AutoPersist+.equals(Object)) &&
        args(other) && target(me){
        if(other==null) return false;
        
        boolean isEqual;
        try{
            isEqual=proceed(me, other);
        } catch(ClassCastException excep){
                        if(!other.getClass().equals(me.getClass()))
                            return false;
                        else
                            throw excep;
        }
        if(!isEqual){
            if(other!=null && other instanceof IPersistent){
                if(((IPersistent)other).getOid()==me.oid) isEqual=true;
            }
        }
        return isEqual;
    }
    
    int around(AutoPersist me):
    execution(int AutoPersist+.hashCode()) && target(me){
        return me.oid;
    }
    
    public void AutoPersist.commit() {
        if (storage != null) { 
            storage.commit();
        }
    }
    
    public synchronized void AutoPersist.load() {
        if (oid != 0 && state.contains(PersistenceState.RAW)) {
            storage.loadObject(this);
        }
    }
    
    public synchronized void AutoPersist.loadAndModify() {
        load();
        modify();
    }

    public final boolean AutoPersist.isRaw() { 
        return state.contains(PersistenceState.RAW);
    } 
    
    public final boolean AutoPersist.isModified() { 
        return state.contains(PersistenceState.DIRTY);
    } 
    
    public final boolean AutoPersist.isDeleted() { 
        return state.contains(PersistenceState.DELETED);
    } 
    
    public final boolean AutoPersist.isPersistent() { 
        return oid != 0;
    }
    
    public void AutoPersist.makePersistent(Storage storage) { 
        if (oid == 0) { 
            storage.makePersistent(this);
        }
    }

    public void AutoPersist.store() {
        if (state.contains(PersistenceState.RAW)) {
            throw new StorageError(StorageError.ACCESS_TO_STUB);
        }
        if (storage != null) { 
            storage.storeObject(this);
            state.remove(PersistenceState.DIRTY);
        }
    }
    
    public void AutoPersist.modify() { 
        if (!state.contains(PersistenceState.DIRTY) && oid != 0) {
            if (state.contains(PersistenceState.RAW)) {
                throw new StorageError(StorageError.ACCESS_TO_STUB);
            }
            Assert.that(!state.contains(PersistenceState.DELETED));
            storage.modifyObject(this);
            state.add(PersistenceState.DIRTY);
        }
    }
    
    public final int AutoPersist.getOid() {
        return oid;
    }
    
    public void AutoPersist.deallocate() { 
        if (oid != 0) { 
            storage.deallocateObject(this);
        }
    }
    
    public boolean AutoPersist.recursiveLoading() {
        return false;
    }
    
    public final Storage AutoPersist.getStorage() {
        return storage;
    }
    
    public void AutoPersist.onLoad() {
    }

    public void AutoPersist.onStore() {
    }
    
    public void AutoPersist.invalidate() {
        state.remove(PersistenceState.DIRTY);
        state.add(PersistenceState.RAW);
    }
    
    public void AutoPersist.finalize() { 
        if (state.contains(PersistenceState.DIRTY) && oid != 0) {
            storage.storeFinalizedObject(this);
        }
        state = EnumSet.of(PersistenceState.DELETED);
    }
    
    public Object AutoPersist.clone() throws CloneNotSupportedException 
    { 
        throw new CloneNotSupportedException();
        /* Doen't work any more...
        Persistent p = (Persistent)super.clone();
        p.oid = 0;
        p.state = EnumSet.noneOf(PersistenceState.class);
        return p;
        */
    }

    public void AutoPersist.readExternal(java.io.ObjectInput s) throws java.io.IOException, ClassNotFoundException
    {
        oid = s.readInt();
    }

    public void AutoPersist.writeExternal(java.io.ObjectOutput s) throws java.io.IOException
    {
	s.writeInt(oid);
    }

    private transient Storage AutoPersist.storage;
    private transient int     AutoPersist.oid;
    private transient EnumSet<PersistenceState> AutoPersist.state = EnumSet.noneOf(PersistenceState.class);
}
