package org.osgl.cache;

/**
 * Created by luog on 22/02/14.
 */
public class SimpleCacheTest extends CacheTestBase {
    @Override
    protected CacheService cache() {
        return CacheServiceProvider.Impl.Simple.get();
    }
}
