package org.garret.perst.impl;

import java.util.*;
import org.garret.perst.*;

/**
 * This class store transaction context associated with thread.
 * Content of this class is opaque for application, but it can use 
 * this context to share the single transaction between multiple threads
 */
public class ThreadTransactionContext {
    int             nested;
    IdentityHashMap<IResource,IResource> locked = new IdentityHashMap<IResource,IResource>();
    ArrayList<IPersistent>       modified = new ArrayList<IPersistent>();
    ArrayList<IPersistent>       deleted = new ArrayList<IPersistent>();
    /**
     * Timestamp of transaction start.
     */
    public long     startTimestamp;
    /**
     * Timestamp of transaction commit. Zero if transaction is not yet committed
     * or was rolled back.
     */
    public long     commitTimestamp;
}

