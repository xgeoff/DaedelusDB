package org.garret.perst.impl;
import  org.garret.perst.*;
import  java.util.*;

class AltPersistentSet<T> extends AltBtree<T> implements IPersistentSet<T> { 
    AltPersistentSet() { 
        type = ClassDescriptor.tpObject;
        unique = true;
    }

    AltPersistentSet(boolean unique) { 
        type = ClassDescriptor.tpObject;
        this.unique = unique;
    }

    public boolean isEmpty() { 
        return nElems == 0;
    }

    public boolean contains(Object o) {
        Key key = new Key(o);
        Iterator<T> i = iterator(key, key, ASCENT_ORDER);
        return i.hasNext();
    }
    
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] arr) {
        Class<?> component = arr.getClass().getComponentType();
        if (component != Object.class && nElems > 0) {
            Iterator<T> it = iterator();
            if (it.hasNext() && !component.isInstance(it.next())) {
                throw new ArrayStoreException();
            }
        }
        return (E[])super.toArray(arr);
    }

    public boolean add(T obj) { 
        return put(new Key(obj), obj);
    }

    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        if (!(o instanceof IPersistent)) {
            return false;
        }
        T obj = (T)o;
        return removeIfExists(new BtreeKey(checkKey(new Key(obj)), obj));
    }
    
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        Collection<?> c = (Collection<?>) o;
        if (c.size() != size()) {
            return false;
        }
        return containsAll(c);
    }

    public int hashCode() {
        int h = 0;
        Iterator<T> i = iterator();
        while (i.hasNext()) {
            h += getStorage().getOid(i.next());
        }
        return h;
    }

    public IterableIterator<T> join(Iterator<T> with) { 
        return with == null ? (IterableIterator<T>)iterator() : new JoinSetIterator<T>(getStorage(), iterator(), with);
    }        
}
