package org.garret.perst.impl;

import org.garret.perst.*;

/**
 * Simple wrapper around {@link OidHashTable} providing a dedicated
 * component for object cache operations.  The cache implementation
 * is supplied by Perst and all calls are delegated to it.
 */
public class ObjectCache {
    private final OidHashTable cache;

    public ObjectCache(OidHashTable cache) {
        this.cache = cache;
    }

    public boolean remove(int oid) {
        return cache.remove(oid);
    }

    public void put(int oid, Object obj) {
        cache.put(oid, obj);
    }

    public Object get(int oid) {
        return cache.get(oid);
    }

    public void flush() {
        cache.flush();
    }

    public void invalidate() {
        cache.invalidate();
    }

    public void reload() {
        cache.reload();
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    public void setDirty(Object obj) {
        cache.setDirty(obj);
    }

    public void clearDirty(Object obj) {
        cache.clearDirty(obj);
    }
}

