package org.osgl.cache.impl;

import org.osgl.cache.CacheService;
import org.osgl.cache.CacheServiceFactory;

/**
 * Created by luog on 18/02/14.
 */
public class MemcachedServiceFactory implements CacheServiceFactory {
    @Override
    public CacheService get() {
        return MemcachedService.getInstance();
    }
}
