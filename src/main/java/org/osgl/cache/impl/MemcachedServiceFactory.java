package org.osgl.cache.impl;

import org.osgl.cache.CacheService;
import org.osgl.cache.CacheServiceFactory;
import org.osgl.util.E;

import java.io.IOException;

/**
 * Created by luog on 18/02/14.
 */
public class MemcachedServiceFactory implements CacheServiceFactory {
    @Override
    public CacheService get() {
        try {
            return MemcachedService.getInstance();
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }
}
