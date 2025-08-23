package org.garret.perst;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class SimpleExampleTest {
    private static final String DB_FILE = "SimpleExampleTest.dbs";
    private static final int PAGE_POOL_SIZE = 32*1024*1024;

    private Storage db;
    private MyRootClass root;
    private MyPersistentClass obj;

    static class MyPersistentClass extends Persistent {
        public int intKey;
        public String strKey;
        public String body;

        @Override
        public String toString() {
            return intKey + ":" + strKey + ":" + body;
        }
    }

    static class MyRootClass extends Persistent {
        public FieldIndex<MyPersistentClass> intKeyIndex;
        public FieldIndex<MyPersistentClass> strKeyIndex;
        public Index<MyPersistentClass> foreignIndex;

        public MyRootClass(Storage db) {
            super(db);
            intKeyIndex = db.createFieldIndex(MyPersistentClass.class, "intKey", true);
            strKeyIndex = db.createFieldIndex(MyPersistentClass.class, "strKey", false);
            foreignIndex = db.createIndex(int.class, false);
        }

        public MyRootClass() {
        }
    }

    @Before
    public void setUp() {
        db = StorageFactory.getInstance().createStorage();
        db.open(DB_FILE, PAGE_POOL_SIZE);
        root = (MyRootClass) db.getRoot();
        if (root == null) {
            root = new MyRootClass(db);
            db.setRoot(root);
        }
        obj = new MyPersistentClass();
        obj.intKey = 1;
        obj.strKey = "A.B";
        obj.body = "Hello world";
        root.intKeyIndex.put(obj);
        root.strKeyIndex.put(obj);
        root.foreignIndex.put(new Key(1001), obj);
        db.commit();
    }

    @After
    public void tearDown() {
        if (db != null && db.isOpened()) {
            db.close();
        }
        new File(DB_FILE).delete();
    }

    @Test
    public void testSearchOperations() {
        MyPersistentClass byInt = root.intKeyIndex.get(new Key(1));
        assertNotNull(byInt);
        assertEquals(obj.strKey, byInt.strKey);

        ArrayList<MyPersistentClass> byStr = root.strKeyIndex.getList(new Key("A.B"), new Key("A.B"));
        assertEquals(1, byStr.size());
        assertEquals(obj, byStr.get(0));

        Iterator<MyPersistentClass> prefixIt = root.strKeyIndex.prefixIterator("A.");
        assertTrue(prefixIt.hasNext());
        assertEquals(obj, prefixIt.next());
        assertFalse(prefixIt.hasNext());

        ArrayList<MyPersistentClass> prefixList = root.strKeyIndex.prefixSearchList("A.B.C");
        assertEquals(1, prefixList.size());
        assertEquals(obj, prefixList.get(0));
    }

    @Test
    public void testRangeQuery() {
        Iterator<MyPersistentClass> it = root.foreignIndex.iterator(new Key(100, true), new Key(10000, false), Index.ASCENT_ORDER);
        assertTrue(it.hasNext());
        assertEquals(obj, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testUpdate() {
        root.intKeyIndex.remove(obj);
        obj.intKey = 2;
        root.intKeyIndex.put(obj);
        db.commit();
        assertNull(root.intKeyIndex.get(new Key(1)));
        assertEquals(obj, root.intKeyIndex.get(new Key(2)));
    }

    @Test
    public void testDeletion() {
        root.intKeyIndex.remove(obj);
        root.strKeyIndex.remove(obj);
        root.foreignIndex.remove(new Key(1001), obj);
        obj.deallocate();
        db.commit();

        assertNull(root.intKeyIndex.get(new Key(1)));
        assertTrue(root.strKeyIndex.getList(new Key("A.B"), new Key("A.B")).isEmpty());
        Iterator<MyPersistentClass> it = root.foreignIndex.iterator(new Key(100, true), new Key(10000, false), Index.ASCENT_ORDER);
        assertFalse(it.hasNext());
    }
}

