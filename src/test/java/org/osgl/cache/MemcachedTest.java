package org.osgl.cache;

/**
 * Created by luog on 22/02/14.
 */
public class MemcachedTest extends CacheTestBase {
    @Override
    protected CacheService cache() {
        System.setProperty("memcached.host", "127.0.0.1");
        return CacheServiceProvider.Impl.Memcached.get();
    }
}
