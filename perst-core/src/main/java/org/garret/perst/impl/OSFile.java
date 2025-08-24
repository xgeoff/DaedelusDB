package org.garret.perst.impl;
import  org.garret.perst.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Implementation of {@link IFile} based on {@link FileChannel} and
 * {@link java.nio.MappedByteBuffer}. All I/O operations are performed using
 * NIO buffers which provides better throughput than classic
 * {@link java.io.RandomAccessFile} based implementation.
 */
public class OSFile implements IFile {

    public void write(long pos, byte[] buf) {
        try {
            MappedByteBufferWrapper mapped = map(pos, buf.length, true);
            mapped.buffer.put(buf);
            if (!noFlush) {
                mapped.buffer.force();
            }
        } catch (IOException x) {
            throw new StorageError(StorageError.FILE_ACCESS_ERROR, x);
        }
    }

    public int read(long pos, byte[] buf) {
        try {
            long size = channel.size();
            if (pos >= size) {
                return 0;
            }
            int len = (int)Math.min(buf.length, size - pos);
            MappedByteBufferWrapper mapped = map(pos, len, false);
            mapped.buffer.get(buf, 0, len);
            return len;
        } catch (IOException x) {
            throw new StorageError(StorageError.FILE_ACCESS_ERROR, x);
        }
    }

    public void sync() {
        if (!noFlush) {
            try {
                channel.force(true);
            } catch (IOException x) {
                throw new StorageError(StorageError.FILE_ACCESS_ERROR, x);
            }
        }
    }

    public void close() {
        try {
            channel.close();
        } catch (IOException x) {
            throw new StorageError(StorageError.FILE_ACCESS_ERROR, x);
        }
    }

    public boolean tryLock(boolean shared) {
        try {
            lck = channel.tryLock(0, Long.MAX_VALUE, shared);
            return lck != null;
        } catch (IOException x) {
            return true;
        }
    }

    public void lock(boolean shared) {
        try {
            lck = channel.lock(0, Long.MAX_VALUE, shared);
        } catch (IOException x) {
            throw new StorageError(StorageError.LOCK_FAILED, x);
        }
    }

    public void unlock() {
        try {
            if (lck != null) {
                lck.release();
            }
        } catch (IOException x) {
            throw new StorageError(StorageError.LOCK_FAILED, x);
        }
    }

    /**
     * Map region of file starting at given position. Wrapper class is used to
     * work around lack of AutoCloseable for MappedByteBuffer.
     */
    private MappedByteBufferWrapper map(long pos, int size, boolean write) throws IOException {
        return new MappedByteBufferWrapper(
            channel.map(write ? FileChannel.MapMode.READ_WRITE : FileChannel.MapMode.READ_ONLY, pos, size));
    }

    /**
     * Construct file backed by {@link FileChannel}.
     */
    public OSFile(String filePath, boolean readOnly, boolean noFlush) {
        this.noFlush = noFlush;
        try {
            channel = FileChannel.open(Path.of(filePath), readOnly
                    ? StandardOpenOption.READ
                    : StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        } catch (IOException x) {
            throw new StorageError(StorageError.FILE_ACCESS_ERROR, x);
        }
    }

    public long length() {
        try {
            return channel.size();
        } catch (IOException x) {
            return -1;
        }
    }

    private static class MappedByteBufferWrapper {
        final java.nio.MappedByteBuffer buffer;

        MappedByteBufferWrapper(java.nio.MappedByteBuffer buffer) {
            this.buffer = buffer;
        }
    }

    protected FileChannel channel;
    protected boolean    noFlush;
    private FileLock     lck;
}
