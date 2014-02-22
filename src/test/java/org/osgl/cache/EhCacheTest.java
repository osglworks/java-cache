package org.osgl.cache;

import org.osgl.cache.impl.EhCacheService;

/**
 * Created by luog on 22/02/14.
 */
public class EhCacheTest extends CacheTestBase {
    @Override
    protected CacheService cache() {
        return EhCacheService.INSTANCE;
    }
}
