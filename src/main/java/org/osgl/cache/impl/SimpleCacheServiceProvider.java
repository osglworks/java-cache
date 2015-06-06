package org.osgl.cache.impl;

import org.osgl.cache.CacheService;
import org.osgl.cache.CacheServiceProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by luog on 23/02/14.
 */
public class SimpleCacheServiceProvider implements CacheServiceProvider {

    public static SimpleCacheServiceProvider INSTANCE = new SimpleCacheServiceProvider();

    private static ConcurrentMap<String, CacheService> services = new ConcurrentHashMap<String, CacheService>();

    @Override
    public CacheService get() {
        return get(CacheService.DEF_CACHE_NAME);
    }

    @Override
    public CacheService get(String name) {
        CacheService cs = services.get(name);
        if (null == cs) {
            cs = new SimpleCacheService(name);
            services.putIfAbsent(name, cs);
        }
        return cs;
    }
}
