import org.garret.perst.*;
import org.garret.perst.impl.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.nio.file.*;

public class MultiFileStorageTest {
    static class BigObject extends Persistent {
        byte[] data;
    }

    @Test
    public void testMultiFileStorage() throws Exception {
        Path dir = Files.createTempDirectory("perst-multi");
        Path seg1 = dir.resolve("seg1.dbs");
        Path seg2 = dir.resolve("seg2.dbs");
        String[] paths = {seg1.toString(), seg2.toString()};
        long[] sizes = {Page.pageSize * 4L, Page.pageSize * 4L};
        IFile file = new MultiFile(paths, sizes, false, false);

        Storage storage = StorageFactory.getInstance().createStorage();
        storage.open(file, 1024 * 1024);
        BigObject obj = new BigObject();
        obj.data = new byte[Page.pageSize * 10];
        storage.setRoot(obj);
        storage.commit();
        storage.close();

        assertTrue(Files.size(seg2) > 0);

        IFile reopen = new MultiFile(paths, sizes, false, false);
        storage = StorageFactory.getInstance().createStorage();
        storage.open(reopen, 1024 * 1024);
        BigObject loaded = (BigObject) storage.getRoot();
        assertNotNull(loaded);
        assertEquals(Page.pageSize * 10, loaded.data.length);
        storage.close();
    }
}
