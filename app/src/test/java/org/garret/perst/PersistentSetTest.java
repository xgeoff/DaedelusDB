/*
 * $URL: PersistentSetTest.java $ 
 * $Rev: 3582 $ 
 * $Date: 2007-11-25 14:29:06 +0300 (Вс., 25 нояб. 2007) $
 *
 * Copyright 2005 Netup, Inc. All rights reserved.
 * URL:    http://www.netup.biz
 * e-mail: info@netup.biz
 */

package org.garret.perst;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Iterator;

/**
 * These tests verifies an implementation of the <code>PersistentSet</code> interface. <br />
 * The implementation is created by the following way :
 * <pre>
 *   storage = org.garret.perst.StorageFactory.getInstance().createStorage()
 *   PersistentSet persistentSet = storage.createSet()
 * </pre>
 * <p>
 * In test are used simple <CODE>Persistent</CODE> class <CODE>Stored</CODE>:
 * <pre>
 *   class Stored extends Persistent {
 *       public String name;
 *   }
 *   class StoredEx extends Stored {
 *       public String ex;
 *   }
 * </pre>
 */
public class PersistentSetTest {
    Storage storage;
    IPersistentSet persistentSet;

    @Before
    public void setUp() throws Exception {
        storage = StorageFactory.getInstance().createStorage();
        storage.open(new NullFile(), Storage.INFINITE_PAGE_POOL);
        persistentSet = storage.createSet();
    }

    @After
    public void tearDown() throws Exception {
        if (storage.isOpened())
            storage.close();
    }

    /**
     * <B>Goal:</B> To verify the functionality of the <CODE>add()</CODE> method.
     * <P>
     * <B>Conditions:</B>
     * <ul>
     * <li><code>add(null)</code> are invoked.</li>
     * </ul>
     * <P>
     * <B>Result:</B>
     * <ul>
     * <li>no exceptions are thrown.</li>
     * </ul>
     */
    @Test
    public void test00() {
        assertTrue(persistentSet.add(null));
        assertTrue(persistentSet.contains(null));
    }

    /**
     * <B>Goal:</B> To verify the functionality of the <CODE>add()</CODE> method.
     * <P>
     * <B>Conditions:</B>
     * <ul>
     * <li>The <code>add(new Stored())</code> method is invoked.</li>
     * </ul>
     * <P>
     * <B>Result:</B>
     * <ul>
     * <li>no exceptions are thrown.</li>
     * </ul>
     */
    @Test
    public void test01(){
        persistentSet.add(new Stored());
    }

    /**
     * <B>Goal:</B> To verify the functionality of the <CODE>add()</CODE> method.
     * <P>
     * <B>Conditions:</B>
     * <ul>
     * <li>The <code>add(stored)</code> method is invoked.</li>
     * <li>The <code>remove(stored)</code> method is invoked.</li>
     * </ul>
     * <P>
     * <B>Result:</B>
     * <ul>
     * <li>no exceptions are thrown.</li>
     * </ul>
     */
    @Test
    public void test02() {
        Stored o = new Stored();
        persistentSet.add(o);
        persistentSet.remove(o);
    }

    /**
     * <B>Goal:</B> To verify the functionality of the <CODE>add()</CODE> method.
     * <P>
     * <B>Conditions:</B>
     * <ul>
     * <li>The <code>add(stored)</code> method is invoked.</li>
     * </ul>
     * <P>
     * <B>Result:</B>
     * <ul>
     * <li>no exceptions are thrown.</li>
     * <li><code>iterator()</code> returns added object.</li>
     * </ul>
     */
    @Test
    public void test03() {
        Stored o = new Stored();
        persistentSet.add(o);
        Iterator i = persistentSet.iterator();
        assertEquals(o, i.next());
        assertFalse(i.hasNext());
    }

    /**
     * <B>Goal:</B> To verify the functionality of the <CODE>add()</CODE> method.
     * <P>
     * <B>Conditions:</B>
     * <ul>
     * </ul>
     * <P>
     * <B>Result:</B>
     * <ul>
     * <li>no exceptions are thrown.</li>
     * <li><code>iterator()</code> returns empty set.</li>
     * </ul>
     */
    @Test
    public void test04() {
        Iterator i = persistentSet.iterator();
        assertFalse(i.hasNext());
    }

    /**
     * <B>Goal:</B> To verify the functionality of the <CODE>add()</CODE> method.
     * <P>
     * <B>Conditions:</B>
     * <ul>
     * <li>The <code>add(stored)</code> method is invoked.</li>
     * <li>The <code>remove(stored)</code> method is invoked.</li>
     * </ul>
     * <P>
     * <B>Result:</B>
     * <ul>
     * <li>no exceptions are thrown.</li>
     * <li><code>iterator()</code> returns empty set.</li>
     * </ul>
     */
    @Test
    public void test05() {
        Stored o = new Stored();
        persistentSet.add(o);
        persistentSet.remove(o);
        Iterator i = persistentSet.iterator();
        assertFalse(i.hasNext());
    }

    /**
     * <B>Goal:</B> To verify the functionality of the <CODE>add()</CODE> method.
     * <P>
     * <B>Conditions:</B>
     * <ul>
     * <li>The <code>add(stored)</code> method is invoked twice.</li>
     * </ul>
     * <P>
     * <B>Result:</B>
     * <ul>
     * <li>no exceptions are thrown.</li>
     * <li>the first invocation of the <code>add(...)</code> method returned <i>true</i> and
     *  the second invocation returned <i>false</i>.</li>
     * </ul>
     */
    @Test
    public void test06() {
        Stored o = new Stored();
        assertTrue(persistentSet.add(o));
        assertFalse(persistentSet.add(o));
        Iterator i = persistentSet.iterator();
        assertEquals(o, i.next());
        assertFalse(i.hasNext());
    }

    /**
     * <B>Goal:</B> To verify the functionality of the <CODE>add()</CODE> method.
     * <P>
     * <B>Conditions:</B>
     * <ul>
     * <li>The <code>add(stored)</code> method is invoked.</li>
     * <li>The <code>remove(stored)</code> method is invoked twice.</li>
     * </ul>
     * <P>
     * <B>Result:</B>
     * <ul>
     * <li>no exceptions are thrown.</li>
     * <li>the first invocation of <code>remove(...)</code> method returned <i>true</i> and
     * the second invocation returned <i>false</i>.</li>
     * </ul>
     */
    @Test
    public void test07() {
        Stored o = new Stored();
        persistentSet.add(o);
        assertTrue(persistentSet.remove(o));
        assertFalse(persistentSet.remove(o));
    }

    /**
     * <B>Goal:</B> To verify the functionality of the <CODE>add()</CODE> method.
     * <P>
     * <B>Conditions:</B>
     * <ul>
     * <li>The <code>add(...)</code> method is invoked five times(five objects added to storage).</li>
     * </ul>
     * <P>
     * <B>Result:</B>
     * <ul>
     * <li>no exceptions are thrown.</li>
     * <li><code>getRecords(...)</code> returned all five added objects.</li>
     * </ul>
     */
    @Test
    public void test08() {
        Stored[] arr = {new Stored(), new Stored(), new Stored(), new Stored(), new Stored()};
        for(Stored s: arr){
            persistentSet.add(s);
        }
        Iterator it = persistentSet.iterator();
        while(it.hasNext()){
            Object o = it.next();
            assertNotNull(o);
            boolean found = false;
            for(int i=0; i<arr.length; i++){
                if(arr[i] == o){
                    arr[i] = null;
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    /**
     * <B>Goal:</B> To verify the functionality of the <CODE>add()</CODE>  method.
     * <P>
     * <B>Conditions:</B>
     * <ul>
     * <li>The <code>add(...)</code> method is invoked five times(two base objects added to the storage
     * and three derived objects added to the storage).</li>
     * </ul>
     * <P>
     * <B>Result:</B>
     * <ul>
     * <li>no exceptions are thrown.</li>
     * <li><code>getsRecords(...)</code> returned all five added objects.</li>
     * </ul>
     */
    @Test
    public void test09() {
        Stored[] arr = {new StoredEx(), new Stored(), new StoredEx(), new Stored(), new StoredEx()};
        for (Stored s : arr) {
            persistentSet.add(s);
        }
        Iterator it = persistentSet.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            assertNotNull(o);
            boolean found = false;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == o) {
                    arr[i] = null;
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }


    /**
     * Internal test class.
     */
    private static class Stored extends Persistent {
        public String name;
        public Stored(String name) {
            this.name = name;
        }

        public Stored() {}
    }
    /**
     * Internal test class.
     */
    private static class StoredEx extends Stored {
        public String ex;

        public StoredEx(String name) {
            this.name = name;
        }
        public StoredEx() {
        }
    }
}
