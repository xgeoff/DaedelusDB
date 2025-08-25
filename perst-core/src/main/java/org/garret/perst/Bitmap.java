package org.garret.perst;

import java.io.*;
import java.util.*;

/**
 * Class used to merge results of multiple databases searches.
 * Each bit of bitmap corresponds to object OID.
 * and/or/xor method can be used to combine different bitmaps.
 */
public class Bitmap implements Iterable<Object>, Serializable
{
    class BitmapIterator implements Iterator<Object>, PersistentIterator
    { 
        public boolean hasNext()
        {
            return curr >= 0;
        }

        public Object next()
        {
            if (curr < 0) {
                throw new NoSuchElementException();
            }
            Object obj = storage.getObjectByOID(curr);
            prev = curr;
            curr = bitmap.nextSetBit(curr + 1);
            return obj;
        }

        public int nextOid()
        {
            if (curr < 0) {
                throw new NoSuchElementException();
            }
            int oid = curr;
            prev = curr;
            curr = bitmap.nextSetBit(curr + 1);
            return oid;
        }

        public void remove()
        {
            if (prev < 0) {
                throw new NoSuchElementException();
            }
            bitmap.clear(prev);
        }

        BitmapIterator()
        {
            curr = bitmap.nextSetBit(0);
            prev = -1;
        }

        int curr;
        int prev;
    };

    /**
     * Check if object with this OID is present in bitmap
     * @param oid object identifier
     * @return true if object is repsent in botmap, false otherwise
     */
    public boolean contains(int oid) {
        return oid < n_bits && bitmap.get(oid);
    }

    /** 
     * Get iterator through objects selected in bitmap
     * @return selected object iterator
     */
    public Iterator<Object> iterator()
    {
        return new BitmapIterator();
    }

    /**
     * Intersect (bit and) two bitmaps
     * @param other bitmaps which will be intersected with this one
     */
    public void and(Bitmap other) 
    { 
        bitmap.and(other.bitmap);
        if (n_bits > other.n_bits) {
            n_bits = other.n_bits;
        }
    }

    /**
     * Union (bit or) two bitmaps
     * @param other bitmaps which will be combined with this one
     */
    public void or(Bitmap other) 
    { 
        bitmap.or(other.bitmap);
        if (n_bits < other.n_bits) {
            n_bits = other.n_bits;
        }
    }

    /**
     * Exclusive OR (xor) of two bitmaps
     * @param other bitmaps which will be combined with this one
     */
    public void xor(Bitmap other) 
    { 
        bitmap.xor(other.bitmap);
        if (n_bits < other.n_bits) {
            n_bits = other.n_bits;
        }
    }

    /**
     * Constructor of bitmap
     * @param sto storage of persistent object selected by this bitmap
     * @param i iterator through persistent object which is used to initialize bitmap
     */
    public Bitmap(Storage sto, Iterator<?> i)
    { 
        storage = sto;
        n_bits = sto.getMaxOid();
        BitSet bm = new BitSet(n_bits);
        PersistentIterator pi = (PersistentIterator)i;
        int oid;
        while ((oid = pi.nextOid()) != 0) {
            bm.set(oid);
        }
        bitmap = bm;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(n_bits);
        long[] data = bitmap.toLongArray();
        out.writeInt(data.length);
        for (long w : data) {
            out.writeLong(w);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        n_bits = in.readInt();
        int len = in.readInt();
        long[] data = new long[len];
        for (int i = 0; i < len; i++) {
            data[i] = in.readLong();
        }
        bitmap = BitSet.valueOf(data);
    }

    transient Storage storage;
    BitSet bitmap;
    int n_bits;
}