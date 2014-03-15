package org.osgl.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by luog on 22/02/14.
 */
public abstract class CacheTestBase extends TestBase {

    protected abstract CacheService cache();

    @Before
    public void setup() {
        cache().shutdown();
        cache().setDefaultTTL(3);
        cache().startup();
    }


    @After
    public void teardown() {
        cache().shutdown();
    }

    @Test
    public void testPutGet() throws Exception {
        cache().put("key1", "val1", 5);
        assertEquals("val1", (cache().get("key1")));
        Thread.sleep(3000);
        assertEquals("val1", (cache().get("key1")));
        Thread.sleep(3000);
        assertEquals(null, cache().get("key1"));
    }

    @Test
    public void testRemove() throws Exception {
        cache().put("key1", "val1", 10);
        assertTrue("cached item does not match previous item", "val1".equals(cache().get("key1")));
        cache().evict("key1");
        assertTrue("removed cached item should not exists", null == cache().get("key1"));
    }

    @Test
    public void testRefreshTTL() throws Exception {
        cache().put("key1", "val1", 3);
        assertEquals("val1", cache().get("key1"));
        Thread.sleep(2000);
        assertEquals("val1", cache().get("key1"));
        Thread.sleep(1050);
        assertEquals(null, cache().get("key1"));
        logger.trace("*****************************************");
        cache().put("key1", "val2", 2);
        assertEquals("val2", cache().get("key1"));
        Thread.sleep(1200);
        assertEquals("val2", cache().get("key1"));
        Thread.sleep(900);
        logger.trace("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        assertEquals(null, cache().get("key1"));
    }

    @Test
    public void testExpireOrder() throws Exception {
        cache().put("k2", "v2", 2);
        cache().put("k3", "v3", 3);
        cache().put("k1", "v1", 1);
        Thread.sleep(1050);
        assertNull(cache().get("k1"));
        assertEquals("v2", cache().get("k2"));
        assertEquals("v3", cache().get("k3"));
        Thread.sleep(1000);
        assertNull(cache().get("k2"));
        assertEquals("v3", cache().get("k3"));
        Thread.sleep(1000);
        assertNull(cache().get("k3"));
    }
    
}
