package org.osgl.cache;

import org.osgl.cache.impl.SimpleCacheService;

/**
 * Created by luog on 22/02/14.
 */
public class SimpleCacheTest extends CacheTestBase {
    @Override
    protected CacheService cache() {
        return SimpleCacheService.INSTANCE;
    }
}
