package org.osgl.cache;

import java.io.Serializable;

/**
 * The Cache service static facade which delegate the operations
 * to the default cache service created by auto discovered cache
 * service factory
 */
public enum Cache {
    ;

    private static volatile CacheService c_;
    private static CacheService c() {
        if (null == c_) {
            synchronized (Cache.class) {
                if (null == c_) {
                    CacheServiceProvider fact = CacheServiceProvider.Impl.Auto;
                    c_ = fact.get();
                }
            }
        }
        return c_;
    }

    public static void startup() {
        c().startup();
    }

    public static void shutdown() {
        c().shutdown();
    }

    public static void put(String key, Serializable value, int ttl) {
        c().put(key, value, ttl);
    }

    public static void put(String key, Serializable value) {
        c().put(key, value);
    }

    public static Object get(String key) {
        return c().get(key);
    }

    public static void evict(String key) {
        c().evict(key);
    }


}
