/*
 * $URL: StorageFactoryTest.java $ 
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

/**
 * These tests verifies an functionality of the <code>StorageFactory</code> class.
 */
public class StorageFactoryTest {

    /**
     * Verifies that a <CODE>createStorage()</CODE> method invocation returns a
     * not-<CODE>null</CODE> object.
     */
    @Test
    public void testCreateStorage() {
        Storage storage = StorageFactory.getInstance().createStorage();
        assertNotNull(storage);
    }

    /**
     * Verifies that a <CODE>createStorage()</CODE> method invocation returns different values
     * in sequential  calls.
     */
    @Test
    public void testCreateTwice() {
        Storage storage0 = StorageFactory.getInstance().createStorage();
        Storage storage1 = StorageFactory.getInstance().createStorage();
        assertNotNull(storage0);
        assertNotNull(storage1);
        assertNotSame(storage0, storage1);
    }

}
