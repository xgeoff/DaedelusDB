import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Simple benchmark comparing legacy {@link RandomAccessFile} I/O with
 * {@link FileChannel} and {@link MappedByteBuffer} based operations.
 */
public class NioBenchmark {
    private static final int PAGE = 4096;
    private static final int PAGES = 1000;

    public static void main(String[] args) throws Exception {
        byte[] data = new byte[PAGE];
        // legacy
        long start = System.currentTimeMillis();
        try (RandomAccessFile raf = new RandomAccessFile("legacy.dat", "rw")) {
            for (int i = 0; i < PAGES; i++) {
                raf.seek((long) i * PAGE);
                raf.write(data);
            }
            raf.getFD().sync();
        }
        long legacy = System.currentTimeMillis() - start;

        // NIO
        start = System.currentTimeMillis();
        Path path = Path.of("nio.dat");
        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            MappedByteBuffer buf = ch.map(FileChannel.MapMode.READ_WRITE, 0, (long) PAGE * PAGES);
            for (int i = 0; i < PAGES; i++) {
                buf.position(i * PAGE);
                buf.put(data);
            }
            buf.force();
        }
        long nio = System.currentTimeMillis() - start;

        System.out.println("Legacy write ms: " + legacy);
        System.out.println("NIO write ms: " + nio);

        Files.deleteIfExists(Path.of("legacy.dat"));
        Files.deleteIfExists(Path.of("nio.dat"));
    }
}
