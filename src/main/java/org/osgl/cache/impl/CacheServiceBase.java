package org.osgl.cache.impl;

import org.osgl.cache.CacheService;

abstract class CacheServiceBase implements CacheService {
    @Override
    public void incr(String key) {
        Object o = get(key);
        if (null == o) {
            put(key, 1);
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() + 1);
        } else if (o instanceof Long) {
            put(key, ((Long) o).longValue() + 1);
        }
        throw new IllegalStateException("Only int or long value support incr operation");
    }

    @Override
    public void incr(String key, int n) {
        Object o = get(key);
        if (null == o) {
            put(key, n);
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() + n);
        } else if (o instanceof Long) {
            put(key, ((Long) o).longValue() + n);
        }
        throw new IllegalStateException("Only int or long value support incr operation");
    }

    @Override
    public void decr(String key) {
        Object o = get(key);
        if (null == o) {
            put(key, -1);
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() - 1);
        } else if (o instanceof Long) {
            put(key, ((Long) o).longValue() - 1);
        }
        throw new IllegalStateException("Only int or long value support decr operation");
    }

    @Override
    public void decr(String key, int n) {
        Object o = get(key);
        if (null == o) {
            put(key, -n);
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() - n);
        } else if (o instanceof Long) {
            put(key, ((Long) o).longValue() - n);
        }
        throw new IllegalStateException("Only int or long value support decr operation");
    }

}
