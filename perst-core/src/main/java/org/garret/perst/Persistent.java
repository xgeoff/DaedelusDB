package org.garret.perst;

/**
 * Base class for all persistent capable objects
 */
public class Persistent extends PinnedPersistent
{ 
    public Persistent() {}

    public Persistent(Storage storage) { 
        super(storage);
    }

    // Cleanup of persistent objects is now managed explicitly; finalization has been removed
}





