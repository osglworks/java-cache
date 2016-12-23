package org.osgl.cache;

import org.junit.Ignore;

@Ignore
public class MemcachedTest extends CacheTestBase {
    @Override
    protected CacheService cache() {
        System.setProperty("memcached.host", "127.0.0.1");
        return CacheServiceProvider.Impl.Memcached.get();
    }
}
