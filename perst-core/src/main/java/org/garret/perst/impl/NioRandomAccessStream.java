package org.garret.perst.impl;

import org.garret.perst.RandomAccessInputStream;
import org.garret.perst.RandomAccessOutputStream;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Random access streams implemented using {@link FileChannel} and
 * {@link MappedByteBuffer}. These streams provide high throughput I/O
 * leveraging Java NIO facilities.
 */
public class NioRandomAccessStream {
    /** Input stream backed by a {@link FileChannel}. */
    public static class Input extends RandomAccessInputStream {
        private final FileChannel channel;
        private long position;
        private final long size;

        public Input(Path path) throws IOException {
            channel = FileChannel.open(path, StandardOpenOption.READ);
            size = channel.size();
        }

        @Override
        public int read() throws IOException {
            byte[] one = new byte[1];
            return read(one, 0, 1) == -1 ? -1 : (one[0] & 0xFF);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (position >= size) {
                return -1;
            }
            int avail = (int) Math.min(len, size - position);
            MappedByteBuffer bb = channel.map(FileChannel.MapMode.READ_ONLY, position, avail);
            bb.get(b, off, avail);
            position += avail;
            return avail;
        }

        @Override
        public long setPosition(long newPos) {
            position = newPos;
            return position;
        }

        @Override
        public long getPosition() {
            return position;
        }

        @Override
        public long size() {
            return size;
        }

        @Override
        public void close() throws IOException {
            channel.close();
        }
    }

    /** Output stream backed by a {@link FileChannel}. */
    public static class Output extends RandomAccessOutputStream {
        private final FileChannel channel;
        private long position;

        public Output(Path path) throws IOException {
            channel = FileChannel.open(path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.READ,
                    StandardOpenOption.WRITE);
        }

        @Override
        public void write(int b) throws IOException {
            byte[] one = new byte[1];
            one[0] = (byte) b;
            write(one, 0, 1);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            MappedByteBuffer bb = channel.map(FileChannel.MapMode.READ_WRITE, position, len);
            bb.put(b, off, len);
            position += len;
        }

        @Override
        public long setPosition(long newPos) {
            position = newPos;
            return position;
        }

        @Override
        public long getPosition() {
            return position;
        }

        @Override
        public long size() {
            try {
                return channel.size();
            } catch (IOException e) {
                return -1;
            }
        }

        @Override
        public void close() throws IOException {
            channel.close();
        }
    }
}
