package org.osgl.cache.impl;

import org.osgl.cache.CacheService;

abstract class CacheServiceBase implements CacheService {
    @Override
    public int incr(String key) {
        Object o = get(key);
        if (null == o) {
            put(key, 1);
            return 0;
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() + 1);
            return (Integer) o;
        }
        throw new IllegalStateException("Only int or long value support incr operation");
    }

    @Override
    public int incr(String key, int ttl) {
        Object o = get(key);
        if (null == o) {
            put(key, 1, ttl);
            return 0;
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() + 1, ttl);
            return (Integer) o;
        }
        throw new IllegalStateException("Only int or long value support incr operation");
    }

    @Override
    public int decr(String key) {
        Object o = get(key);
        if (null == o) {
            put(key, -1);
            return 0;
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() - 1);
            return (Integer)o;
        }
        throw new IllegalStateException("Only int or long value support decr operation");
    }

    @Override
    public int decr(String key, int ttl) {
        Object o = get(key);
        if (null == o) {
            put(key, -1, ttl);
            return 0;
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() - 1, ttl);
            return (Integer)o;
        }
        throw new IllegalStateException("Only int or long value support decr operation");
    }

}
