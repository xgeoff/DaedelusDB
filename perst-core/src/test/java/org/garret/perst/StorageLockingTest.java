package org.garret.perst;

import static org.garret.perst.Storage.INFINITE_PAGE_POOL;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.garret.perst.impl.LockManager;
import org.garret.perst.impl.StorageImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for storage-level locking and writer delegation.
 */
public class StorageLockingTest {

    private StorageImpl storage;

    @Before
    public void setUp() {
        storage = (StorageImpl) StorageFactory.getInstance().createStorage();
        storage.open(new NullFile(), INFINITE_PAGE_POOL);
        storage.setRoot(new Root());
        storage.commit();
    }

    @After
    public void tearDown() {
        if (storage != null && storage.isOpened()) {
            storage.close();
        }
    }

    /**
     * Start multiple reader threads attempting to access the same object while a writer
     * thread holds the write lock. Readers should block until the writer releases the lock.
     */
    @Test
    public void testReadersBlockDuringWrite() throws Exception {
        final Root root = (Root) storage.getRoot();
        final int oid = storage.getOid(root);

        // Access the lock manager via reflection
        Field f = StorageImpl.class.getDeclaredField("lockManager");
        f.setAccessible(true);
        final LockManager lm = (LockManager) f.get(storage);

        final CountDownLatch writerLocked = new CountDownLatch(1);
        final CountDownLatch writerDone = new CountDownLatch(1);
        final CountDownLatch readersReady = new CountDownLatch(2);
        Thread writer = new Thread(new Runnable() {
            public void run() {
                lm.acquireWrite(oid);
                writerLocked.countDown();
                try {
                    root.i = 1; // simulate write
                    Thread.sleep(500);
                } catch (InterruptedException ignore) {
                } finally {
                    lm.releaseWrite(oid);
                    writerDone.countDown();
                }
            }
        });

        final List<Long> readerDelays = new ArrayList<Long>();
        Thread reader1 = new Thread(new Reader(root, oid, lm, writerLocked, readersReady, readerDelays));
        Thread reader2 = new Thread(new Reader(root, oid, lm, writerLocked, readersReady, readerDelays));
        reader1.start();
        reader2.start();

        // Ensure both readers are waiting before starting the writer
        readersReady.await();
        writer.start();

        writer.join();
        reader1.join();
        reader2.join();

        assertEquals(2, readerDelays.size());
        for (long d : readerDelays) {
            assertTrue("Readers should wait for writer to finish", d >= 400);
        }
        assertEquals(1, root.i);
    }

    private static class Reader implements Runnable {
        private final Root root;
        private final int oid;
        private final LockManager lm;
        private final CountDownLatch latch;
        private final CountDownLatch ready;
        private final List<Long> delays;

        Reader(Root root, int oid, LockManager lm, CountDownLatch latch, CountDownLatch ready, List<Long> delays) {
            this.root = root;
            this.oid = oid;
            this.lm = lm;
            this.latch = latch;
            this.ready = ready;
            this.delays = delays;
        }

        public void run() {
            try {
                ready.countDown();
                latch.await();
                long start = System.currentTimeMillis();
                storageCheckReadLock(root, oid, lm);
                long elapsed = System.currentTimeMillis() - start;
                synchronized (delays) {
                    delays.add(elapsed);
                }
            } catch (InterruptedException ignore) {
            }
        }

        private void storageCheckReadLock(Root root, int oid, LockManager lm) {
            // Use StorageImpl.checkReadLock via the lock manager directly
            // Equivalent to storage.checkReadLock(oid) but without reference to outer class
            if (lm.isWriteLocked(oid)) {
                lm.acquireRead(oid);
                lm.releaseRead(oid);
            }
        }
    }

    /**
     * When a non-writer thread attempts to store an object, the operation should be
     * delegated to the dedicated writer thread.
     */
    @Test
    public void testNonWriterWriteDelegated() throws Exception {
        storage.startWriterThread();
        final Root root = (Root) storage.getRoot();
        final RecordingListener listener = new RecordingListener();
        storage.setListener(listener);

        Thread t = new Thread(new Runnable() {
            public void run() {
                root.i = 2;
                root.store();
            }
        });
        t.start();
        t.join();

        // Wait up to 1 second for store to be processed
        for (int i = 0; i < 100 && listener.storeThread == null; i++) {
            Thread.sleep(10);
        }
        assertNotNull("Store should be delegated to writer thread", listener.storeThread);
        assertNotSame("Non-writer thread should not perform store", t, listener.storeThread);
    }

    private static class RecordingListener extends StorageListener {
        volatile Thread storeThread;
        public void onObjectStore(Object obj) {
            storeThread = Thread.currentThread();
        }
    }

    /** Simple persistent class used in the tests. */
    private static class Root extends Persistent {
        int i;
    }
}
