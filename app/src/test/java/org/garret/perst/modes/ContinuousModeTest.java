package org.garret.perst.modes;

import org.garret.perst.*;
import org.garret.perst.continuous.*;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class ContinuousModeTest {
    static class Counter extends CVersion {
        @Indexable int id;
        int value;
    }

    @Test
    public void continuousIncrements() throws Exception {
        File dbFile = File.createTempFile("continuous", ".dbs");
        dbFile.delete();
        Storage storage = StorageFactory.getInstance().createStorage();
        storage.open(dbFile.getPath());
        CDatabase db = new CDatabase();
        db.open(storage, null);

        db.beginTransaction();
        Counter counter = new Counter();
        counter.id = 1;
        db.insert(counter);
        db.commitTransaction();

        int threads = 4;
        int iterations = 50;
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    boolean committed = false;
                    while (!committed) {
                        db.beginTransaction();
                        Counter c = db.getSingleton(db.<Counter>find(Counter.class, "id", new Key(1))).update();
                        c.value++;
                        try {
                            db.commitTransaction();
                            committed = true;
                        } catch (ConflictException e) {
                            db.rollbackTransaction();
                        }
                    }
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        db.beginTransaction();
        Counter c = db.getSingleton(db.<Counter>find(Counter.class, "id", new Key(1)));
        Assert.assertEquals(threads * iterations, c.value);
        db.commitTransaction();
        db.close();
        dbFile.delete();
    }
}
