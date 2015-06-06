package org.osgl.cache.impl;

import org.osgl.cache.CacheService;
import org.osgl.cache.CacheServiceProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by luog on 18/02/14.
 */
public class MemcachedServiceProvider implements CacheServiceProvider {

    public static MemcachedServiceProvider INSTANCE = new MemcachedServiceProvider();

    private static ConcurrentMap<String, CacheService> services = new ConcurrentHashMap<String, CacheService>();

    @Override
    public CacheService get() {
        return MemcachedService.getInstance();
    }

    @Override
    public CacheService get(String name) {
        return MemcachedService.getInstance();
    }
}
