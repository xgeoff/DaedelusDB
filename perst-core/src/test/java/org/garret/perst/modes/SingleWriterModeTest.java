package org.garret.perst.modes;

import org.garret.perst.*;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SingleWriterModeTest {
    static class Counter extends Persistent {
        int value;
    }

    @Test
    public void singleWriterIncrements() throws Exception {
        File dbFile = File.createTempFile("single_writer", ".dbs");
        dbFile.delete();
        Storage storage = StorageFactory.getInstance().createStorage();
        storage.open(dbFile.getPath());

        Counter counter = new Counter();
        storage.setRoot(counter);

        ExecutorService writer = Executors.newSingleThreadExecutor();
        int threads = 4;
        int iterations = 50;
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    writer.submit(() -> {
                        storage.beginThreadTransaction(Storage.EXCLUSIVE_TRANSACTION);
                        counter.value++;
                        storage.store(counter);
                        storage.commit();
                        storage.endThreadTransaction();
                    });
                }
                latch.countDown();
            }).start();
        }

        try {
            Assert.assertTrue("Worker threads did not finish in time", latch.await(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assert.fail("Interrupted while waiting for worker threads to finish");
        }
        writer.shutdown();
        writer.awaitTermination(10, TimeUnit.SECONDS);

        storage.beginThreadTransaction(Storage.SERIALIZABLE_TRANSACTION);
        Assert.assertEquals(threads * iterations, counter.value);
        storage.endThreadTransaction();
        storage.close();
        dbFile.delete();
    }
}
