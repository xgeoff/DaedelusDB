package org.garret.perst;

import org.junit.Test;
import static org.junit.Assert.*;

public class PatriciaTrieExampleTest {
    @Test
    public void prefixSearch() {
        Storage db = StorageFactory.getInstance().createStorage();
        db.open(new NullFile(), Storage.INFINITE_PAGE_POOL);
        try {
            PatriciaTrie<PersistentString> root = db.createPatriciaTrie();
            db.setRoot(root);

            root.add(PatriciaTrieKey.from8bitString("724885"), new PersistentString("ATT"));
            root.add(PatriciaTrieKey.from8bitString("72488547"), new PersistentString("BCC"));
            db.commit();

            assertEquals("ATT", root.findExactMatch(PatriciaTrieKey.from8bitString("724885")).toString());
            assertEquals("BCC", root.findExactMatch(PatriciaTrieKey.from8bitString("72488547")).toString());

            assertNull(root.findExactMatch(PatriciaTrieKey.from8bitString("123456")));
        } finally {
            db.close();
        }
    }
}
