package org.osgl.cache;

import java.io.Serializable;

/**
 * Created by luog on 22/02/14.
 */
public enum Cache {
    ;

    private static volatile CacheService c_;
    private static CacheService c() {
        if (null == c_) {
            synchronized (Cache.class) {
                if (null == c_) {
                    CacheServiceFactory fact = CacheServiceFactory.Impl.Auto;
                    c_ = fact.get();
                }
            }
        }
        return c_;
    }

    public static void put(String key, Serializable value, int ttl) {
        c().put(key, value, ttl);
    }

    public static void put(String key, Serializable value) {
        c().put(key, value);
    }

    public static void evict(String key) {
        c().evict(key);
    }


}
