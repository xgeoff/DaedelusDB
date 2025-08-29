package org.garret.perst;

/**
 * Simple in-memory implementation of {@link IFile} used by tests.
 * All data written to this file is discarded and reads return zero
 * bytes.  The file has a fixed length of zero and locking operations
 * are no-ops.
 */
class NullFile implements IFile {
    public void write(long pos, byte[] buf) {
        // discard data
    }

    public int read(long pos, byte[] buf) {
        // behave as if requested number of bytes were read
        return buf.length;
    }

    public void sync() {
        // no-op
    }

    public boolean tryLock(boolean shared) {
        return true;
    }

    public void lock(boolean shared) {
        // no-op
    }

    public void unlock() {
        // no-op
    }

    public void close() {
        // no-op
    }

    public long length() {
        return 0;
    }
}
