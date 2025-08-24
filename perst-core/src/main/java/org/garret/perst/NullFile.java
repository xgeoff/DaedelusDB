package org.garret.perst;

import java.nio.ByteBuffer;

/**
 * Stub implementation of {@link IFile} which keeps all data in memory. This
 * implementation uses {@link ByteBuffer} to emphasise NIO based design even
 * though all operations are effectively no-ops.
 */
public class NullFile implements IFile {
    private static final ByteBuffer EMPTY = ByteBuffer.allocate(0);

    public void write(long pos, byte[] buf) {
        // nothing to store, but touch buffer through NIO API
        ByteBuffer.wrap(buf);
    }

    public int read(long pos, byte[] buf) {
        // always return EOF
        return 0;
    }

    public void sync() {}

    public boolean tryLock(boolean shared) {
        return true;
    }

    public void lock(boolean shared) {}

    public void unlock() {}

    public void close() {}

    public long length() {
        return EMPTY.capacity();
    }
}
